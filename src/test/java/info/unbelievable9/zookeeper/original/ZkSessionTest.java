package info.unbelievable9.zookeeper.original;

import info.unbelievable9.zookeeper.ZkRootTest;
import info.unbelievable9.zookeeper.original.watcher.ZkWatcher;
import info.unbelievable9.zookeeper.util.CommonUtil;
import org.apache.log4j.Logger;
import org.apache.zookeeper.ZooKeeper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * 会话连接测试
 *
 * @author : unbelievable9
 * @date : 2019-04-24
 */
public class ZkSessionTest extends ZkRootTest {

    private static final Logger logger = Logger.getLogger(ZkSessionTest.class);

    private long sessionId = 0L;

    private byte[] sessionPasswd;

    /**
     * 创建简单回话测试用例
     *
     * @throws IOException          IO异常
     * @throws InterruptedException 中断异常
     */
    @Test(priority = 1)
    public void sampleSessionTest() throws IOException, InterruptedException {
        Assert.assertNotNull(properties);

        String connectString = properties.getProperty("zookeeper.server3.url")
                + ":"
                + properties.get("zookeeper.server3.port");

        CommonUtil.refreshConnectedSemaphore();

        ZooKeeper zooKeeper = new ZooKeeper(
                connectString,
                5000,
                new ZkWatcher());

        logger.info("连接状态: " + zooKeeper.getState());

        sessionId = zooKeeper.getSessionId();
        sessionPasswd = zooKeeper.getSessionPasswd();

        CommonUtil.getConnectedSemaphore().await();

        Assert.assertEquals(zooKeeper.getState(), ZooKeeper.States.CONNECTED);
        zooKeeper.close();
        logger.info("连接已关闭");
    }

    /**
     * 利用 Session 信息复用回话
     *
     * @throws IOException          IO异常
     * @throws InterruptedException 中断异常
     */
    @Test(priority = 2)
    public void sessionTest() throws IOException, InterruptedException {
        Assert.assertNotNull(properties);

        String connectString = properties.getProperty("zookeeper.server3.url")
                + ":"
                + properties.get("zookeeper.server3.port");

        CommonUtil.refreshConnectedSemaphore();

        // 使用错误的 Session 信息尝试连接
        ZooKeeper zooKeeper = new ZooKeeper(
                connectString,
                5000,
                new ZkWatcher(),
                1L,
                "test".getBytes());

        logger.info("连接状态: " + zooKeeper.getState());

        CommonUtil.getConnectedSemaphore().await();

        Assert.assertEquals(zooKeeper.getState(), ZooKeeper.States.CLOSED);
        zooKeeper.close();
        logger.info("连接已关闭");

        CommonUtil.refreshConnectedSemaphore();

        // 使用正确的 Session 信息尝试连接
        zooKeeper = new ZooKeeper(
                connectString,
                5000,
                new ZkWatcher(),
                sessionId,
                sessionPasswd);

        logger.info("连接状态: " + zooKeeper.getState());

        CommonUtil.getConnectedSemaphore().await();

        Assert.assertEquals(zooKeeper.getState(), ZooKeeper.States.CONNECTED);
        zooKeeper.close();
        logger.info("连接已关闭");
    }
}
