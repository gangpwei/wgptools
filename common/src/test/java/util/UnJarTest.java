package util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author gangpeng.wgp
 * @date 2019/01/04 ионГ9:49
 */

public class UnJarTest {
    @Test
    public void unJar() throws Exception {

        UnJar.unJar("/Users/weigangpeng/IdeaProjects/bacardi/deploy/target/bacardi.war/WEB-INF/lib", "/Users/weigangpeng/IdeaProjects/bacardi_lib/");
    }

    @Test
    public void forFileDirectory() throws Exception {
    }

    @Test
    public void load() throws Exception {
    }

}