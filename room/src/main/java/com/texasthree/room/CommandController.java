package com.texasthree.room;


import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.message.MessageController;
import com.texasthree.core.message.MessageDispatcher;
import com.texasthree.proto.Cmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@MessageController
public class CommandController {

    private static final Logger LOG = LoggerFactory.getLogger(CommandController.class);

    @Autowired
    private MessageDispatcher dispatcher;

    public void setName(PlayerSession ps, Cmd.SetName cmd) {
        User user = User.getUser(ps.getId().toString());
        if (user != null) {
            return;
        }
        Cmd.UserData data = new Cmd.UserData();
        data.name = cmd.name;
        data.id = ps.getId().toString();
        data.chips = 1000;
        user = new User(data);

        LOG.info("玩家填写姓名 name={}", cmd.name);
    }

    /**
     * 创建房间
     */
    public void createRoom(PlayerSession ps, Cmd.CreateRoom cmd) {
//        GameRoomSession session = new RoomSession(new GameRoomSession.GameRoomSessionBuilder(), dispatcher);
//        Room room = new Room(cmd.data, session);
    }

    /**
     * 进入房间
     */
    public void enterRoom(PlayerSession ps, Cmd.EnterRoom cmd) {
        Room room = Room.getRoom(cmd.id);
        User user = User.getUser(ps.getId().toString());
        user.enter(room);
    }

    /**
     * 离开房间
     */
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

    /**
     * 心跳
     */
    public void heartbeat(PlayerSession ps, Cmd.Heartbeat cmd) {
        LOG.info("收到心跳 id={} time={}", ps.getId(), cmd.timestamp);
    }
}
