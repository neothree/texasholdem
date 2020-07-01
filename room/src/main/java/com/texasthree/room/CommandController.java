package com.texasthree.room;


import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.message.MessageController;
import com.texasthree.core.message.MessageDispatcher;
import org.springframework.beans.factory.annotation.Autowired;

@MessageController
public class CommandController {

    @Autowired
    private MessageDispatcher dispatcher;

    /**
     * 创建房间
     */
    public void createRoom(PlayerSession ps, Cmd.CreateRoom cmd) {
//        GameRoomSession session = new RoomSession(new GameRoomSession.GameRoomSessionBuilder(), dispatcher);
//        Room room = new Room(cmd.data, session);
    }

    public void enterRoom(PlayerSession ps, Cmd.EnterRoom cmd) {
        Room room = Room.getRoom(cmd.id);
        User user = User.getUser(ps.getId().toString());
        user.enter(room);
    }

    public void leaveRoom(PlayerSession ps, Cmd.LeaveRoom cmd) {
        User user = User.getUser(ps.getId().toString());
        Room room = user.getRoom();
        user.leave(room);
    }

    /**
     * 坐下
     */
    public void sitdown(PlayerSession ps, Cmd.Sitdown cmd) {
        User user = User.getUser(ps.getId().toString());
        user.getRoom().sitdown(user, cmd.position);
    }

    /**
     * 站起
     */
    public void situp(PlayerSession ps, Cmd.Situp cmd) {
        User user = User.getUser(ps.getId().toString());
        user.getRoom().situp(cmd.position);
    }

    /**
     * 开始游戏
     */
    public void startGame(PlayerSession ps, Cmd.StartGame cmd) {
        User user = User.getUser(ps.getId().toString());
        user.getRoom().start();
    }
}
