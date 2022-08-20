package com.texasthree.zone.controller;

import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.security.shiro.LoginerRealm;
import com.texasthree.utility.restful.RestResponse;
import com.texasthree.zone.Zone;
import com.texasthree.zone.room.Room;
import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HttpController extends AbstractMeController<User> {

    @Autowired
    private LoginerRealm<User> loginerRealm;

    @Autowired
    private UserService userService;

    @Autowired
    private Zone zone;

    @PostMapping(value = "/login")
    public RestResponse<LoginResponse> login(
            HttpServletRequest request,
            @RequestParam("username") String username,
            @RequestParam("password") String password) throws Exception {

        // 没有的话创建一个
        var user = this.userService.getDataByUsername(username);
        if (user == null) {
            user = this.zone.createUser(username, password);
        }

        var res = loginerRealm.login(username, password);
        if (!res.isSuccess()) {
            return res;
        }

        var ret = new LoginResponse();
        ret.uid = user.getId();
        ret.name = user.getName();
        ret.token = request.getSession().getId();
        return new RestResponse<>(res.getCode(), res.getMessage(), ret);
    }

    private static class LoginResponse {
        public String uid;
        public String name;
        public String token;

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
