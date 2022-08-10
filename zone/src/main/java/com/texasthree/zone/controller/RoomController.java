package com.texasthree.zone.controller;

import com.texasthree.zone.entity.RoomConfig;
import com.texasthree.zone.net.Command;
import com.texasthree.zone.net.CommandController;
import com.texasthree.zone.entity.Room;
import com.texasthree.zone.entity.User;

/**
 * @author: neo
 * @create: 2021-06-18 10:27
 */
@CommandController
public class RoomController {

    @Command
    public static void command(Cmd.CreateRoom data, User user) {
        var config = new RoomConfig();
        var room = new Room(config, user);
    }


    /**
     * 进入房间
     */
    @Command
    public static void command(Cmd.EnterRoom data, User user) {
        var room = checkRoom(data.roomId, user);
        if (room == null) {
            return;
        }
        user.enter(room);
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
        room.sitDown(user, data.position);
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
     * 解散房间
     */
    @Command
    public static void command(Cmd.Dismiss data, User user) {
        var room = user.getRoom();
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        room.dismiss();
    }

    /**
     * 启动房间牌局
     */
    @Command
    public static void command(Cmd.EnableRound data, User user) {
        var room = user.getRoom();
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        room.enableRound();
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
