package com.texasthree.core.server;

/**
 * A generic interface used to manage a server.
 *
 * @author Abraham Menacherry
 */
public interface ServerManager {

    void startServers() throws Exception;

    /**
     * Used to stop the server and manage cleanup of resources.
     */
    void stopServers() throws Exception;
}
