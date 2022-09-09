package com.texasthree.zone.room;


import com.texasthree.zone.user.User;

/**
 * @author: neo
 * @create: 2022-09-08 16:44
 */
class RoomEventHandler {

    private final Room room;

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

    private void onSeat(User user, int seatId) {
        var seat = room.getSeat(seatId);
        var info = new Protocal.Seat(seat);
        if (!seat.occupied()) {
            room.send(user.getId(), info);
        }
        room.send(info);
    }
}
