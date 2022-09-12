package com.texasthree.game.texas;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceTest {

    @Test
    public void testOdds() {
        assertEquals(0, Insurance.odds(0).compareTo(BigDecimal.ZERO));
        for (var count = 1; count < 15; count++) {
            assertEquals(1, Insurance.odds(count).compareTo(BigDecimal.ZERO));
        }
        assertEquals(0, Insurance.odds(15).compareTo(BigDecimal.ZERO));
    }
}