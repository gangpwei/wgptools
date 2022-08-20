package dubbo.model;

/**
 * @author weigangpeng
 * @date 2018/05/30 ÏÂÎç8:40
 */

public class BeanConfig {
    private String id;
    private String claz;

    public BeanConfig(String id, String claz) {
        this.id = id;
        this.claz = claz;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClaz() {
        return claz;
    }

    public void setClaz(String claz) {
        this.claz = claz;
    }

    public static final String WARP = "\n";
    public  String toXmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append(WARP).append("    <bean id=\"").append(id).append("\" ").append("class=\"").append(claz).append("\" />");
        return sb.toString();
    }
}
