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

package org.mule.galaxy.web.client.registry;

import org.mule.galaxy.web.client.Galaxy;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Set;

public class SearchPanel extends AbstractBrowsePanel {

    private FlowPanel searchPanel;
    private SearchForm searchForm;
    
    public SearchPanel(Galaxy galaxy) {
        super(galaxy);
    }

    protected RegistryMenuPanel createRegistryMenuPanel() {
        return new RegistryMenuPanel(true, false);
    }
    
    protected void initializeMenuAndTop() {
        FlowPanel browseToolbar = new FlowPanel();
        browseToolbar.setStyleName("toolbar");
        
        searchPanel = new FlowPanel(); 
        searchForm = new SearchForm(galaxy);
        searchPanel.add(searchForm);
        currentTopPanel = searchPanel;
        menuPanel.setTop(searchPanel);
        
        searchForm.addSearchListener(new ClickListener() {
            public void onClick(Widget arg0) {
                refreshArtifacts();
            }
        });
    }

    public String getFreeformQuery() {
        return searchForm.getFreeformQuery();
    }

    public Set getPredicates() {
        return searchForm.getPredicates();
    }
    
    
}
