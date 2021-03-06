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

package org.mule.galaxy.web.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AdminServiceAsync {
    void executeScript(String scriptText, AsyncCallback async);

    void getScripts(AsyncCallback<List<WScript>> async);
    
    void save(WScript script, AsyncCallback async);
    
    void deleteScript(String id, AsyncCallback async);

    void getScriptJobs(AsyncCallback<List<WScriptJob>> async);
    
    void getScriptJob(String id, AsyncCallback async);
    
    void save(WScriptJob script, AsyncCallback async);
    
    void deleteScriptJob(String id, AsyncCallback async);

}
