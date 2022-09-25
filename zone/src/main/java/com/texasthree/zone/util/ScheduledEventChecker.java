package com.texasthree.zone.util;

/**
 * @author: neo
 * @create: 2022-08-31 17:34
 */
public class ScheduledEventChecker {

    private ScheduledEvent opEvent;

    public void once(Runnable command, int delay) {
        this.opEvent = new ScheduledEvent(command, delay);
    }

    public void check() {
        if (this.opEvent != null) {
            this.opEvent.check();
        }
    }

    public void force() {
        if (this.opEvent != null) {
            this.opEvent.force();
        }
    }

    public void clear() {
        this.opEvent = null;
    }

    public int leftSec() {
        if (opEvent == null) {
            return 0;
        }
        return (int) ((opEvent.getNextMsec() - System.currentTimeMillis()) / 1000);
    }
}
