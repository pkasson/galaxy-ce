package org.mule.galaxy.web.client.admin;

import java.util.Collection;

import org.mule.galaxy.web.client.ItemNotFoundException;


public interface UserService {
    
    /**
     * @gwt typeArgs org.mule.galaxy.web.client.admin.WUser
     * @return
     */
    Collection getUsers();
    
    String addUser(String username, String fullname, String password);
    
    void updateUser(WUser user, String oldPassword, String password, String confirm) 
        throws PasswordChangeException, ItemNotFoundException;
}
