package com.texasthree.round;

import com.texasthree.round.texas.Action;
import com.texasthree.round.texas.Move;
import com.texasthree.round.texas.Player;

import java.util.List;

public class RoundState {
    public Move move;
    public List<Player> players;
    public List<Action> ops;
    public int raiseLine;
    public List<Integer> board;
    public List<Integer> devide;
    public List<Action> actions;
}
