package org.mule.galaxy.jcr;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.util.QNameUtil;

public class JcrArtifact extends AbstractJcrObject implements Artifact {
    private static final String CONTENT_TYPE = "contentType";
    private static final String CREATED = "created";
    private static final String UPDATED = "updated";
    private static final String NAME = "name";
    private static final String QNAME = "qname";
    
    private Set<ArtifactVersion> versions;
    private VersionHistory vh;
    private Workspace workspace;
    private ContentHandler contentHandler;
    
    public JcrArtifact(Workspace w, Node node) {
        this(w, node, null);
    } 
    public JcrArtifact(Workspace w, Node node, ContentHandler contentHandler) {
        super(node);
        this.workspace = w;
        this.contentHandler = contentHandler;
    }

    public String getId() {
        try {
            return node.getUUID();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Workspace getWorkspace() {
        return workspace;
    }

    public Calendar getCreated() {
        return getDateOrNull(CREATED);
    }

    public Calendar getUpdated() {
        return getDateOrNull(UPDATED);
    }

    public String getContentType() {
        return getStringOrNull(CONTENT_TYPE);
    }

    
    public QName getDocumentType() {
        return QNameUtil.fromString(getStringOrNull(QNAME));
    }

    public String getName() {
        return getStringOrNull(NAME);
    }
    
    public void setContentType(String ct) {
        try {
            node.setProperty(CONTENT_TYPE, ct);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setDocumentType(QName documentType) {
        try {
            node.setProperty(QNAME, documentType.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setName(String name) {
        try {
            node.setProperty(NAME, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Set<ArtifactVersion> getVersions() {
        if (versions == null) {
            versions = new HashSet<ArtifactVersion>();

            VersionHistory vh = getVersionHistory();
            try {
                Version root = vh.getRootVersion();
                for (VersionIterator itr = vh.getAllVersions(); itr.hasNext();) {
                    Version v = itr.nextVersion();
                    
                    Node node = v.getNodes().nextNode();
                    if (!v.equals(root)) {
                        versions.add(new JcrVersion(this, node));
                    }
                }
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
            
        return versions;
    }

    public ArtifactVersion getVersion(String versionName) {
        for (ArtifactVersion v : getVersions()) {
            if (v.getVersion().equals(versionName)) {
                return v;
            }
        }
        return null;
    }

    public Node getNode() {
        return node;
    }

    public VersionHistory getVersionHistory() {
        if (vh == null) {
            try {
                vh = node.getVersionHistory();
            } catch (UnsupportedRepositoryOperationException e) {
                throw new RuntimeException(e);
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
        return vh;
    }

    public ArtifactVersion getLatestVersion() {
        ArtifactVersion latest = null;
        for (ArtifactVersion v : getVersions()) {
            if (latest == null) {
                latest = v;
            } else if (latest.getCreated().before(v.getCreated())) {
                latest = v;
            }
        }
        return latest;
    }
    
    public ContentHandler getContentHandler() {
        return contentHandler;
    }
    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

}
