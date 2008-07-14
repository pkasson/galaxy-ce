package org.mule.galaxy.impl;


import org.mule.galaxy.Dao;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.PropertyDescriptor;
import org.mule.galaxy.test.AbstractGalaxyTest;

import java.util.Collection;

public class PropertyTest extends AbstractGalaxyTest {
    protected Dao<PropertyDescriptor> propertyDescriptorDao;
    
    public void testProperties() throws Exception {
       importHelloWsdl();
       
       PropertyDescriptor pd = new PropertyDescriptor("location", 
                                                      "Geographic Location",
                                                      false);
       
       registry.savePropertyDescriptor(pd);
       assertEquals("location", pd.getProperty());
       
       PropertyDescriptor pd2 = registry.getPropertyDescriptor(pd.getId());
       assertNotNull(pd2);
       assertEquals(pd.getDescription(), pd2.getDescription());
       
       Collection<PropertyDescriptor> pds = registry.getPropertyDescriptors();
       // 12 of these are index related
//       assertEquals(23, pds.size());
       
       PropertyDescriptor pd3 = registry.getPropertyDescriptorByName(pd.getProperty());
       assertNotNull(pd3);
       
       pd.setId(null);
       try {
           registry.savePropertyDescriptor(pd);
           fail("DuplicateItemException expected");
       } catch (DuplicateItemException e) {
       }
    }

}
