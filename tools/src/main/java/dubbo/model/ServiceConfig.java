package dubbo.model;

public class ServiceConfig {
    private String interfaceName;
    private String version;
    private String timeout;
    private String target;

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
}