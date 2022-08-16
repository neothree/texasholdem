package com.texasthree.zone.controller;

import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.security.shiro.LoginerRealm;
import com.texasthree.utility.restful.RestResponse;
import com.texasthree.zone.ZoneService;
import com.texasthree.zone.entity.Room;
import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpController extends AbstractMeController<User> {

    @Autowired
    private LoginerRealm<User> loginerRealm;

    @Autowired
    private UserService userService;

    @Autowired
    private ZoneService zoneService;

    @PostMapping(value = "/login")
    public RestResponse login(String username,
                              String password) throws Exception {
        if (this.userService.getDataByUsername(username) == null) {
            this.zoneService.createUser(username, password);
        }
        return loginerRealm.login(username, password);
    }

    @GetMapping(value = "/rooms")
    public RestResponse rooms() {
        return new RestResponse<>();
    }

    /**
     * 进入房间
     */
    @PostMapping(value = "/room")
    public void enterRoom(@RequestParam String roomId) {
        var room = Room.getRoom(roomId);
        if (room == null) {
            return;
        }

        var me = this.getMe();
        me.leave();
        me.enter(room);
    }
}
