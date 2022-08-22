package com.texasthree.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.game.texas.Card;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Deck {
    private static Deck instance;

    public static Deck getInstance() {
        if (instance == null) {
            instance = new Deck();
        }
        return instance;
    }

    private List<Card> all;


    private Deck() {
        var inputStream = this.getClass().getClassLoader().getResourceAsStream("table_card.json");
        try {
            var reader = new InputStreamReader(inputStream, "utf-8");
            var ch = 0;
            var sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            inputStream.close();
            reader.close();

            all = new ArrayList<>();
            var mapper = new ObjectMapper();
            var cards = mapper.readTree(sb.toString());
            for (var v : cards) {
                var card = new Card(v.get("name").asText(), v.get("point").asInt(), v.get("suit").asInt());
                if (card.suit <= 4) {
                    all.add(card);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    public List<Card> shuffle() {
        var pair = new ArrayList<>(all);
        for (int i = 0; i < 3; i++) {
            Collections.shuffle(pair);
        }
        return pair;
    }

    public Card getCardById(int id) {
        return this.all.stream().filter(v -> v.getId() == id).findFirst().orElseThrow();
    }

    public List<Card> getAll() {
        return new ArrayList<>(all);
    }

}
