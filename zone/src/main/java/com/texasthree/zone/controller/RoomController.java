package com.texasthree.zone.controller;

import com.texasthree.game.texas.Optype;
import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.utility.restful.RestResponse;
import com.texasthree.zone.Zone;
import com.texasthree.zone.net.Server;
import com.texasthree.zone.room.Protocal;
import com.texasthree.zone.room.Room;
import com.texasthree.zone.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


@RestController
@RequestMapping("/room")
public class RoomController extends AbstractMeController<User> {

    @Autowired
    private Zone zone;

    @Autowired
    private Server server;

    @GetMapping(value = "/{roomId}")
    public RestResponse room(@PathVariable("roomId") String roomId) throws Exception {
        log.info("获取房间数据 {}", roomId);
        var data = new Protocal.RoomData(zone.getRoom(), this.getMe().getId());
        return new RestResponse<>(data);
    }

    /**
     * 进入房间
     */
    @PostMapping(value = "/{roomId}")
    public RestResponse enter(@PathVariable("roomId") String roomId) throws Exception {
        log.info("进入房间 {}", roomId);
        this.getMe().enter(zone.getRoom());
        return RestResponse.SUCCESS;
    }

    /**
     * 离开房间
     */
    @DeleteMapping("/{roomId}")
    public void leave(@PathVariable("roomId") String roomId) {
        zone.getRoom().removeUser(this.getMe());
    }


    /**
     * 购买记分牌
     */
    @PostMapping(value = "/{roomId}/chips")
    public RestResponse bring(@PathVariable("roomId") String roomId,
                              @RequestParam("amount") Integer amount) throws Exception {
        var room = zone.getRoom();
        room.bring(this.getMe().getId(), amount);
        return RestResponse.SUCCESS;
    }

    /**
     * 提前结算
     */
    @DeleteMapping(value = "/{roomId}/chips")
    public RestResponse takeout(@PathVariable("roomId") String roomId) throws Exception {
        var room = Room.getRoom(roomId);
        room.takeout(this.getMe().getId());
        return RestResponse.SUCCESS;
    }

    /**
     * 坐下
     */
    @PostMapping(value = "/seat/{seatId}")
    public void sitDown(@PathVariable("seatId") int seatId) {
        var room = zone.getRoom();
        room.sitDown(this.getMe(), seatId);
        onSeat(room, null, seatId);
    }

    /**
     * 站起
     */
    @DeleteMapping(value = "/seat/{seatId}")
    public RestResponse sitUp(@PathVariable("seatId") String seatId) {
        var user = this.getMe();
        var room = user.getRoom();
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        if (room.running(user.getId())) {
            return RestResponse.error("牌局进行中不能站起");
        }

        var seat = room.getUserSeat(user.getId());
        if (seat == null) {
            return RestResponse.SUCCESS;
        }
        room.sitUp(user);
        onSeat(room, user.getId(), seat.id);
        return RestResponse.SUCCESS;
    }

    private Protocal.Seat onSeat(Room room, String uid, int seatId) {
        var info = new Protocal.Seat();
        info.seatId = seatId;
        var user = room.getSeats().get(seatId).getUser();
        if (user != null) {
            var p = new Protocal.Player();
            p.uid = user.getId();
            p.name = user.getName();
            p.chips = user.getChips();
            info.player = p;
        }
        room.send(uid, info);
        room.send(info);
        return info;
    }


    /**
     * 押注
     */
    @PostMapping(value = "/round/action")
    public void action(@RequestParam("op") String op,
                       int chipsBet) {
        var room = zone.getRoom();
        room.getRound().action(getOptype(op), chipsBet);
    }

    private Optype getOptype(String op) {
        return Arrays.stream(Optype.values()).filter(v -> v.name().equalsIgnoreCase(op)).findFirst().get();
    }

}
