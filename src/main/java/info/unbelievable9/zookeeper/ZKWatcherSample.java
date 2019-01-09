package info.unbelievable9.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * Author      : Unbelievable9
 * Class Name  : ZKWatcherSample
 * Description : 通知接收 Watcher
 * Date        : 2019-01-09
 **/
public class ZKWatcherSample implements Watcher {
    static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("收到通知: " + watchedEvent);

        if (watchedEvent.getState().equals(Event.KeeperState.SyncConnected)) {
            connectedSemaphore.countDown();

            System.out.println("ZooKeeper 已连接成功");
        }
    }
}
