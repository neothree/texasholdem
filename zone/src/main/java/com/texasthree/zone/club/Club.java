package com.texasthree.zone.club;

/**
 * 俱乐部
 *
 * @author: neo
 * @create: 2022-09-22 13:12
 */
public class Club {

    private ClubData data;

    public Club(ClubData data) {
        this.data = data;
    }

    public String getId() {
        return this.data.getId();
    }

    public String getFundId() {
        return this.data.getFundId();
    }

    public String getBalanceId() {
        return this.data.getBalanceId();
    }

    public String getName() {
        return this.data.getName();
    }

    public String getCreator() {
        return this.data.getCreator();
    }

    public String getAvatar() {
        return this.data.getAvatar();
    }

    public int getCapacity() {
        return this.data.getCapacity();
    }

    void setData(ClubData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}
