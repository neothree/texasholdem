package com.texasthree.appzone.room;


import com.texasthree.appzone.protocal.RoomProtocal;

/**
 * @author: neo
 * @create: 2022-09-08 16:44
 */
class RoomEventHandler {

    private final Room room;

    RoomEventHandler(Room room) {
        this.room = room;
    }

    void on(RoomEvent event, int seatId) {
        switch (event) {
            case SEAT:
                this.onSeat(seatId);
                break;
            default:
                throw new IllegalArgumentException(event.name());
        }
    }

    private void onSeat(int seatId) {
        var info = new RoomProtocal.Seat(room.getSeat(seatId), room);
        room.send(info);
    }
}
