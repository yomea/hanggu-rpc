package com.hangu.rpc.common.listener;

import com.hangu.rpc.common.manager.HanguExecutorManager;
import com.hangu.rpc.common.properties.ZookeeperConfigProperties;
import com.hangu.rpc.common.registry.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

@Slf4j
public class CuratorConnectionStateListener implements ConnectionStateListener {

    private final long UNKNOWN_SESSION_ID = -1L;

    private long lastSessionId;
    private int timeout;
    private int sessionExpireMs;

    private RegistryService registryService;

    public CuratorConnectionStateListener(RegistryService registryService, ZookeeperConfigProperties properties) {
        this.registryService = registryService;
        this.timeout = properties.getConnectTimeout();
        this.sessionExpireMs = properties.getSessionTimeout();
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState state) {
        long sessionId = UNKNOWN_SESSION_ID;
        try {
            sessionId = client.getZookeeperClient().getZooKeeper().getSessionId();
        } catch (Exception e) {
            log.warn("Curator client state changed, but failed to get the related zk session instance.");
        }

        if (state == ConnectionState.LOST) {
            log.warn("Curator zookeeper session " + Long.toHexString(lastSessionId) + " expired.");
        } else if (state == ConnectionState.SUSPENDED) {
            log.warn("Curator zookeeper connection of session " + Long.toHexString(sessionId) + " timed out. " +
                "connection timeout value is " + timeout + ", session expire timeout value is " + sessionExpireMs);
            // 因为创建的是临时节点，所以这里要重新注册和重新订阅
            HanguExecutorManager.getGlobalExecutor().execute(() -> {
                registryService.retryRegister();
                registryService.retrySubscribe();
            });
        } else if (state == ConnectionState.CONNECTED) {
            lastSessionId = sessionId;
            log.info("Curator zookeeper client instance initiated successfully, session id is " + Long.toHexString(
                sessionId));
        } else if (state == ConnectionState.RECONNECTED) {
            if (lastSessionId == sessionId && sessionId != UNKNOWN_SESSION_ID) {
                log.warn("Curator zookeeper connection recovered from connection lose, " +
                    "reuse the old session " + Long.toHexString(sessionId));
            } else {
                log.warn("New session created after old session lost, " +
                    "old session " + Long.toHexString(lastSessionId) + ", new session " + Long.toHexString(sessionId));
                lastSessionId = sessionId;
            }
            // 因为创建的是临时节点，所以这里要重新注册和重新订阅
            HanguExecutorManager.getGlobalExecutor().execute(() -> {
                registryService.retryRegister();
                registryService.retrySubscribe();
            });
        }
    }

}