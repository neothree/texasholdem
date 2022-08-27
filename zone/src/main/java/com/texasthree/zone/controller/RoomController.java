package com.texasthree.zone.controller;

import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.utility.restful.RestResponse;
import com.texasthree.zone.Zone;
import com.texasthree.zone.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/room")
public class RoomController extends AbstractMeController<User> {


    @Autowired
    private Zone zone;

    @GetMapping(value = "/{roomId}")
    public RestResponse getRoomData(@PathVariable("roomId") String roomId) throws Exception {
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
        zone.newRoom().addUser(this.getMe());
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
     * 玩家执行牌局操作
     */
    @PostMapping(value = "/round/action")
    public RestResponse action(@RequestParam("username") String username) {
        return RestResponse.SUCCESS;
    }

}
