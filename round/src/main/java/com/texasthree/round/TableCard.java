package com.texasthree.round;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.round.texas.Card;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TableCard {
    private static TableCard instance;

    public static TableCard getInstance() {
        if (instance == null) {
            instance = new TableCard();
        }
        return instance;
    }

    private List<Card> all;


    private TableCard() {
        var fileName = this.getClass().getClassLoader().getResource("table_card.json").getPath();
        try {
            var fileReader = new FileReader(fileName);
            var reader = new InputStreamReader(new FileInputStream(new File(fileName)), "utf-8");
            var ch = 0;
            var sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();

            all = new ArrayList<>();
            var mapper = new ObjectMapper();
            var cards = mapper.readTree(sb.toString());
            for (var v : cards) {
                all.add(new Card(v.get("name").asText(), v.get("point").asInt(), v.get("suit").asInt()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    public List<Card> shuffle() {
        List<Card> pair = new ArrayList<Card>();
        pair.addAll(all);

        for (int i = 0; i < 3; i++) {
            Collections.shuffle(pair);
        }
        return pair;
    }

    public Card getCardById(int id) {
        for (Card v : all) {
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
    }

}
