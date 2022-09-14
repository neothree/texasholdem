package com.texasthree.zone.room;

import com.texasthree.game.texas.Card;
import com.texasthree.game.texas.Insurance;
import com.texasthree.game.texas.InsurancePot;
import com.texasthree.game.texas.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: neo
 * @create: 2022-09-13 12:56
 */
public class TexasInsurance {

    private static Logger log = LoggerFactory.getLogger(TexasInsurance.class);

    private final Insurance game;

    private final RoundEventHandler handler;

    private final ScheduledEventChecker scheduler = new ScheduledEventChecker();

    private final TexasRound round;

    private final Runnable onFinish;

    TexasInsurance(TexasRound round, RoundEventHandler handler, Runnable onFinished) {
        this.round = round;
        this.handler = handler;
        this.onFinish = onFinished;
        var players = new ArrayList<Player>();
        for (var v : round.players()) {
            if (!v.isLeave() && round.isFold(v.getId())) {
                players.add(v);
            }
        }
        this.game = new Insurance(players, round.getLeftCard(), round.circle(), round.getDivides());
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
    void buyBegin() {
        log.info(">>>>>>>>>>>>>>>>> 一轮购买开始 : {}<<<<<<<<<<<<<<<<<<<<<<<<", game.getCircle());
        if (!this.game.circleFinished()) {
            this.scheduler.once(this::buyEnd, 2 * 1000);
            this.handler.on(round, RoundEvent.BUYER);
        } else {
            // 无法购买，直接下一轮
            this.scheduler.once(this::buyEnd, 20 * 1000);
        }
    }

    /**
     * 购买
     */
    void buy(int potId, int amount, List<Card> outs) {
        log.info("购买保险");
        this.game.buy(potId, amount, outs);
        this.handler.on(round, RoundEvent.BUY);
        if (!this.game.circleFinished()) {
            // 这轮购买结束
            this.scheduler.once(this::buyEnd, 2 * 1000);
        }
    }

    /**
     * 一轮结束
     */
    void buyEnd() {
        log.info(">>>>>>>>>>>>>>>>> 一轮保险结束 : {} <<<<<<<<<<<<<<<<<<<<<<<<", game.getCircle());
        this.game.end();
        this.handler.on(round, RoundEvent.BUY_END);
        if (!this.game.finished()) {
            // 下一轮开始
            this.scheduler.once(this::buyBegin, 1000);
        } else {
            // 整体结束
            this.scheduler.once(this::finish, 1000);
        }
    }

    private void finish() {
        log.info(">>>>>>>>>>>>>>>>> 保险阶段结束 <<<<<<<<<<<<<<<<<<<<<<<<");
        this.scheduler.clear();
        this.onFinish.run();
    }


    public void loop() {
        this.scheduler.check();
    }

    public int leftSec() {
        return this.scheduler.leftSec();
    }

    private void updateHand() {

    }

    List<InsurancePot> getPots() {
        return game.getPots();
    }

    List<Player> getPlayers() {
        return this.game.getPlayers();
    }

    List<Card> getCommunityCards() {
        return game.getCommunityCards();
    }
}