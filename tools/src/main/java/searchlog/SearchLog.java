package searchlog;

/**
 * @author gangpeng.wgp
 * @date 2018/11/30 ÉÏÎç9:54
 */

public class SearchLog {
    private String loginId;
    private String orgId;
    private String queryStr;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getQueryStr() {
        return queryStr;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SearchLog{");
        sb.append("loginId='").append(loginId).append('\'');
        sb.append(", orgId='").append(orgId).append('\'');
        sb.append(", queryStr='").append(queryStr).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
