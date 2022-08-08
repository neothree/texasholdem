package com.texasthree.zone.packet;

import com.texasthree.zone.packet.Packet;

/**
 * @author: neo
 * @create: 2021-10-25 17:18
 */
public interface PacketHandler {
    void handle(Packet packet);
}
