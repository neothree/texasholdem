package com.texasthree.core.server.netty;

import com.texasthree.core.server.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;


@Service
public class ServerManagerImpl implements ServerManager {

    private static final Logger LOG = LoggerFactory.getLogger(ServerManagerImpl.class);

    private Set<AbstractNettyServer> servers = new HashSet<AbstractNettyServer>();

    @Autowired
    private NettyTCPServer tcpServer;

    @Autowired
    private NettyUDPServer udpServer;

    @PostConstruct
    private void init() {
        servers.add(tcpServer);
        servers.add(udpServer);
    }

    @Override
    public void startServers() throws Exception {
        for (AbstractNettyServer nettyServer : servers) {
            nettyServer.startServer();
        }
    }

    @Override
    public void stopServers() throws Exception {
        for (AbstractNettyServer nettyServer : servers) {
            try {
                nettyServer.stopServer();
            } catch (Exception e) {
                LOG.error("Unable to stop server {} due to error {}", nettyServer, e);
                throw e;
            }
        }
    }
}
