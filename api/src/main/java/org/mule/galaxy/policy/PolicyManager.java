package org.mule.galaxy.policy;

import java.util.Collection;
import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.Phase;

public interface PolicyManager {
    /**
     * Approve the next artifact version. NOTE: previous may be null here!
     * @param previous
     * @param next
     * @return
     * @throws RegistryException
     */
    public List<ApprovalMessage> approve(ArtifactVersion previous, ArtifactVersion next);
    
    Collection<ArtifactPolicy> getPolicies();
    
    Collection<ArtifactPolicy> getActivePolicies(Artifact a);

    Collection<PolicyInfo> getActivePolicies(Artifact a, boolean includeInherited);

    Collection<ArtifactPolicy> getActivePolicies(Phase p);

    Collection<ArtifactPolicy> getActivePolicies(Lifecycle p);

    Collection<ArtifactPolicy> getActivePolicies(Workspace w, Phase p);

    Collection<ArtifactPolicy> getActivePolicies(Workspace w, Lifecycle p);
    
    
    void setActivePolicies(Workspace w, Collection<Phase> phases, ArtifactPolicy... policies);
    
    void setActivePolicies(Artifact a, Collection<Phase> phases, ArtifactPolicy... policies);

    void setActivePolicies(Collection<Phase> phases, ArtifactPolicy... policies);
    
    void setActivePolicies(Workspace w, Lifecycle lifecycle, ArtifactPolicy... policies);
    
    void setActivePolicies(Artifact a, Lifecycle lifecycle, ArtifactPolicy... policies);

    void setActivePolicies(Lifecycle lifecycle, ArtifactPolicy... policies);
    

    ArtifactPolicy getPolicy(String id);

    void addPolicy(ArtifactPolicy artifactPolicy);
}
