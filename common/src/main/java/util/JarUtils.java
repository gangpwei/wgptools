package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

public class JarUtils {

    /**
     * 创建空的临时目录
     *
     * @return
     */
    public static File createTempDirectory() {
        final String tmpPath = System.getProperty("java.io.tmpdir");
        long current = System.currentTimeMillis();
        File tmpDir = new File(tmpPath + File.separator + "patchDir" + current);
        if (tmpDir.exists()) {
            tmpDir.delete();
            return createTempDirectory();
        }
        tmpDir.mkdirs();
        return tmpDir;
    }

    /**
     * 解压jar文件
     *
     * @param jarFile     要解压的jar文件路径
     * @param destination 解压到哪里
     * @throws IOException
     */
    public static void unJar(String jarFile, String destination) throws Exception {
        File jar = new File(jarFile);
        File dir = new File(destination);
        unJar(jar, dir);
    }

    public static String convertPackageToPath(String packageName) {
        String sep = File.separator;
        if (packageName.equals("")) {
            return sep;
        } else {
            return packageName.replace(".", sep) + sep;
        }
    }

    /**
     * @param packageName
     * @param sourceFile
     * @param targetJarFile
     */
    public static void addFileToJar(String packageName, File sourceFile,
                                    File targetJarFile) {
        try {
            File tmpDir = createTempDirectory();
            unJar(targetJarFile, tmpDir);
            String packagePath = convertPackageToPath(packageName);
            File targetFile = new File(tmpDir, packagePath);
            if (sourceFile.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(sourceFile, targetFile);
            } else {
                FileUtils.copyFileToDirectory(sourceFile, targetFile);
            }
            jar(targetJarFile, tmpDir);
            FileUtils.deleteDirectory(tmpDir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param packageName
     * @param sourceFile
     * @param targetJarFile
     */
    public static void delFileFromJar(String packageName, File sourceFile,
                                      File targetJarFile) {
        try {
            File tmpDir = createTempDirectory();
            unJar(targetJarFile, tmpDir);
            String packagePath = convertPackageToPath(packageName);
            File targetFile = new File(tmpDir, packagePath);
            targetFile = new File(targetFile, sourceFile.getName());
            if (targetFile.exists()) {
                targetFile.delete();
            }
            jar(targetJarFile, tmpDir);
            FileUtils.deleteDirectory(tmpDir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 压缩临时文件目录为jar文件 替换jarFile
     *
     * @param jarFile      target
     * @param tmpDirectory
     */
    public static void jar(File jarFile, File tmpDirectory) {
        if (jarFile == null || tmpDirectory == null
            || jarFile.exists() == false || tmpDirectory.exists() == false) {
            return;
        }
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
                jarFile));
            BufferedInputStream bis;
            List<File> fileList = getAllFiles(tmpDirectory);
            for (int i = 0; i < fileList.size(); i++) {
                File file = (File)fileList.get(i);
                zos
                    .putNextEntry(new ZipEntry(getEntryName(tmpDirectory,
                        file)));
                if (file.isDirectory()) {
                    continue;
                }
                bis = new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = new byte[8192];
                int length = -1;
                while (true) {
                    length = bis.read(buffer);
                    if (length == -1) { break; }
                    zos.write(buffer, 0, length);
                }
                bis.close();
                // IOUtils.copy(bis, zos);
                zos.closeEntry();
            }
            zos.close();
        } catch (Exception ex) {
        }
    }

    public static List<File> getAllFiles(File file) {
        List<File> result = new ArrayList<File>();
        if (file != null) {
            if (file.isDirectory()) {
                File[] ls = file.listFiles();
                for (File t : ls) {
                    List<File> tLst = getAllFiles(t);
                    result.addAll(tLst);
                }
            } else {
                result.add(file);
            }
        }
        return result;
    }

    /**
     * 获得zip entry 字符串
     *
     * @param file
     * @return
     */
    public static String getEntryName(File baseFile, File file) {
        String fileName = file.getPath();
        String result = "";
        try {
            if (baseFile.getParentFile().getParentFile() == null) {
                result = fileName.substring(baseFile.getPath()
                    .length());
            } else {
                result = fileName.substring(baseFile.getPath()
                    .length() + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**jar -xvf
     * @param jarFile
     * @param unJarDir
     */
    public static void unJar(File jarFile,File unJarDir) throws Exception{
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(jarFile));
        unJar(in,unJarDir);

    }

    /**
     * @param in
     * @param unJarDir
     */
    public static void unJar(InputStream in,File unJarDir) throws Exception{
        if(!unJarDir.exists()){
            unJarDir.mkdirs();
        }

        JarInputStream jin = new JarInputStream(in);
        byte[] buffer = new byte[8092];

        ZipEntry entry = jin.getNextEntry();
        while(entry != null){
            String fileName = entry.getName();
            if(File.separatorChar != '/'){
                fileName = fileName.replace('/', File.separatorChar);
            }
            if(fileName.charAt(fileName.length() -1 ) == '/'){
                fileName = fileName.substring(0,fileName.length() -1);
            }
            if(fileName.charAt(0)== '/'){
                fileName = fileName.substring(1);
            }
            File file = new File(unJarDir,fileName);
            if(entry.isDirectory()){
                file.mkdirs();
            }else{
                File parent = file.getParentFile();
                if(!parent.exists()){
                    parent.mkdirs();
                }
                if(!file.getName().endsWith(".class")){
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                    int len = 0;
                    while((len = jin.read(buffer,0,buffer.length)) != -1){
                        out.write(buffer,0,len);
                    }
                    out.flush();
                    out.close();
                    file.setLastModified(entry.getTime());
                }
            }
            jin.closeEntry();
            entry = jin.getNextEntry();
        }

        Manifest mf = jin.getManifest();
        if(mf != null){
            File file = new File(unJarDir,"META-INF/MANIFEST.MF");
            File parent = file.getParentFile();
            if(!parent.exists()){
                parent.mkdirs();
            }
            OutputStream out = new FileOutputStream(file);
            mf.write(out);
            out.flush();
            out.close();
        }

        jin.close();
    }


    public static void main(String[] args) throws Exception {
        //String a = "com.mjp.core.aa";
        //String b = convertPackageToPath(a);
        //System.out.println(b);


        File jarF = new File("/Users/weigangpeng/IdeaProjects/muses_new/banner/bundle/war/target/b2b.crm.muses.bundle.war-1.0-SNAPSHOT-sources.jar");
        File bjar = new File("c:/test/b.jar");
        //File unJarDir = new File(jarF.getAbsolutePath()+"/");
        File unJarDir = new File("/Users/weigangpeng/IdeaProjects/muses_new/banner/bundle/war/");

        unJar(jarF, unJarDir);
        //jar(bjar,unJarDir);
    }

} 