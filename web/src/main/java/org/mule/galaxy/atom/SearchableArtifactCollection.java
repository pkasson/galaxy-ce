/*
 * $Id: ContextPathResolver.java 794 2008-04-23 22:23:10Z andrew $
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

package org.mule.galaxy.atom;


import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;

public class SearchableArtifactCollection extends AbstractArtifactCollection {
    
    public SearchableArtifactCollection(Registry registry, LifecycleManager lifecycleManager) {
        super(registry, lifecycleManager);
    }

    @Override
    protected String addEntryDetails(RequestContext request, 
                                     Entry e, 
                                     IRI entryBaseIri, 
                                     ArtifactVersion entryObj)
        throws ResponseContextException {
        String link = super.addEntryDetails(request, e, entryBaseIri, entryObj);
        
        Collection col = factory.newCollection();
        col.setAttributeValue("id", "versions");
        col.setHref(getArtifactLink(request, entryObj) + ";history");
        col.setTitle("Artifact Versions");
        e.addExtension(col);
        
        return link;
    }

    private IRI getArtifactLink(RequestContext request, ArtifactVersion entryObj) {
        return new IRI(getHref(request)).resolve(getNameOfArtifact(entryObj));
    }

    public String getId(RequestContext request) {
        return "tag:galaxy.mulesource.com,2008:registry:" + registry + ":feed";
    }

    public String getId(ArtifactVersion doc) {
        return ID_PREFIX + doc.getParent().getId();
    }

    public String getTitle(RequestContext request) {
        return "Mule Galaxy Registry/Repository";
    }

    public String getTitle(ArtifactVersion doc) {
        if (doc.getParent().getName() != null) {
            return doc.getParent().getName();
        } else {
            return "(No title) " + doc.getParent().getDocumentType().toString();
        }
    }

    @SuppressWarnings("unchecked")
    public Iterable<ArtifactVersion> getEntries(RequestContext request) throws ResponseContextException {
        try {
            String q = request.getParameter("q");
            
            if (q == null || "".equals(q)) {
                q = "select artifact";
            } else {
                q = UrlEncoding.decode(q);
            }
            
            final Iterator results = registry.search(q, 0, 100).getResults().iterator();
            
            return createArtifactVersionIterable(results, request);
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        }
    }

    protected Iterable<ArtifactVersion> createArtifactVersionIterable(final Iterator results, 
                                                                      final RequestContext context) {
        return new Iterable<ArtifactVersion>() {

            public Iterator<ArtifactVersion> iterator() {
                return new Iterator<ArtifactVersion>() {

                    public boolean hasNext() {
                        return results.hasNext();
                    }

                    public ArtifactVersion next() {
                        Object next = results.next();
                        ArtifactVersion av = null;
                        if (next instanceof ArtifactVersion) {
                            av = (ArtifactVersion) next;
                        } else {
                            av = ((Artifact) next).getDefaultVersion();
                        }
                        
                        return av;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                };
            }
            
        };
    }

    @Override
    protected ArtifactResult postMediaEntry(String slug, 
                                            MimeType mimeType, 
                                            String version,
                                            InputStream inputStream, 
                                            User user,
                                            RequestContext request)
        throws RegistryException, ArtifactPolicyException, IOException, MimeTypeParseException, ResponseContextException, DuplicateItemException, AccessException  {

        Workspace workspace = (Workspace) request.getAttribute(Scope.REQUEST, ArtifactResolver.WORKSPACE);

        if (workspace == null) {
            EmptyResponseContext ctx = new EmptyResponseContext(500);
            ctx.setStatusText("The specified workspace is invalid. Please POST to a valid workspace URL.");
            
            throw new ResponseContextException(ctx);
        }
        
        return registry.createArtifact(workspace, 
                                       mimeType.toString(), 
                                       slug, 
                                       version, inputStream, 
                                       user);
    }

    public void deleteEntry(String name, RequestContext request) throws ResponseContextException {
        Artifact artifact = getArtifact(request);

        try {
            registry.delete(artifact);
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new ResponseContextException(405, e);
        }
    }

    public void deleteMedia(String name, RequestContext request) throws ResponseContextException {
        Artifact artifact = getArtifact(request);

        try {
            registry.delete(artifact);
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new ResponseContextException(405, e);
        }
    }
    
    @Override
    public void putMedia(ArtifactVersion artifactVersion,
                         MimeType contentType, String slug, 
                         InputStream inputStream, RequestContext request)
        throws ResponseContextException {
        Artifact a = artifactVersion.getParent();
        
        try {
            registry.newVersion(a, inputStream, getVersion(request), getUser());
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        } catch (ArtifactPolicyException e) {
           throw createArtifactPolicyExceptionResponse(e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        } catch (DuplicateItemException e) {
            throw new ResponseContextException(409, e);
        } catch (AccessException e) {
            throw new ResponseContextException(401, e);
        }
    }

}
