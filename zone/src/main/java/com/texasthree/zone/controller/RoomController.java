package com.texasthree.zone.controller;

import com.texasthree.zone.entity.Room;
import com.texasthree.zone.entity.User;
import com.texasthree.zone.net.Command;
import com.texasthree.zone.net.CommandController;

/**
 * @author: neo
 * @create: 2021-06-18 10:27
 */
@CommandController
public class RoomController {

    /**
     * 进入房间
     */
    @Command
    public static void command(Cmd.EnterRoom data, User user) {
        user.enter(Room.one());
    }

    /**
     * 离开房间
     */
    @Command
    public static void command(Cmd.LeaveRoom data, User user) {
        user.leave();
    }

    /**
     * 坐下
     */
    @Command
    public static void command(Cmd.SitDown data, User user) {
        var room = user.getRoom();
        if (room == null) {
            user.send(new Cmd.Warning("房间不存在"));
            return;
        }
        room.sitDown(user, data.seatId);
    }

    /**
     * 站起
     */
    @Command
    public static void command(Cmd.SitUp data, User user) {
        var room = user.getRoom();
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        room.sitUp(user);
    }


    /**
     * 玩家执行牌局操作
     */
    @Command
    public static void command(Cmd.PlayerAction data, User user) {

    }

    private static Room checkRoom(String roomId, User user) {
        var room = Room.getRoom(roomId);
        if (room == null) {
            user.send(new Cmd.Warning("房间不存在"));
        }
        return room;
    }
}
