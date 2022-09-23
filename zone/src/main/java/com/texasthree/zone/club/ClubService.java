package com.texasthree.zone.club;

import com.texasthree.zone.club.member.Member;
import com.texasthree.zone.club.member.MemberData;
import com.texasthree.zone.club.member.MemberDataDao;
import com.texasthree.zone.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: neo
 * @create: 2022-09-22 13:13
 */
@Service
public class ClubService {
    protected final Logger log = LoggerFactory.getLogger(ClubService.class);

    private final Map<String, Club> clubMap = new HashMap<>();

    private final ClubDataDao cdao;

    private final MemberDataDao mdao;

    @Autowired
    public ClubService(ClubDataDao cdao, MemberDataDao mdao) {
        this.cdao = cdao;
        this.mdao = mdao;
    }

    @Transactional
    public Club club(String creator, String name) {
        var data = new ClubData(creator, name);
        this.cdao.save(data);
        var club = new Club(data);
        this.clubMap.put(club.getId(), club);

        log.info("创建俱乐部 creator={} name={}", creator, name);
        return club;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addMember(String id, User user) {
        var club = getClubById(id);
        var md = new MemberData(user.getId());
        this.mdao.save(md);
        club.addMember(new Member(md));
        log.info("俱乐部添加成员 club={} user={}", club, user);
    }

    private Club getClubById(String id) {
        if (!this.clubMap.containsKey(id)) {
            var data = this.cdao.findById(id);
            if (data.isEmpty()) {
                throw new IllegalArgumentException("无找到俱乐部数据 id=" + id);
            }
            this.clubMap.put(id, new Club(data.get()));
        }
        return this.clubMap.get(id);
    }
}
