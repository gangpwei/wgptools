package jar;

import java.util.ArrayList;
import java.util.List;
import static util.StringUtil.*;

/**
 * @author weigangpeng
 * @date 2017/12/16 下午3:05
 */

public class Jar {
    private String group;
    private String artifactId;
    private String version;

    private Jar parent;

    private List<Jar> children;

    private List<Jar> unusedChildren;

    private int dependencyLevel = 0;

    private boolean include = true;

    private boolean used = true;

    /**
     * 文件大小，单位byte
     */
    private long fileSize = 0 ;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 冲突列表
     */
    private List<Jar> conflictList;

    public Jar(String group, String artifactId, String version) {
        this.group = group;
        this.artifactId = artifactId;
        this.version = version;
    }

    public Jar() {

    }

    public boolean isInclude() {
        return include;
    }

    public void setInclude(boolean include) {
        this.include = include;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public Jar getParent() {
        return parent;
    }

    public void setParent(Jar parent) {
        this.parent = parent;
    }

    public List<Jar> getChildren() {
        return children;
    }

    public void setChildren(List<Jar> children) {
        this.children = children;
    }

    public int getDependencyLevel() {
        return dependencyLevel;
    }

    public void setDependencyLevel(int dependencyLevel) {
        this.dependencyLevel = dependencyLevel;
    }

    public String getFileName(){
        if(fileName == null){
            return artifactId + "-" + version + ".jar";
        }else {
            return fileName;
        }
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void addChild(Jar child){
        if(children == null){
            children = new ArrayList<>();
        }
        children.add(child);
    }

    public void addUnUsedChild(Jar child){
        if(unusedChildren == null){
            unusedChildren = new ArrayList<>();
        }
        unusedChildren.add(child);
    }
    public List<Jar> getUnusedChildren() {
        return unusedChildren;
    }

    public void setUnusedChildren(List<Jar> unusedChildren) {
        this.unusedChildren = unusedChildren;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{");
        sb.append("group='").append(group).append('\'');
        sb.append(", artifactId='").append(artifactId).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String toPomString() {
        int tab = 2;
        final StringBuffer sb = new StringBuffer("");
        sb.append(getTab(tab )).append("<dependency>\n");
        sb.append(getTab(tab + 1)).append("<groupId>").append(group).append("</groupId>\n");
        sb.append(getTab(tab + 1)).append("<artifactId>").append(artifactId).append("</artifactId>\n");
        sb.append(getTab(tab + 1)).append("<version>").append(version).append("</version>\n");
        if(unusedChildren != null){
            sb.append(getTab(tab + 1 )).append("<exclusions>\n");
            for (Jar child : unusedChildren) {
                sb.append(child.toExcludePomString());
            }
            sb.append(getTab(tab + 1 )).append("</exclusions>\n");

        }
        sb.append(getTab(tab )).append("</dependency>\n");
        return sb.toString();
    }

    public String toExcludePomString() {
        int tab = 4;
        final StringBuffer sb = new StringBuffer("");
        sb.append(getTab(tab )).append("<exclusion>\n");
        sb.append(getTab(tab + 1)).append("<groupId>").append(group).append("</groupId>\n");
        sb.append(getTab(tab + 1)).append("<artifactId>").append(artifactId).append("</artifactId>\n");
        sb.append(getTab(tab )).append("</exclusion>\n");
        return sb.toString();
    }

    public String toLevelString() {
        final StringBuffer sb = new StringBuffer(getTab(dependencyLevel));
        sb.append("group='").append(formatLen(group, 20)).append('\'');
        sb.append(", artifactId='").append(formatLen(artifactId, 20)).append('\'');
        sb.append(", version='").append(formatLen(version, 8)).append('\'');
        //sb.append(", parent=").append(parent);
        //sb.append(", children=").append(children);
        sb.append(", dependencyLevel=").append(dependencyLevel);
        sb.append(", include=").append(include);
        if(children != null){
            for (Jar child : children) {
                sb.append("\n").append(child.toLevelString());
            }
        }
        return sb.toString();
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Jar> getConflictList() {
        return conflictList;
    }

    public void setConflictList(List<Jar> conflictList) {
        this.conflictList = conflictList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Jar)) { return false; }

        Jar jar = (Jar)o;

        if (group != null ? !group.equals(jar.group) : jar.group != null) { return false; }
        if (artifactId != null ? !artifactId.equals(jar.artifactId) : jar.artifactId != null) { return false; }
        if (version != null ? !version.equals(jar.version) : jar.version != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = group != null ? group.hashCode() : 0;
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = 31 * result + (unusedChildren != null ? unusedChildren.hashCode() : 0);
        result = 31 * result + dependencyLevel;
        result = 31 * result + (include ? 1 : 0);
        result = 31 * result + (used ? 1 : 0);
        return result;
    }
}
