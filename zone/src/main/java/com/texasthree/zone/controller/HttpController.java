package com.texasthree.zone.controller;

import com.texasthree.zone.entity.Room;
import com.texasthree.zone.entity.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HttpController {

    private Map<String, User> users = new HashMap<>();

    @PostMapping(value = "/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        if (!users.containsKey(username)) {
            users.put(username, new User());
        }
        var user = users.get(username);
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
