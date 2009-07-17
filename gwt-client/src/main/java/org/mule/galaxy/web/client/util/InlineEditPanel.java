/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web.client.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;

/**
 * A panel for editing the name of a registry entry.
 */
public abstract class InlineEditPanel extends Composite {

    private InlineFlowPanel panel;
    private ErrorPanel errorPanel;
    private Button saveButton;
    private Button cancelButton;
    
    public InlineEditPanel(ErrorPanel errorPanel) {
        super();
        this.errorPanel = errorPanel;
        
        panel = new InlineFlowPanel();
        showDisplayPanel();
        
        initWidget(panel);
    }

    protected void showDisplayPanel() {
        panel.clear();
        panel.add(createDisplayWidget());
        panel.add(new Label(" "));

        Image editImg = new Image("images/page_edit.gif");
        editImg.setStyleName("icon-baseline");
        editImg.setTitle("Edit");
        editImg.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent arg0) {
                showEditPanel();
            }
            
        });
        panel.add(editImg);
    }

    protected abstract Widget createDisplayWidget();
    
    protected abstract Widget createEditWidget();

    protected void showEditPanel() {
        panel.clear();

        final HorizontalPanel row = new HorizontalPanel();
        row.add(createEditWidget());
        
        saveButton = new Button("Save");
        saveButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent arg0) {
                setEnabled(false);
                doSave(getSaveCallback());
            }
            
        });

        cancelButton = new Button("Cancel");
        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                cancel();
                showDisplayPanel();
            }
        });

        row.add(saveButton);
        row.add(cancelButton);
        row.setVerticalAlignment(HasAlignment.ALIGN_TOP);
        panel.add(row);
    }

    protected AsyncCallback getSaveCallback() {
        return new AbstractCallback(errorPanel) {

            @Override
            public void onFailure(Throwable caught) {
                setEnabled(true);
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
                finishSave();
            }
            
        };
    }

    protected void finishSave() {                
        setEnabled(true);
        showDisplayPanel();
    }

    public void setEnabled(boolean e) {
        cancelButton.setEnabled(e);
        saveButton.setEnabled(e);
    }
    
    protected abstract void doSave(AsyncCallback asyncCallback);

    protected void cancel() {
    }
}