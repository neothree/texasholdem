package com.texasthree.room;

/**
 * @author : guoqing
 * create at:  2020-06-29  17:52
 * @description:
 */
public class ScheduledEvent {

    private Runnable command;

    private int delay;

    private int nextMsec ;

    public ScheduledEvent(Runnable command, int delay) {
        this.command = command;
        this.delay = delay;
    }

    public void check() {

    }

    public long getNextMsec() {
        return this.nextMsec;
    }
}
