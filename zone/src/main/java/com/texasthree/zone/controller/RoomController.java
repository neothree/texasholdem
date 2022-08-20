package com.texasthree.zone.controller;

import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.utility.restful.RestResponse;
import com.texasthree.zone.entity.Room;
import com.texasthree.zone.user.User;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/room")
public class RoomController extends AbstractMeController<User> {


    /**
     * 进入房间
     */
    @PostMapping(value = "/{roomId}")
    public RestResponse enter(@PathVariable("roomId") String roomId) throws Exception {
        log.info("进入房间");
        return RestResponse.SUCCESS;
    }

    /**
     * 离开房间
     */
    @DeleteMapping("/{roomId}")
    public void leave(@PathVariable("roomId") String roomId) {
        log.info("离开房间");
        var user = this.getMe();
    }

    /**
     * 坐下
     */
    @PostMapping(value = "/seat/{seatId}")
    public void sitDown(@PathVariable("seatId") String seatId) {

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
    @PostMapping(value = "/action")
    public RestResponse action(@RequestParam("username") String username) {
        return RestResponse.SUCCESS;
    }

    private static Room checkRoom(String roomId, User user) {
        var room = Room.getRoom(roomId);
        if (room == null) {
            user.send(new Cmd.Warning("房间不存在"));
        }
        return room;
    }
}
