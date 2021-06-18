package com.texasthree.game;

import com.texasthree.game.texas.Action;
import com.texasthree.game.texas.Player;

import java.util.List;

public class GameState {
    public String move;
    public List<Player> players;
    public List<Action> ops;
    public int raiseLine;
    public List<Integer> board;
    public List<Integer> divides;
    public List<Action> actions;
}
