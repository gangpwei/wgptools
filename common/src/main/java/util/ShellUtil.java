package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellUtil {
    public static String runShell(String shStr) throws Exception {

        Process process;
        process = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", shStr});
        process.waitFor();
        BufferedReader read = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        String result = "";
        while ((line = read.readLine()) != null) {
            result += line;
        }
        return result;
    }
}