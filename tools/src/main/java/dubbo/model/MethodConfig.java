package dubbo.model;

public class MethodConfig {
    private String retries;
    private String timeout;
    private String name;

    public String getRetries() {
        return retries;
    }

    public void setRetries(String retries) {
        this.retries = retries;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return
            "name='" + name + '\'' +
                (retries != null ? ", retries='" + retries + '\'' : "") +
                (timeout != null ? ", timeout='" + timeout + '\'' : "");

    }
}