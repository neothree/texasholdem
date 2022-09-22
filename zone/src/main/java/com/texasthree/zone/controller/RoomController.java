package com.texasthree.zone.controller;

import com.texasthree.game.texas.Optype;
import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.utility.restful.RestResponse;
import com.texasthree.zone.Zone;
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

    @GetMapping
    public RestResponse rooms() {
        return new RestResponse<>();
    }

    @GetMapping(value = "/{roomId}")
    public RestResponse room(@PathVariable("roomId") String roomId) throws Exception {
        log.info("请求获取房间数据 {}", roomId);
        var data = new Protocal.RoomData(zone.getRoom(), this.getMe().getId());
        return new RestResponse<>(data);
    }

    /**
     * 进入房间
     */
    @PostMapping(value = "/{roomId}")
    public RestResponse enter(@PathVariable("roomId") String roomId) throws Exception {
        log.info("请求进入房间 {}", roomId);
        this.getMe().enter(zone.getRoom());
        return RestResponse.SUCCESS;
    }

    /**
     * 离开房间
     */
    @DeleteMapping("/{roomId}")
    public void leave(@PathVariable("roomId") String roomId) {
        log.info("请求离开房间 {}", roomId);
        zone.getRoom().removeUser(this.getMe());
    }


    /**
     * 买入 - 购买记分牌
     */
    @PostMapping(value = "/{roomId}/chips")
    public RestResponse buyin(@PathVariable("roomId") String roomId,
                              @RequestParam("amount") Integer amount) throws Exception {
        log.info("请求购买记分牌 {}", roomId);
        var room = zone.getRoom();
        room.buyin(this.getMe(), amount);
        return RestResponse.SUCCESS;
    }

    /**
     * 提前结算
     */
    @DeleteMapping(value = "/{roomId}/chips")
    public RestResponse settle(@PathVariable("roomId") String roomId) throws Exception {
        log.info("请求提前结算 {}", roomId);
        var room = Room.getRoom(roomId);
        room.settle(this.getMe().getId());
        return RestResponse.SUCCESS;
    }

    /**
     * 坐下
     */
    @PostMapping(value = "/seat/{seatId}")
    public void sitDown(@PathVariable("seatId") int seatId) {
        log.info("请求坐下 {}", seatId);
        var room = zone.getRoom();
        room.sitDown(this.getMe(), seatId);
    }

    /**
     * 站起
     */
    @DeleteMapping(value = "/seat/{seatId}")
    public RestResponse sitUp(@PathVariable("seatId") String seatId) {
        log.info("请求站起 {}", seatId);
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
        return RestResponse.SUCCESS;
    }

    /**
     * 押注
     */
    @PostMapping(value = "/round/action")
    public void action(@RequestParam("op") String op,
                       int chipsBet) {
        log.info("请求押注 op{} chipsBet={}", op, chipsBet);
        var round = zone.getRoom().getRound();
        if (round == null
                || round.getOperator() == null
                || !round.getOperator().getId().equals(this.getMe().getId())) {
            log.error("押注错误 {} {}", op, chipsBet);
            return;
        }
        round.action(getOptype(op), chipsBet);
    }

    private Optype getOptype(String op) {
        return Arrays.stream(Optype.values()).filter(v -> v.name().equalsIgnoreCase(op)).findFirst().get();
    }

    /**
     * 购买保险
     */
    @PostMapping(value = "/round/buy")
    public void buy(@RequestParam("potId") int potId,
                    @RequestParam("amount") int amount) {
        log.info("请求购买保险 potId={} amount={}", potId, amount);
        var round = zone.getRoom().getRound();
        round.buy(this.getMe().getId(), potId, amount);
    }
}
