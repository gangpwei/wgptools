package dubbo.model;

import java.util.List;

public class ConsumerConfig {
    private String interfaceName;
    private String version;
    private String timeout;
    private String retries;
    private String group = "DUBBO";
    private String id;

    private String annotation;


    public static final String DEFAULT_TIMEOUT = "3000";


    public ConsumerConfig() {
    }

    public ConsumerConfig(String interfaceName, String version, String timeout, String group, String id) {
        this.interfaceName = interfaceName;
        this.version = version;
        this.timeout = timeout;
        this.group = group;
        this.id = id;
    }

    public String getRetries() {
        return retries;
    }

    public void setRetries(String retries) {
        this.retries = retries;
    }

    public List<MethodConfig> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodConfig> methods) {
        this.methods = methods;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    List<MethodConfig> methods;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String result =
            "id='" + id + '\'' +
            "interfaceName='" + interfaceName + '\'' +
                ", version='" + version + '\'' +
                (retries != null ? ", retries='" + retries + '\'' : "")
            ;
        if (timeout != null) {
            result += ", timeout='" + timeout + '\'';
        }
        if(methods != null ){
            for (MethodConfig method : methods) {
                result =  result + "\n               " + method;
            }
        }
        return result;

    }

    public static final String WARP = "\n";

    public  String toXmlString(){

        StringBuilder sb = new StringBuilder();
        String className;
        if(getRetries() != null){
            className = "com.taobao.hsf.app.api.util.HSFApiConsumerBean";
        }else{
            className = "com.taobao.hsf.app.spring.util.HSFSpringConsumerBean";
        }
        className = "com.taobao.hsf.app.spring.util.HSFSpringConsumerBean";

        if(annotation != null){
            sb.append(WARP).append(annotation);
        }
        sb.append(WARP).append("    <bean name=\"").append(getId()).append("\" class=\"" + className + "\" init-method=\"init\">");
        sb.append(WARP).append("        <property name=\"interfaceName\" value=\"").append(getInterfaceName()).append("\" />");
        sb.append(WARP).append("        <property name=\"version\" value=\"").append(getConfigValue(getVersion())).append("\" />");
        sb.append(WARP).append("        <property name=\"group\" value=\"").append(getConfigValue(getGroup())).append("\" />");

        String timeout = DEFAULT_TIMEOUT;
        if(getTimeout() != null){
            timeout = getTimeout();
        }
        sb.append(WARP).append("        <property name=\"clientTimeout\" value=\"").append(getConfigValue(timeout)).append("\" />");
        if(getRetries() != null){
            //sb.append(WARP).append("        <property name=\"retries\" value=\"").append(getConfigValue(getRetries())).append("\" />");
        }
        if(getMethods() != null && getMethods().size()>0){
            sb.append(WARP).append("        <property name=\"methodSpecials\">");
            sb.append(WARP).append("            <list>");
            for (MethodConfig methodConfig : getMethods()) {
                sb.append(WARP).append("                <bean class=\"com.taobao.hsf.model.metadata.MethodSpecial\">");
                sb.append(WARP).append("                    <property name=\"methodName\" value=\"").append(methodConfig.getName()).append("\" />");
                sb.append(WARP).append("                    <property name=\"clientTimeout\" value=\"").append(getConfigValue(methodConfig.getTimeout())).append("\" />");
                sb.append(WARP).append("                </bean>");

            }
            sb.append(WARP).append("            </list>");
            sb.append(WARP).append("        </property>");
        }
        sb.append(WARP).append("    </bean>");
        sb.append(WARP);
        return sb.toString();
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public static String getConfigValue(String source){
        //if(source.contains("_")){
        //    return "${" + source + "}";
        //}
        return source;
    }
}