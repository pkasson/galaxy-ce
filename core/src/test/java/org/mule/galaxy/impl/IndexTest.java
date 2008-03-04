package org.mule.galaxy.impl;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Index;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.xml.namespace.QName;

public class IndexTest extends AbstractGalaxyTest {
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
        
    }
    
    public void testXmlSchema() throws Exception {
        Artifact a = importXmlSchema();
        
        assertEquals("http://www.example.org/test/", 
                     a.getProperty("xmlschema.targetNamespace"));
        
        Object property = a.getProperty("xmlschema.element");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        assertTrue(((Collection) property).contains("testElement"));

        property = a.getProperty("xmlschema.complexType");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        assertTrue(((Collection) property).contains("testComplexType"));

        property = a.getProperty("xmlschema.group");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        assertTrue(((Collection) property).contains("testGroup"));

        property = a.getProperty("xmlschema.attributeGroup");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        assertTrue(((Collection) property).contains("testAttributeGroup"));
        
        
    }
    public void xtestReindex() throws Exception {
        Artifact artifact = importHelloWsdl();
        
        Index i = indexManager.getIndex("wsdl.targetNamespace");
        
        i.setExpression("concat('foo', 'bar')");
        
        indexManager.save(i);
        
        Thread.sleep(2000);
        
        artifact = registry.getArtifact(artifact.getId());
        Object value = artifact.getProperty("wsdl.targetNamespace");
        assertEquals("foobar", value);
    }
    
    public void testWsdlIndex() throws Exception {
        Collection<Index> indices = indexManager.getIndices(Constants.WSDL_DEFINITION_QNAME);
        assertNotNull(indices);
        assertEquals(5, indices.size());
        
        Index idx = null;
        Index tnsIdx = null;
        Index epIdx = null;
        for (Index i : indices) {
            if (i.getId().equals("wsdl.service")) {
                idx = i;
            } else if (i.getId().equals("wsdl.endpoint")) {
                epIdx = i;
            } else if (i.getId().equals("wsdl.targetNamespace")) {
                tnsIdx = i;
            }
        }
        
        assertEquals("wsdl.service", idx.getId());
        assertEquals("WSDL Services", idx.getName());
        assertEquals(Index.Language.XQUERY, idx.getLanguage());
        assertEquals(String.class, idx.getQueryType());
        assertNotNull(idx.getExpression());
        assertEquals(1, idx.getDocumentTypes().size());
        
        assertEquals("wsdl.targetNamespace", tnsIdx.getId());
        assertEquals(Index.Language.XPATH, tnsIdx.getLanguage());
        assertEquals(String.class, tnsIdx.getQueryType());
        assertNotNull(tnsIdx.getExpression());
        assertEquals(1, tnsIdx.getDocumentTypes().size());
        
        // Import a document which should now be indexed
        Artifact artifact = importHelloWsdl();

        ArtifactVersion version = artifact.getActiveVersion();
        Object property = version.getProperty("wsdl.service");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection services = (Collection) property;
        
        assertTrue(services.contains("HelloWorldService"));
        
        property = version.getProperty("wsdl.targetNamespace");
        assertNotNull(property);
        assertEquals("http://mule.org/hello_world", property);
        
        property = version.getProperty(epIdx.getId());
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection endpoints = (Collection) property;
        assertTrue(endpoints.contains("SoapPort"));
        
        // Try out search!
        Set results = registry.search("select artifact where wsdl.service = 'HelloWorldService'", 0, 100).getResults();
        assertEquals(1, results.size());
        
        Artifact next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());
        
        results = registry.search(new Query(Artifact.class, 
                                                Restriction.eq("wsdl.service", 
                                                               new QName("HelloWorldService")))).getResults();
        
        assertEquals(1, results.size());
        
        next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            Restriction.eq("wsdl.service", 
                                                           new QName("HelloWorldService")))).getResults();
    
        assertEquals(1, results.size());
        
        ArtifactVersion nextAV = (ArtifactVersion) results.iterator().next();
        assertEquals("0.1", nextAV.getVersionLabel());
        // assertNotNull(nextAV.getData());
        // TODO test data
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            Restriction.eq("documentType", Constants.WSDL_DEFINITION_QNAME.toString()))).getResults();
    
        assertEquals(1, results.size());
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            Restriction.eq("contentType", "application/xml"))).getResults();
    
        assertEquals(1, results.size());
        

        results = registry.search(new Query(ArtifactVersion.class, 
                                            Restriction.in("contentType", Arrays.asList("application/xml")))).getResults();
    
        assertEquals(1, results.size());
    }

    public void testGroovyIndex() throws Exception
    {
        InputStream stream = new FileInputStream("c:\\java\\mule\\mule1-netboot-1.0-beta-3-SNAPSHOT\\lib\\boot\\mule1-netboot-launcher-1.0-beta-3-SNAPSHOT.jar");

        assertNotNull(stream);

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        ArtifactResult ar = registry.createArtifact(workspace,
                                                    "application/java-archive",
                                                    "test.jar",
                                                    "1",
                                                    stream,
                                                    getAdmin());

        Artifact artifact = ar.getArtifact();

        assertNotNull(artifact);

        Index idx = indexManager.getIndex("test.field");
        assertNotNull(idx);

        indexManager.save(idx, true);

        //assertEquals("http://www.example.org/test/",
        //             artifact.getProperty("xmlschema.targetNamespace"));
        //
        //Object property = artifact.getProperty("xmlschema.element");
        //assertNotNull(property);
        //assertTrue(property instanceof Collection);
        //assertTrue(((Collection) property).contains("testElement"));
        //
        //property = artifact.getProperty("xmlschema.complexType");
        //assertNotNull(property);
        //assertTrue(property instanceof Collection);
        //assertTrue(((Collection) property).contains("testComplexType"));
        //
        //property = artifact.getProperty("xmlschema.group");
        //assertNotNull(property);
        //assertTrue(property instanceof Collection);
        //assertTrue(((Collection) property).contains("testGroup"));
        //
        //property = artifact.getProperty("xmlschema.attributeGroup");
        //assertNotNull(property);
        //assertTrue(property instanceof Collection);
        //assertTrue(((Collection) property).contains("testAttributeGroup"));

    }
}
