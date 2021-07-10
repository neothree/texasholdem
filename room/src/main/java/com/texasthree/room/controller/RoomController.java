package com.texasthree.room.controller;

import com.texasthree.room.Cmd;
import com.texasthree.room.Room;
import com.texasthree.room.User;
import com.texasthree.room.net.Command;
import com.texasthree.room.net.Controller;

/**
 * @author: neo
 * @create: 2021-06-18 10:27
 */
@Controller
public class RoomController {

    /**
     * 进入房间
     */
    @Command
    public void enterRoom(Cmd.EnterRoom data, User user) {
        var room = Room.getRoom(data.roomId);
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        user.enter(room);
    }

    /**
     * 离开房间
     */
    @Command
    public void leaveRoom(Cmd.LeaveRoom data, User user) {
        user.leave();
    }

    /**
     * 坐下
     */
    @Command
    public void sitDown(Cmd.SitDown data, User user) {
        var room = user.getRoom();
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        room.sitDown(user, data.position);
    }

    /**
     * 站起
     */
    @Command
    public void sitUp(Cmd.SitUp data, User user) {
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
    public void dismiss(Cmd.Dismiss data, User user) {
        var room = user.getRoom();
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        room.dismiss(user);
    }

    /**
     * 启动房间牌局
     */
    @Command
    public void enableRound(Cmd.EnableRound data, User user) {
        var room = user.getRoom();
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        room.enableRound(user);
    }

    /**
     * 玩家执行牌局操作
     */
    @Command
    public void playerAction(Cmd.PlayerAction data, User user) {

    }
}
