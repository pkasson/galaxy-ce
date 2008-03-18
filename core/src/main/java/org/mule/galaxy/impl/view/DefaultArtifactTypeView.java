package org.mule.galaxy.impl.view;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.view.ArtifactTypeView;
import org.mule.galaxy.view.ViewLink;

public class DefaultArtifactTypeView implements ArtifactTypeView {

    public String[] getColumnNames() {
        return new String [] {
          "Name",
          "Workspace",
          "Version",
          "Phase"
        };
    }

    public String getColumnValue(Artifact row, int i) {
        switch (i) {
        case 0:
            return row.getName();
        case 1:
            return row.getParent().getPath();
        case 2:
            return row.getDefaultVersion().getVersionLabel();
        case 3:
            return row.getDefaultVersion().getPhase().getName();
        }
        
        return null;
    }

    public ViewLink getLink(Artifact row, int i) {
        return null;
    }

    public boolean isSummary(int column) {
        return column != 1;
    }

    public boolean isDetail(int column) {
        return true;
    }
    
}
