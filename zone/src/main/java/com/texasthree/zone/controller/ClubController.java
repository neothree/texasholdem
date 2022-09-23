package com.texasthree.zone.controller;

import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.zone.user.User;
import org.springframework.web.bind.annotation.*;

/**
 * @author: neo
 * @create: 2022-09-23 10:28
 */
@RestController
@RequestMapping("/club")
public class ClubController extends AbstractMeController<User> {

    @PostMapping(value = "/balance/{amount}")
    public void balance(@PathVariable("amount") int amount) throws Exception {
        log.info("请求俱乐部余额贡献 {}", amount);

    }

    @DeleteMapping(value = "/balance/{amount}")
    public void balance(@PathVariable("amount") int amount,
                        @RequestParam("member") String member) throws Exception {
        log.info("请求俱乐部余额分发 {}", amount);
    }
}
