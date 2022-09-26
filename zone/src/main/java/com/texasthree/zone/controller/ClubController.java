package com.texasthree.zone.controller;

import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.club.Club;
import com.texasthree.zone.club.ClubService;
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

    private static class ClubInfo {
        public String id;
        public String name;
        public String creator;
        public String creatorName;
        public String avatar;
        public int num;
        public int capacity;
        public int fund;
        public int balance;

        ClubInfo(Club club) {
            this.id = club.getId();
            this.name = club.getName();
            this.creator = club.getCreator();
            this.creatorName = "张三";
            this.avatar = club.getAvatar();
            this.balance = club.getBalance().intValue();
            this.fund = club.getFund().intValue();
            this.capacity = club.getCapacity();
            this.num = 10;
        }
    }

    @GetMapping
    public ClubInfo info() throws Exception {
        var me = this.getMe();
        if (StringUtils.isEmpty(me.getClubId())) {
            return null;
        }
        return new ClubInfo(this.clubService.getClubById(me.getClubId()));
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
