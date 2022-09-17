package com.texasthree.game.texas;

import com.texasthree.game.AllCard;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest extends AllCard {

    @Test
    public void testPlayer() {
        var id = 1;
        var chips = 200;
        var player = new Player(id, chips);
        assertEquals(1, player.getId());
        assertEquals(chips, player.getChips());

        player.minus(20);
        assertEquals(180, player.getChips());
    }

    @Test
    public void testLeave() {
        var player = make();
        assertTrue(player.inGame());
        assertFalse(player.isLeave());

        player.leave();
        assertFalse(player.inGame());
        assertTrue(player.isLeave());
    }

    @Test
    public void testWinners() {
        var players = Arrays.asList(
                make(0, diamondA, heartA),
                make(1, diamond2, heart2),
                make(2, diamond3, heart3),
                make(3, diamond4, heart4),
                make(4, diamond5, heart5)
        );

        assertWinner(players, Arrays.asList(club5, club10, spadesA), 0);
        assertWinner(players, Arrays.asList(club5, club10, spadesA, spades5), 4);
        assertWinner(players, Arrays.asList(club5, club10, spadesA, spades5, spades4), 4);

        players = Arrays.asList(
                make(0, diamondA, heartA),
                make(1, diamond2, heart2),
                make(2, diamond3, heart3),
                make(3, diamond4, heart4),
                make(4, clubA, spadesA)
        );
        assertWinner(players, Arrays.asList(club5, club10, spades10), 0, 4);
    }

    void assertWinner(List<Player> players, List<Card> cards, Integer... expect) {
        players.forEach(v -> v.getHand().fresh(cards));
        var winners = Player.winners(players);
        assertEquals(expect.length, winners.size());
        var set = winners.stream().map(Player::getId).collect(Collectors.toSet());
        assertTrue(set.containsAll(Arrays.asList(expect)));
    }

    private Player make() {
        return new Player(1, 100);
    }

    private Player make(int id, Card c0, Card c1) {
        var p = new Player(id, 100);
        p.setHand(new Hand(Arrays.asList(c0, c1)));
        return p;
    }
}