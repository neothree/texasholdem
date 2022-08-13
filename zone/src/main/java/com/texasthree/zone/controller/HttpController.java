package com.texasthree.zone.controller;

import com.texasthree.zone.entity.Room;
import com.texasthree.zone.entity.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpController {


    @PostMapping(value = "/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        var user = User.getUserByUsername(username);
        if (user == null) {
            user = new User(username);
        }
        return user.getToken();
    }

    /**
     * 进入房间
     */
    @PostMapping(value = "/login")
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
