package com.texasthree.core.service.impl;

import com.texasthree.core.app.Game;
import com.texasthree.core.app.GameRoom;
import com.texasthree.core.app.Player;
import com.texasthree.core.app.impl.DefaultPlayer;
import com.texasthree.core.service.LookupService;
import com.texasthree.core.util.Credentials;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * The lookup service abstracts away the implementation detail on getting the
 * game objects from the reference key provided by the client. This lookup is
 * now done from a hashmap but can be done from database or any other manner.
 *
 * @author Abraham Menacherry
 */
@Service
public class SimpleLookupService implements LookupService {
    private final Map<String, GameRoom> refKeyGameRoomMap;

    public SimpleLookupService() {
        refKeyGameRoomMap = new HashMap<String, GameRoom>();
    }

    public SimpleLookupService(Map<String, GameRoom> refKeyGameRoomMap) {
        super();
        this.refKeyGameRoomMap = refKeyGameRoomMap;
    }

    @Override
    public Game gameLookup(Object gameContextKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GameRoom gameRoomLookup(Object gameContextKey) {
        return refKeyGameRoomMap.get((String) gameContextKey);
    }

    @Override
    public Player playerLookup(Credentials loginDetail) {
        return new DefaultPlayer();
    }

    public Map<String, GameRoom> getRefKeyGameRoomMap() {
        return refKeyGameRoomMap;
    }


}
