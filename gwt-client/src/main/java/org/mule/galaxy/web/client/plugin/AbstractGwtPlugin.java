package org.mule.galaxy.web.client.plugin;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractGwtPlugin implements EntryPoint, ValueChangeHandler<String> {
    protected String rootToken;
    protected String name;
    
    private RootPanel insertPoint;
    private Map<String,Widget> pages = new HashMap<String,Widget>();
    private Widget currentWidget;
    
    public AbstractGwtPlugin(String rootToken, String name) {
        super();
        this.rootToken = rootToken;
        this.name = name;
    }

    public void onModuleLoad() {
        History.addValueChangeHandler(this);
        register(rootToken, getClass().getName());
        GWT.log(name + " Plugin loaded", null);
    }

    /*
     * Unfortunately this doesn't work at the moment as GWT changes all the method
     * names, so by hard coding ::loadPlugin, we break things when we're out of hosted mode. 
     */
    public native void register(String token, String className)
    /*-{
        var plugin = this;
        var callback = function() {
           plugin["@" + className + "::loadPlugin()"]();
        }
        $wnd.registerPlugin(token, this, callback);
    }-*/;
    
    public void loadPlugin() {  
        try {
            RootPanel.get("plugin");
        } catch (Throwable t) {
            // for some reason, the first time this is called, it throws an exception
            // in hosted mode. However, second time works fine. We'll just ignore it
            // for now I guess...
        }
        insertPoint = RootPanel.get("plugin");
        load(insertPoint);
    }
    
    public void load(RootPanel insertPoint) {
        String token = History.getToken();
        show(token);
    }

    public void onValueChange(ValueChangeEvent<String> event) {
        show(event.getValue());
    }

    /**
     * Shows the widget associated with the specified token.
     * @param value
     */
    protected void show(String t) {
        Widget w = pages.get(t);
        
        if (w == null) {
            String[] tokens = t.split("/");
            w = pages.get(tokens[0] + "/*");
        }
        
        if (w == null) {
            return;
        }
        
        if (currentWidget != null) {
            insertPoint.remove(currentWidget);
        }
        
        currentWidget = w;
        insertPoint.add(currentWidget);
    }
    
    public void addPage(String tokenPattern, Widget page) {
        pages.put(tokenPattern, page);
    }
}
