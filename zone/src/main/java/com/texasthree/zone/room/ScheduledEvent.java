package com.texasthree.zone.room;

/**
 * 定时事件
 */
public class ScheduledEvent {

    private Runnable command;

    private long nextMsec;

    private boolean done;

    public ScheduledEvent(Runnable command, int delay) {
        this.command = command;
        this.nextMsec = System.currentTimeMillis() + delay;
    }

    public void check() {
        if (!done && System.currentTimeMillis() >= nextMsec) {
            this.run();
        }
    }

    public long getNextMsec() {
        return this.nextMsec;
    }

    public void force() {
        if (!done) {
            this.run();
        }
    }

    private void run() {
        this.done = true;
        this.command.run();
    }

}
