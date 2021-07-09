package com.texasthree.room.controller;

import com.texasthree.room.Cmd;
import com.texasthree.room.User;
import com.texasthree.room.net.Command;
import com.texasthree.room.net.Controller;

/**
 * @author: neo
 * @create: 2021-06-18 10:27
 */
@Controller
public class RoomController {

    @Command
    public void enterRoom(Cmd.EnterRoom data, User user) {

    }
}
