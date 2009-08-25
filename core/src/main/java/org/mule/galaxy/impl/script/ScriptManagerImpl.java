package org.mule.galaxy.impl.script;

import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.activity.ActivityManager.EventType;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.script.Script;
import org.mule.galaxy.script.ScriptManager;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.Permission;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springmodules.jcr.JcrCallback;

/**
 * Manages the executing and DAO of Scripts. Scheduling is done in the ScriptJobDaoImpl.
 */
public class ScriptManagerImpl extends AbstractReflectionDao<Script> 
    implements ScriptManager, ApplicationContextAware {
    private final Log log = LogFactory.getLog(getClass());

    private ApplicationContext applicationContext;

    private AccessControlManager accessControlManager;

    private Map<String, Object> scriptVariables;
    
    private ScriptJobDaoImpl scriptJobDao;
    
    private ActivityManager activityManager;

    //@GuardedBy(self)
    private final Map<String, GroovyCodeSource> cache = new HashMap<String, GroovyCodeSource>();
    
    public ScriptManagerImpl() throws Exception {
        super(Script.class, "scripts", true);
    }

    @Override
    protected void doInitializeInJcrTransaction(Session session) throws RepositoryException,
        UnsupportedRepositoryOperationException {
        super.doInitializeInJcrTransaction(session);
        
        // Run startup scripts
        for (Script s : listAll()) {
            if (s.isRunOnStartup()) {
                try {
                    execute(s.getScript(), s);
                } catch (Throwable e) {
                    activityManager.logActivity("Could not run startup script '" 
                                                + s.getName() + "'. " + e.getCause().getMessage(), EventType.ERROR);
                    
                    log.error("Could not run startup script '" + s.getName() + "'.", e);
                }
            }
        }
    }
    
    @Override
    public void delete(String id) {
        synchronized (cache) {
            cache.remove(id);
        }
        super.delete(id);
    }

    @Override
    protected void doSave(Script t, Node node, boolean isNew, boolean isMoved, Session session)
        throws RepositoryException {
        if (t.getId() != null) {
            synchronized (cache) {
                cache.remove(t.getId());
            }
        }
        super.doSave(t, node, isNew, isMoved, session);
    }

    public String execute(final String scriptText) throws AccessException, RegistryException {
        return execute(scriptText, null);
    }

    public String execute(final Script script) throws AccessException, RegistryException {
        return execute(script.getScript(), script);
    }
    
    public String execute(final String scriptText, Script script) throws AccessException, RegistryException {
        accessControlManager.assertAccess(Permission.EXECUTE_ADMIN_SCRIPTS);
        
        final Binding binding = new Binding();
        binding.setProperty("applicationContext", applicationContext);
        
        for (Map.Entry<String, Object> e : scriptVariables.entrySet()) {
            binding.setProperty(e.getKey(), e.getValue());
        }
        if (script != null) {
            binding.setProperty("script", script);
        }
        
        final GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), binding);
        final GroovyCodeSource source;
        synchronized (cache) {
            if (script != null && cache.containsKey(script.getId())) {
                source = cache.get(script.getId());
            } else {
                source = new GroovyCodeSource(scriptText, "script" + Integer.toString(scriptText.hashCode()).replace('-', 'm'), "");
                if (script != null) {
                    cache.put(script.getId(), source);
                }
            }
        }

        try {
            return (String)JcrUtil.doInTransaction(getSessionFactory(), new JcrCallback() {

                public Object doInJcr(Session session) throws IOException, RepositoryException {
                    Object result = shell.evaluate(source);
                    return result == null ? null : result.toString();
                }
                
            });
        } catch (Exception e1) {
            logger.error(e1);
            throw new RegistryException(e1);
        }
    }

    @Override
    protected void doDelete(String id, Session session) throws RepositoryException {
        // Delete all scriptJobs which are associated with this Job
        scriptJobDao.deleteJobsWithScript(id);
        
        super.doDelete(id, session);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }
    
    protected String generateNodeName(Script s) {
        return s.getName();
    }
    
    public void setScriptVariables(Map<String, Object> scriptVariables) {
        this.scriptVariables = scriptVariables;
    }

    public void setScriptJobDao(ScriptJobDaoImpl scriptJobDao) {
        this.scriptJobDao = scriptJobDao;
    }

    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }
    
}
