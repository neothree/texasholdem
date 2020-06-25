package com.texasthree.round;

import com.texasthree.round.texas.Card;
import com.texasthree.round.texas.TableCard;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * TableCard Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 16, 2020</pre>
 */
public class TableCardTest {

    /**
     * Method: getInstance()
     */
    @Test
    public void testGetInstance() throws Exception {
        List<Card> pair = TableCard.getInstance().shuffle();
        Set<Integer> set = new HashSet<>();
        pair.forEach(v -> set.add(v.getId()));
        assertEquals(54, set.size());
    }

    @Test
    public void testRpc() throws Exception {
        GrpcClient client = new GrpcClient("127.0.0.1", 6566);
        client.send("hello", "world");
    }
}