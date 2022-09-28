package com.texasthree.appzone.controller;

import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.club.ClubService;
import com.texasthree.club.member.ClubMember;
import com.texasthree.appzone.protocal.ClubProtocal;
import com.texasthree.appzone.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    /**
     * 获取俱乐部成员
     */
    @GetMapping("/members")
    public List<ClubProtocal.Member> member() throws Exception {
        var clubId = requireMeClubId(this.getMe());
        var list = new ArrayList<ClubProtocal.Member>();
        list.add(new ClubProtocal.Member(new ClubMember(clubId, "111")));
        return list;
    }

    /**
     * 成员贡献给俱乐部余额
     */
    @PostMapping(value = "/balance/{amount}")
    public void balance(@PathVariable("amount") int amount) throws Exception {
        log.info("请求俱乐部余额贡献 {}", amount);
        var me = this.getMe();
        this.clubService.memberToBalance(me.getClubId(), me.getId(), BigDecimal.valueOf(amount));
    }

    /**
     * 分发俱乐部余额给成员
     */
    @DeleteMapping(value = "/balance/{amount}")
    public void balance(@PathVariable("amount") int amount,
                        @RequestParam("member") String member) throws Exception {
        log.info("请求俱乐部余额分发 {}", amount);
        var me = this.getMe();
        this.clubService.balanceToMember(me.getClubId(), member, BigDecimal.valueOf(amount), me.getId());
    }

    private String requireMeClubId(User user) {
        if (StringUtils.isEmpty(user.getClubId())) {
            throw new IllegalArgumentException("不存在俱乐部");
        }
        return user.getClubId();
    }
}
