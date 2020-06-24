package com.texasthree.core.event;


public interface EventHandler {
    /**
     * On event
     *
     * @param event
     */
    public void onEvent(Event event);

    public int getEventType();

}
