package com.texasthree.zone.net;

import com.texasthree.zone.packet.Packet;
import com.texasthree.zone.packet.PacketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: neo
 * @create: 2022-08-08 16:35
 */
@RestController
@RequestMapping
public class WsController {

    @Autowired
    private PacketHandler packetHandler;

    /**
     * 发送websocket聊天消息
     */
    @MessageMapping("/packet")
    public void wsChatMessage(SimpMessageHeaderAccessor headerAccessor,
                              @Payload Packet packet) throws Exception {
        this.packetHandler.handle(packet);
    }

}
