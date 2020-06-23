package com.texasthree.core.server.netty;

import com.texasthree.core.context.AppContext;
import com.texasthree.core.server.ServerManager;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class ServerManagerImpl implements ServerManager {

    private static final Logger LOG = LoggerFactory.getLogger(ServerManagerImpl.class);

    private Set<AbstractNettyServer> servers = new HashSet<AbstractNettyServer>();

    @Override
    public void startServers(int tcpPort, int udpPort) throws Exception {

        if (tcpPort > 0) {
            AbstractNettyServer tcpServer = (AbstractNettyServer) AppContext.getBean(AppContext.TCP_SERVER);
            tcpServer.startServer(tcpPort);
            servers.add(tcpServer);
        }

        if (udpPort > 0) {
            AbstractNettyServer udpServer = (AbstractNettyServer) AppContext.getBean(AppContext.UDP_SERVER);
            udpServer.startServer(udpPort);
            servers.add(udpServer);
        }

    }

    @Override
    public void startServers() throws Exception {
        AbstractNettyServer tcpServer = (AbstractNettyServer) AppContext.getBean(AppContext.TCP_SERVER);
        tcpServer.startServer();
        servers.add(tcpServer);
        AbstractNettyServer udpServer = (AbstractNettyServer) AppContext.getBean(AppContext.UDP_SERVER);
        udpServer.startServer();
        servers.add(udpServer);
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
