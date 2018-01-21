package util.file;

/**
 * 文件拷贝时，替换行
 * @author weigangpeng
 * @date 2018/01/21 上午11:55
 */

public interface FileLineReplacer {

    String filter(String line);
}
