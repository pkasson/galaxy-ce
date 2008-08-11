package org.mule.galaxy.impl.extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.Dao;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.type.PropertyDescriptor;

/**
 * Takes care of storing retrieving Identifiable classes (e.g., User) as a property
 * on an artifact.  
 */
public class IdentifiableExtension<T extends Identifiable> implements Extension {
    protected String id;
    protected Dao<T> dao;
    protected String name;
    protected boolean isMultivalueSupported = true;
    
    @SuppressWarnings("unchecked")
    public Object get(Item entry, PropertyDescriptor pd, boolean getWithNoData) {
        Object storedValue = entry.getInternalProperty(pd.getProperty());
	if (pd.isMultivalued()) {
	    List<Identifiable> values = new ArrayList<Identifiable>();
	    List<String> ids = (List<String>) storedValue;
	    
	    if (ids == null) {
	        return new ArrayList<String>();
	    }
	    for (String id : ids) {
		// TODO - account for deleted items
		try {
		    values.add(dao.get(id));
		} catch (NotFoundException e) {
		}
	    }
	    
	    return values;
	} else {
	    try {
		return dao.get((String) storedValue);
	    } catch (NotFoundException e) {
		return null;
	    }
	}
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void store(Item entry, PropertyDescriptor pd, Object value)
	    throws PolicyException, PropertyException {
	Object storeValue;
	if (value instanceof Collection) {
	    ArrayList<String> ids = new ArrayList<String>();
	    for (Object o : (Collection)value) {
		Identifiable i = (Identifiable) o;
		ensureSaved(i);
		ids.add(i.getId());
	    }
	    storeValue = ids;
	} else if (value instanceof Identifiable) {
	    Identifiable i = (Identifiable) value;
	    ensureSaved(i);
	    storeValue = i.getId();
	} else {
	    storeValue = null;
	}

        entry.setInternalProperty(pd.getProperty(), storeValue);
    }

    @SuppressWarnings("unchecked")
    private void ensureSaved(Identifiable i) {
        if (i.getId() == null) {
		try {
		    dao.save((T) i);
		} catch (DuplicateItemException e) {
		    throw new RuntimeException(e);
		} catch (NotFoundException e) {
		    throw new RuntimeException(e);
		}
	    }
    }

    public void validate(Item entry, PropertyDescriptor pd, Object valueToStore) throws PolicyException {
    }

    public List<String> getPropertyDescriptorConfigurationKeys() {
        return new ArrayList<String>();
    }

    public boolean isMultivalueSupported() {
        return isMultivalueSupported;
    }

    public void setMultivalueSupported(boolean isMultivalueSupported) {
        this.isMultivalueSupported = isMultivalueSupported;
    }

    public Dao<T> getDao() {
        return dao;
    }

    public void setDao(Dao<T> dao) {
        this.dao = dao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
