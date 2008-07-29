package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;

import org.mule.galaxy.web.client.util.StringListBox;

public class SimpleListPropertyPanel extends PropertyPanel {

    private TextBox valueTB;
    private Label valueLabel;
    private SimplePanel listBoxPanel;
    private StringListBox listBox;

    protected Widget createEditForm() {
        listBoxPanel = new SimplePanel();
        return listBoxPanel;
    }

    protected Object getValueToSave() {
        return valueTB.getText();
    }

    protected String getRenderedText() {
        Collection c = getProperty().getListValue();
        
        String txt;
        if (c == null || c.size() == 0) {
            txt = "-----";
        } else {
            txt = c.toString();
            txt = txt.substring(1, txt.length()-1);
        }
        
        return txt;
    }
    
    protected void onSave(Object value) {
        valueLabel.setText(getRenderedText());
        listBox = new StringListBox((Collection) getProperty().getListValue());
        listBoxPanel.clear();
        listBoxPanel.add(listBox);
    }

    protected Widget createViewWidget() {
        valueLabel = new Label();
        return valueLabel;
    }

    public void initialize() {
        super.initialize();
        valueLabel.setText(getRenderedText());
        listBox = new StringListBox((Collection) getProperty().getListValue());
        listBoxPanel.add(listBox);
    }

}
