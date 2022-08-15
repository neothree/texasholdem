package com.texasthree.zone.controller;

import com.texasthree.security.login.service.LoginerService;
import com.texasthree.security.shiro.LoginerRealm;
import com.texasthree.utility.restful.RestResponse;
import com.texasthree.zone.entity.Room;
import com.texasthree.zone.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class HttpController {

    @Autowired
    private LoginerRealm<User> loginerRealm;

    @Autowired
    private LoginerService loginerService;

    @RequestMapping(value = "/login", method = POST)
    public RestResponse login(@RequestParam String username,
                              @RequestParam String password) {
        var loginer = this.loginerService.getDataByUsername(username);
        if (loginer == null) {
            this.loginerService.login(username, password);
        }
        return loginerRealm.login(username, password);
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

    private User getMe() {
        return null;
    }
}
