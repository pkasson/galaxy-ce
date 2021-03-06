/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
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

package org.mule.galaxy.repository.client.item;

import java.util.ArrayList;
import java.util.List;

import org.mule.galaxy.repository.client.RepositoryModule;
import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.web.client.ui.panel.AbstractShowableContainer;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;


/**
 * Contains:
 * - BasicArtifactInfo
 * - Service dependencies
 * - Depends on...
 * - Comments
 * - Governance tab
 * (with history)
 * - View Artiact
 */
public class ItemPanel extends AbstractShowableContainer {

    private ItemInfo info;
    private String itemId;
    private List<String> params;
    private RepositoryMenuPanel menuPanel;
    private RepositoryModule repository;
    private ContentPanel contentPanel;

    public ItemPanel(RepositoryMenuPanel menuPanel) {
        this.repository = menuPanel.getRepositoryModule();
        this.menuPanel = menuPanel;
    }

    @Override
    public void showPage(List<String> params) {
        this.params = params;
        removeAll();
    }

    public void initializeItem(ItemInfo info) {
        this.info = info;
        
        if (info.isArtifact() || info.isWorkspace()) {
            initCollection();
        } else {
            initArtifactVersion();
        }
        
        layout(true);
    }

    private void initArtifactVersion() {
        ArtifactVersionPanel versionPanel = new ArtifactVersionPanel(menuPanel, info, this, params);
        add(versionPanel);
    }
    
    private void initCollection() {
        contentPanel = new ContentPanel();
        contentPanel.setAutoHeight(true);
        contentPanel.setAutoWidth(true);
        contentPanel.setBodyBorder(false);
        contentPanel.addStyleName("x-panel-container-full");
        
        Image editImg = new Image("images/page_edit.gif");
        editImg.setStyleName("icon-baseline");
        editImg.setTitle("Edit");
        editImg.addClickHandler(new ClickHandler() {
            
            public void onClick(ClickEvent arg0) {
                Window window = new Window() {

                    @Override
                    public void hide() {
                        ModelData selected = menuPanel.getTree().getSelectionModel().getSelectedItem();
                        if (selected != null) {
                            selected.set("name", info.getName());
                        }

                        super.hide();
                    }
                    
                };
                window.add(new NameEditPanel(menuPanel, info, window));
                window.show();
            }
        });
        contentPanel.getHeader().addTool(new WidgetComponent(editImg));
        
        AbstractShowableContainer panel;
        if (info.isArtifact()) {
            panel = createArtifactPanel();
        } else if (info.isWorkspace()) {
            panel = createWorkspacePanel();
        } else {
            throw new IllegalStateException();
        }
        contentPanel.add(panel);
        add(contentPanel);
        panel.showPage(new ArrayList<String>());
        contentPanel.layout();
    }

    protected ArtifactPanel createArtifactPanel() {
        return repository.createArtifactPanel(info, this);
    }

    protected WorkspacePanel createWorkspacePanel() {
        return repository.createWorkspacePanel(info, this);
    }

    public String getItemId() {
        return itemId;
    }

    protected void setHeading(String s) {
        this.contentPanel.setHeading(s);
    }


}