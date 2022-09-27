package com.texasthree.zone.controller;

import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.club.ClubService;
import com.texasthree.zone.club.member.Member;
import com.texasthree.zone.club.member.MemberData;
import com.texasthree.zone.protocal.ClubProtocal;
import com.texasthree.zone.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public ClubProtocal.ClubData club() throws Exception {
        var clubId = requireMeClubId(this.getMe());
        return new ClubProtocal.ClubData(this.clubService.getClubById(clubId));
    }

    @GetMapping("/members")
    public List<ClubProtocal.Member> member() throws Exception {
        var clubId = requireMeClubId(this.getMe());
        var m = new Member(new MemberData(clubId, "111"));
        var list = new ArrayList<ClubProtocal.Member>();
        list.add(new ClubProtocal.Member(m));
        return list;
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

    private String requireMeClubId(User user) {
        if (StringUtils.isEmpty(user.getClubId())) {
            throw new IllegalArgumentException("不存在俱乐部");
        }
        return user.getClubId();
    }
}
