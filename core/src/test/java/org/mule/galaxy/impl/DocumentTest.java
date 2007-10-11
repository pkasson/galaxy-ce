package org.mule.galaxy.impl;


import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import org.mule.galaxy.AbstractGalaxyTest;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.util.DOMUtils;

import org.w3c.dom.Document;

public class DocumentTest extends AbstractGalaxyTest {
    protected Registry registry;
    
    public void testAddWsdl() throws Exception {
        Document helloWsdl = DOMUtils.readXml(getResourceAsStream("/wsdl/hello.wsdl"));
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        Artifact document = registry.createArtifact(workspace, helloWsdl);
        
        assertNotNull(document.getId());
        assertEquals("application/xml", document.getContentType());
        assertNotNull(document.getDocumentType());
        assertEquals("definitions", document.getDocumentType().getLocalPart());
        
        Set<? extends ArtifactVersion> versions = document.getVersions();
        assertNotNull(versions);
        assertEquals(1, versions.size());
        
        // Test the version history
        ArtifactVersion version = versions.iterator().next();
        assertSame(helloWsdl, version.getData());
        
        Calendar created = version.getCreated();
        assertTrue(created.getTime().getTime() > 0);
        
        InputStream stream = version.getStream();
        assertNotNull(stream);
        
        Document helloWsdl2 = DOMUtils.readXml(getResourceAsStream("/wsdl/hello.wsdl"));
        ArtifactVersion newVersion = registry.newVersion(document, helloWsdl2);
        
        versions = document.getVersions();
        assertEquals(2, versions.size());
        
        stream = newVersion.getStream();
        assertNotNull(stream);
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }

}
