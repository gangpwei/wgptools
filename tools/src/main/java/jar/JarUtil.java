package jar;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DateUtil;
import util.StringUtil;

/**
 * @author weigangpeng
 * @date 2017/12/21 上午11:28
 */

public class JarUtil {

    private static final Logger log = LoggerFactory.getLogger(JarUtil.class);

    /**
     * 格式化jar包名称， 把"alipmc-api-1.2.6-20150525.110450-1.jar"转换成alipmc-api-1.2.6-SNAPSHOT.jar
     * @param name
     * @return
     */
    public static String getFormattedJarName(String name) {
        String foramttedName = name;

        try {
            if(name.indexOf("-20") > 0){
                String snapshotStr = name.substring(name.indexOf("-20") + 1, name.indexOf(".jar"));
                if(snapshotStr.indexOf(".") > 0){
                    String dateStr = snapshotStr.substring(0, snapshotStr.indexOf("."));
                    Date snapshotDate = DateUtil.parseDate(dateStr, "yyyyMMdd");
                    if(snapshotDate != null){
                        foramttedName = name.replaceAll(snapshotStr, "SNAPSHOT");
                    }
                }
            }
        } catch (Exception e) {
            log.error("getFormattedJarName error, name=" + name, e);
        }
        return foramttedName;
    }

    public static Jar getJarFromJarFile(File jarFile){
        String jarName = jarFile.getName();
        Jar jar = getJarFormJarName(jarName);
        jar.setFileSize(jarFile.length());
        return jar;
    }

    public static Jar getJarFormJarName(String jarName) {
        Jar jar = new Jar();
        jar.setFileName(jarName);

        String name = getFormattedJarName(jarName);

        String[] arr = name.split("-");
        int x =0;
        int y = 0;
        for (int i = 0; i< arr.length;i ++) {
            String item = arr[i];
            if(StringUtil.isEmpty(item)){
                continue;
            }
            if(StringUtil.appearNumber(item, ".") >= 2 && hasVersion(item)) {

                y = i;
                break;
            }
            x += item.length();
        }

        if(x > 0){
            jar.setArtifactId(name.substring(0, x + y -1));
            jar.setVersion(name.substring(x + y, name.length()));
        }else{
            jar.setArtifactId(name);
        }
        return jar;
    }

    public static boolean hasVersion(final String str) {
        if(StringUtil.appearNumber(str, ".") < 2){
            return false;
        }

        String[] versionArray = str.split("\\.");
        int numberCount = 0;
        for (String s : versionArray) {
            if(StringUtil.isNumeric(s)){
                numberCount += 1;
            }
        }

        if(numberCount >= 2){
            return true;
        }
        return false;

    }
    //private static Pattern JAR_VERSION = Pattern.compile("*[0-9]{1,4}\\.[0-9]{1,4}");
    //
    //public static boolean hasVersion(final String str) {
    //    Matcher m = JAR_VERSION.matcher(str);
    //    return m.matches();
    //}

    public static void main(String[] args) {
        System.out.println(getJarFormJarName("havana.common-0.1.11-20130123.144159-67.jar"));
        System.out.println(hasVersion("0.1.11"));
    }
}
