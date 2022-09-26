package com.texasthree.zone.controller;

import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.club.ClubService;
import com.texasthree.zone.protocal.ClubProtocal;
import com.texasthree.zone.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: neo
 * @create: 2022-09-23 10:28
 */
@RestController
@RequestMapping("/club")
public class ClubController extends AbstractMeController<User> {

    @Autowired
    private ClubService clubService;

    @GetMapping
    public ClubProtocal.ClubInfo info() throws Exception {
        var me = this.getMe();
        if (StringUtils.isEmpty(me.getClubId())) {
            return null;
        }
        return new ClubProtocal.ClubInfo(this.clubService.getClubById(me.getClubId()));
    }

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
