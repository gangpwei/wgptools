package pom;

import java.util.List;

/**
 * @author weigangpeng
 * @date 2017/11/29 ÏÂÎç5:09
 */

public class Dependency {
    private String groupId;
    private String artifactId;
    private String version;

    private List<Dependency> exclusions;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Dependency> getExclusions() {
        return exclusions;
    }

    public void setExclusions(List<Dependency> exclusions) {
        this.exclusions = exclusions;
    }

    public Dependency(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public Dependency() {
    }

    @Override
    public String toString() {
        return "Dependency{" +
            "groupId='" + groupId + '\'' +
            ", artifactId='" + artifactId + '\'' +
            ", version='" + version + '\'' +
            ", exclusions=" + exclusions +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Dependency)) { return false; }

        Dependency that = (Dependency)o;

        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) { return false; }
        if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        return result;
    }
}
