package org.mule.galaxy.web.client.ui.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.PageInfo;
import org.mule.galaxy.web.client.PageManager;
import org.mule.galaxy.web.client.ui.NavMenuItem;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * Base class for all {@link MenuPanel} composed of multiple {@link Widget} accessible via tabs.
 *
 */
public abstract class AbstractTabPanel extends MenuPanel implements ValueChangeHandler<String> {

    private final Galaxy galaxy;
    private final int tabIndex;
    private final String tabToken;
    private final String header;
    private ListView<NavMenuItem> listView;

    public AbstractTabPanel(final Galaxy galaxy, final String id, final int tabIndex, final String tabToken, final String header) {
        this.galaxy = galaxy;
        this.tabIndex = tabIndex;
        this.tabToken = tabToken;
        this.header = header;

        setId(id);
        History.addValueChangeHandler(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onFirstShow() {
        super.onFirstShow();

        final List<NavMenuItem> alertNavItems = new ArrayList<NavMenuItem>();
        for (final Map.Entry<String, String> tab : getTabs().entrySet()) {
            alertNavItems.add(new NavMenuItem(tab.getKey(), tab.getValue()));
        }

        final ContentPanel accordionPanel = WidgetHelper.createAccodionWrapperPanel();
        accordionPanel.setCollapsible(false);
        accordionPanel.setHeaderVisible(false);
        accordionPanel.setAutoHeight(true);
        accordionPanel.setAutoWidth(true);
        final ContentPanel listViewPanel = WidgetHelper.createPanelWithListView(this.header, alertNavItems);
        this.listView = (ListView<NavMenuItem>) listViewPanel.getWidget(0);
        accordionPanel.add(listViewPanel);
        addMenuItem(accordionPanel);

        setDefaultSelection();
    }

    protected abstract Map<String, String> getTabs();

    protected final PageManager getPageManager() {
        return this.galaxy.getPageManager();
    }

    private void setDefaultSelection() {
        this.listView.getSelectionModel().select(0, false);
    }

    public void onValueChange(final ValueChangeEvent<String> stringValueChangeEvent) {
        if (!isFirstShow()) {
            final String token = stringValueChangeEvent.getValue();
            if ((token.equals(this.tabToken))) {
                setDefaultSelection();
            }
        }
    }

    public void createPageInfo(final String token, final Widget composite) {
        final PageInfo page = new PageInfo(token, this.tabIndex) {

            public Widget createInstance() {
                return null;
            }

            public Widget getInstance() {
                setMain(composite);
                return AbstractTabPanel.this;
            }

        };
        getPageManager().addPage(page);
    }

}
