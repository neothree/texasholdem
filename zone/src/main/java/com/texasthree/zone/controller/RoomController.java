package com.texasthree.zone.controller;

import com.texasthree.game.texas.Optype;
import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.utility.restful.RestResponse;
import com.texasthree.zone.Zone;
import com.texasthree.zone.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


@RestController
@RequestMapping("/room")
public class RoomController extends AbstractMeController<User> {

    @Autowired
    private Zone zone;

    @GetMapping(value = "/{roomId}")
    public RestResponse room(@PathVariable("roomId") String roomId) throws Exception {
        log.info("获取房间数据 {}", roomId);
        var room = zone.getRoom();
        return new RestResponse<>(room.data());
    }

    /**
     * 进入房间
     */
    @PostMapping(value = "/{roomId}")
    public RestResponse enter(@PathVariable("roomId") String roomId) throws Exception {
        log.info("进入房间 {}", roomId);
        zone.getRoom().addUser(this.getMe());
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
     * 坐下
     */
    @PostMapping(value = "/seat/{seatId}")
    public void sitDown(@PathVariable("seatId") int seatId) {
        zone.getRoom().sitDown(this.getMe(), seatId);
    }

    /**
     * 站起
     */
    @DeleteMapping(value = "/seat/{seatId}")
    public void sitUp(@PathVariable("seatId") String seatId) {
        var user = this.getMe();
        var room = user.getRoom();
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        room.sitUp(user);
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
