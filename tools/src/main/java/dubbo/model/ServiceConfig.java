package dubbo.model;

public class ServiceConfig {
    private String interfaceName;
    private String version;
    private String timeout;
    private String group;
    private String target;
    private String annotation;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getTarget() {
        return target;
    }

    public String getGroup() {
        if(group == null){
            group = "DUBBO";
        }
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        String result =
            "interfaceName='" + interfaceName + '\'' +
                ", version='" + version + '\'' +
                ", target='" + target + '\'';
        if (timeout != null) {
            result += ", timeout='" + timeout + '\'';
        }
        return result;

    }

    public ServiceConfig() {
    }

    public ServiceConfig(String interfaceName, String version, String timeout, String group, String target) {
        this.interfaceName = interfaceName;
        this.version = version;
        this.timeout = timeout;
        this.group = group;
        this.target = target;
    }

    public static final String WARP = "\n";

    public  String toXmlString(){
        StringBuilder sb = new StringBuilder();
        if(annotation != null){
            sb.append(WARP).append(annotation);
        }
        sb.append(WARP).append("    <bean class=\"com.taobao.hsf.app.spring.util.HSFSpringProviderBean\" init-method=\"init\">");
        sb.append(WARP).append("        <property name=\"serviceInterface\" value=\"").append(getInterfaceName()).append("\" />");
        sb.append(WARP).append("        <property name=\"serviceVersion\" value=\"").append(getConfigValue(getVersion())).append("\" />");
        sb.append(WARP).append("        <property name=\"target\" ref=\"").append(getTarget()).append("\" />");
        sb.append(WARP).append("        <property name=\"serviceGroup\" value=\"").append(getConfigValue(getGroup())).append("\" />");
        if(getTimeout() != null){
            sb.append(WARP).append("        <property name=\"clientTimeout\" value=\"").append(getConfigValue(getTimeout())).append("\" />");
        }
        sb.append(WARP).append("    </bean>");
        sb.append(WARP);
        return sb.toString();
    }

    public static String getConfigValue(String source){
        //if(source.contains("_")){
        //    return "${" + source + "}";
        //}
        return source;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
}