package com.texasthree.appzone.round;

/**
 * @author: neo
 * @create: 2022-09-24 18:25
 */
public class TexasEvent {

    private final TexasRound round;

    private final Object value;

    TexasEvent(TexasRound round) {
        this(round, null);
    }

    TexasEvent(TexasRound round, Object value) {
        this.round = round;
        this.value = value;
    }

    TexasRound getRound() {
        return round;
    }

    Object getValue() {
        return value;
    }
}
