package com.texasthree.round;

import com.texasthree.round.texas.Action;
import com.texasthree.round.texas.Player;

import java.util.List;

public class RoundState {
    public String move;
    public List<Player> players;
    public List<Action> ops;
    public int raiseLine;
    public List<Integer> board;
    public List<Integer> devide;
    public List<Action> actions;
}
