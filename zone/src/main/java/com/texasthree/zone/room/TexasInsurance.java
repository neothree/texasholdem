package com.texasthree.zone.room;

import com.texasthree.game.texas.Card;
import com.texasthree.game.texas.Insurance;
import com.texasthree.game.texas.InsurancePot;
import com.texasthree.game.texas.Player;

import java.util.List;

/**
 * @author: neo
 * @create: 2022-09-13 12:56
 */
public class TexasInsurance {

    private final Insurance game;

    private final RoundEventHandler handler;

    private final ScheduledEventChecker scheduler = new ScheduledEventChecker();

    private final TexasRound round;

    TexasInsurance(TexasRound round, RoundEventHandler handler) {
        this.round = round;
        this.handler = handler;
        this.game = new Insurance(null, null, null, null, null);
    }

    void start() {
        this.handler.on(round, RoundEvent.INSUSRANCE);
    }

    void buyBegin() {
        this.handler.on(round, RoundEvent.BUYER);

    }

    void buy() {
        this.handler.on(round, RoundEvent.BUY);

    }

    void buyEnd() {
        this.handler.on(round, RoundEvent.BUY_END);
    }

    private void onOpTimeout() {
    }

    private void finish() {

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
