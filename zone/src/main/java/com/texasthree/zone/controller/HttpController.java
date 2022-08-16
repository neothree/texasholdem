package com.texasthree.zone.controller;

import com.texasthree.security.login.enums.LoginApp;
import com.texasthree.security.login.service.LoginerService;
import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.security.shiro.LoginerRealm;
import com.texasthree.utility.restful.RestResponse;
import com.texasthree.zone.entity.Room;
import com.texasthree.zone.user.User;
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
    private LoginerService loginerService;

    @PostMapping(value = "/login")
    public RestResponse login(String username,
                              String password) throws Exception {
        var loginer = this.loginerService.getDataByUsername(username);
        if (loginer == null) {
            this.loginerService.loginer(username, password, LoginApp.USER);
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
