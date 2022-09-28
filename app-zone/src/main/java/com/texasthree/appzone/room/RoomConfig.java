package com.texasthree.appzone.room;

/**
 * @author: neo
 * @create: 2022-08-09 14:54
 */
public class RoomConfig {

    private String id;

    private String name;
    /**
     * 小盲
     */
    private int smallBlind = 1;
    /**
     * 容量（人数）
     */
    private int capacity = 2;
    /**
     * 前注
     */
    private int ant = 0;
    /**
     * 时长（单位：分钟）
     */
    private int duration = 30;
    /**
     * 保险
     */
    private boolean insurance = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSmallBlind() {
        return smallBlind;
    }

    public void setSmallBlind(int smallBlind) {
        this.smallBlind = smallBlind;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAnt() {
        return ant;
    }

    public void setAnt(int ant) {
        this.ant = ant;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isInsurance() {
        return insurance;
    }

    public void setInsurance(boolean insurance) {
        this.insurance = insurance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
