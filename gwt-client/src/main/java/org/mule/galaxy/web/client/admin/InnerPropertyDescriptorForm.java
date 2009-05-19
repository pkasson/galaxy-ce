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

package org.mule.galaxy.web.client.admin;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.rpc.WExtensionInfo;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

/**
 * A reusable form builder to add a property.
 */
public class InnerPropertyDescriptorForm {

    private WPropertyDescriptor property;
    private ValidatableTextBox nameTB;
    private ValidatableTextBox descriptionTB;
    private CheckBox multivalue;
    private ListBox typeLB;
    private HashMap<String, ValidatableTextBox> fields;
    private Galaxy galaxy;

    public void initialize(Galaxy galaxy,
                           WPropertyDescriptor pd,
                           final FlexTable table) {
        this.property = pd;
        this.galaxy = galaxy;
        table.setText(0, 0, "Name:");
        
        table.setText(1, 0, "Description:");
        table.setText(2, 0, "Type:");
        
        nameTB = new ValidatableTextBox(new StringNotEmptyValidator());
        nameTB.getTextBox().setText(property.getName());

        table.setWidget(0, 1, nameTB);
        
        descriptionTB = new ValidatableTextBox(new StringNotEmptyValidator());
        descriptionTB.setText(property.getDescription());
        table.setWidget(1, 1, descriptionTB);
        
        if (property.getId() == null) {
            addTypeSelector(table);

            showTypeConfiguration(table, "");
        } else {
            String id = "";
            if (property.getExtension() != null) {
                WExtensionInfo ext = galaxy.getExtension(property.getExtension());
                id = ext.getId();
                table.setText(2, 1, ext.getDescription());
            } else {
                table.setText(2, 1, "String");
            }

            showTypeConfiguration(table, id);
        } 
        AbstractComposite.styleHeaderColumn(table);
    }
    
    private void addTypeSelector(final FlexTable table) {
        typeLB = new ListBox();
        typeLB.addItem("String", "");
        
        List extensions = galaxy.getExtensions();
        
        for (Iterator itr = extensions.iterator(); itr.hasNext();) {
            WExtensionInfo e = (WExtensionInfo) itr.next();
            
            // Only show properties which we have edit renderers for
            if (galaxy.getPropertyInterfaceManager().isExtensionEditable(e.getId())) {
                typeLB.addItem(e.getDescription(), e.getId());
                
                if (e.getId().equals(property.getExtension())) {
                    typeLB.setSelectedIndex(typeLB.getItemCount()-1);
                    showTypeConfiguration(table, e.getId());
                }
            }
        }
        
        typeLB.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent arg0) {
                int idx = typeLB.getSelectedIndex();
                String id;
                if (idx == 0) {
                    id = "";
                } else {
                    id = typeLB.getValue(idx);
                }
                showTypeConfiguration(table, id);
            }
            
        });
        table.setWidget(2, 1, typeLB);
    }


    private void showTypeConfiguration(FlexTable table, String id) {
        for (int i = 3; i < table.getRowCount(); i++) {
            table.removeRow(i);
        }

        fields = new HashMap<String, ValidatableTextBox>();
        
        if ("".equals(id)) {
            initializeMultivalue(table);
            AbstractComposite.styleHeaderColumn(table);
            return;
        }
        
        WExtensionInfo ei = galaxy.getExtension(id);
        
        if (ei.getConfigurationKeys() == null) return;
        
        if (ei.isMultivalueSupported()) {
            initializeMultivalue(table);
        }
        
        Map<String, String> config = property.getConfiguration();
        for (Iterator itr = ei.getConfigurationKeys().iterator(); itr.hasNext();) {
            int row = table.getRowCount();
            String key = (String) itr.next();
            
            table.setText(row, 0, key + ":");
            
            ValidatableTextBox field = new ValidatableTextBox(new StringNotEmptyValidator());
            if (config != null) {
                field.getTextBox().setText(config.get(key));
            }
            fields.put(key, field);
            table.setWidget(row, 1, field);
        }
        
        AbstractComposite.styleHeaderColumn(table);
    }

    private void initializeMultivalue(FlexTable table) {

        table.setText(3, 0, "Multiple Values:");

        if (property.getId() != null) {
            table.setText(3, 1, property.isMultiValued() ? "True" : "False");
        } else {
            multivalue = new CheckBox();
            multivalue.setChecked(property.isMultiValued());
            table.setWidget(3, 1, multivalue);
        }
    }

    public WPropertyDescriptor getPropertyDescriptor() {
        property.setDescription(descriptionTB.getText());
        property.setName(nameTB.getTextBox().getText());
        
        if (typeLB != null) {
            int idx = typeLB.getSelectedIndex();
            if (idx == 0) {
                property.setExtension(null);
            } else {
                property.setExtension(typeLB.getValue(idx));
            }
        }
        
        if (multivalue != null) {
            property.setMultiValued(multivalue.isChecked());
        }
        
        HashMap<String, String> config = new HashMap<String, String>();
        property.setConfiguration(config);
        for (Map.Entry<String, ValidatableTextBox> e : fields.entrySet()) {
            
            ValidatableTextBox tb = e.getValue();
            
            config.put(e.getKey(), tb.getTextBox().getText());
        }
        
        return property;
    }

    protected boolean validate() {
        boolean isOk = true;

        isOk &= nameTB.validate();
        isOk &= descriptionTB.validate();
        
        for (Iterator<ValidatableTextBox> itr = fields.values().iterator(); itr.hasNext();) {
            ValidatableTextBox tb = itr.next();
            
            isOk &= tb.validate();
        }
        return isOk;
    }

}