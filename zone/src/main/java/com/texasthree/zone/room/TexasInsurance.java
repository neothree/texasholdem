package com.texasthree.zone.room;

import com.texasthree.game.insurance.Claim;
import com.texasthree.game.insurance.Insurance;
import com.texasthree.game.insurance.InsurancePot;
import com.texasthree.game.texas.Card;
import com.texasthree.game.texas.Circle;
import com.texasthree.game.texas.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

/**
 * @author: neo
 * @create: 2022-09-13 12:56
 */
public class TexasInsurance {

    private static Logger log = LoggerFactory.getLogger(TexasInsurance.class);

    private final Insurance game;

    private final RoundEventHandler handler;

    private final TexasRound round;

    private final Runnable onFinish;

    private final ScheduledEventChecker scheduler = new ScheduledEventChecker();

    TexasInsurance(TexasRound round, RoundEventHandler handler, Runnable onFinished) {
        this.round = round;
        this.handler = handler;
        this.onFinish = onFinished;
        var players = new ArrayList<Player>();
        for (var v : round.players()) {
            if (!v.isLeave() && !round.isFold(v.getId())) {
                players.add(v);
            }
        }
        var circle = Circle.RIVER;
        if (Circle.PREFLOP.equals(round.circle()) || Circle.FLOP.equals(round.circle())) {
            circle = Circle.TURN;
        }
        this.game = new Insurance(players, round.getLeftCard(), circle, round.getDivides());
    }

    /**
     * 开始
     */
    void start() {
        log.info(">>>>>>>>>>>>>>>>> 保险阶段开始 <<<<<<<<<<<<<<<<<<<<<<<<");
        this.handler.on(round, RoundEvent.INSUSRANCE);

        this.scheduler.once(this::buyBegin, 2 * 1000);
    }

    /**
     * 一轮开始
     */
    private void buyBegin() {
        log.info(">>>>>>>>>>>>>>>>> 一轮购买开始 : {}<<<<<<<<<<<<<<<<<<<<<<<<", game.getCircle());
        if (!this.game.circleFinished()) {
            // 玩家购买
            this.scheduler.once(this::buyEnd, 20 * 1000);
            this.handler.on(round, RoundEvent.BUYER);
        } else {
            // 无法购买，直接下一轮
            this.scheduler.once(this::buyEnd, 3 * 1000);
        }
    }

    /**
     * 购买
     */
    void buy(int id, int potId, int amount, List<Card> outs) {
        var pot = this.getPot(this.game.getCircle(), potId);
        if (pot.applicant != id) {
            throw new IllegalArgumentException();
        }

        if (outs == null) {
            // 调试
            outs = pot.getOuts();
        }
        log.info("购买保险");
        this.game.buy(potId, amount, outs);
        if (this.game.circleFinished()) {
            // 这轮购买结束
            this.scheduler.once(this::buyEnd, 2 * 1000);
        }
    }

    /**
     * 一轮结束
     */
    private void buyEnd() {
        log.info(">>>>>>>>>>>>>>>>> 一轮保险结束 : {} <<<<<<<<<<<<<<<<<<<<<<<<", game.getCircle());
        this.game.end();
        this.handler.on(round, RoundEvent.BUY_END);
        if (this.game.finished()) {
            // 整体结束
            this.finish();
        } else {
            // 下一轮开始
            this.buyBegin();
        }
    }

    private void finish() {
        log.info(">>>>>>>>>>>>>>>>> 保险阶段结束 <<<<<<<<<<<<<<<<<<<<<<<<");
        this.scheduler.clear();
        this.onFinish.run();
    }

    Map<Integer, Integer> claims() {
        return this.game.claims().stream()
                .collect(groupingBy(Claim::getApplicant, summingInt(Claim::getProfit)));
    }

    public void loop() {
        this.scheduler.check();
    }

    public int leftSec() {
        return this.scheduler.leftSec();
    }

    InsurancePot getPot(String circle, int potId) {
        return this.game.getPots().stream()
                .filter(v -> v.circle.equals(circle) && v.id == potId)
                .findFirst().get();
    }

    List<InsurancePot> getCirclePots() {
        var circle = this.game.getCircle();
        return this.game.getPots().stream()
                .filter(v -> v.circle.equals(circle))
                .collect(Collectors.toList());
    }

    List<Player> getPlayers() {
        return this.game.getPlayers();
    }

    List<Card> getCommunityCards() {
        return game.getCommunityCards();
    }

    boolean finished() {
        return this.game.finished();
    }

    public void force() {
        this.scheduler.force();
    }
}
