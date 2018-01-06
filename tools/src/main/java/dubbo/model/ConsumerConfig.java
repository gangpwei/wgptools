package dubbo.model;

import java.util.List;

public class ConsumerConfig {
    private String interfaceName;
    private String version;
    private String timeout;
    private String retries;
    private String id;

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
}