package com.texasthree.zone.room;


import com.texasthree.zone.user.User;

/**
 * @author: neo
 * @create: 2022-09-08 16:44
 */
class RoomEventHandler {

    private Room room;

    RoomEventHandler(Room room) {
        this.room = room;
    }

    void on(RoomEvent event, User user, int seatId) {
        switch (event) {
            case SEAT:
                this.onSeat(user, seatId);
                break;
            default:
                throw new IllegalArgumentException(event.name());
        }
    }

    private void onSeat(User user, Integer seatId) {
        var info = new Protocal.Seat();
        info.seatId = seatId;
        if (room.getSeat(seatId).occupied()) {
            var p = new Protocal.Player();
            p.uid = user.getId();
            p.name = user.getName();
            p.chips = user.getChips();
            info.player = p;
        } else {
            room.send(user.getId(), info);
        }
        room.send(info);
    }
}
