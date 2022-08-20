package antx;

/**
 * @author weigangpeng
 * @date 2017/12/28 ÉÏÎç9:23
 */

public class GenerateConf {

    private String tempalte;

    private String destfile;

    private String charset;

    private boolean isUsed = false;

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public String getTempalte() {
        return tempalte;
    }

    public void setTempalte(String tempalte) {
        this.tempalte = tempalte;
    }

    public String getDestfile() {
        return destfile;
    }

    public void setDestfile(String destfile) {
        this.destfile = destfile;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GenerateConf{");
        sb.append("tempalte='").append(tempalte).append('\'');
        sb.append(", destfile='").append(destfile).append('\'');
        sb.append(", charset='").append(charset).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
