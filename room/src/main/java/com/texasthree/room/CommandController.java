package com.texasthree.room;


import com.texasthree.RoomApplication;
import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.message.MessageController;
import com.texasthree.proto.Cmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageController
public class CommandController {

    private static final Logger LOG = LoggerFactory.getLogger(CommandController.class);

    public void setName(PlayerSession ps, Cmd.SetName cmd) throws Exception {
        User user = User.getUser(ps.getId().toString());
        if (user != null) {
            return;
        }
        Cmd.UserData data = new Cmd.UserData();
        data.name = cmd.name;
        data.id = ps.getId().toString();
        data.chips = 1000;
        user = new User(data, ps);

        User.send(ps, cmd);

        Room room = RoomApplication.room;
        room.addUser(user);
        room.sitdown(user, 0);
        for (int i = 1; i <= 2; i++) {
            Cmd.UserData d = new Cmd.UserData();
            data.name = "robot_" + i;
            data.id = i + "";
            data.chips = 1000;
            User v = new User(d, null);
            room.addUser(v);
            room.sitdown(user, i);
        }
        room.start();
    }

    private void giveRobot() {

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
