package com.texasthree.round.texas;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
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

    private List<Card> all = new ArrayList<Card>();


    private TableCard() {
        String fileName = this.getClass().getClassLoader().getResource("table_card.json").getPath();
        try {
            File jsonFile = new File(fileName);

            FileReader fileReader = new FileReader(fileName);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            JSONArray array = JSONArray.parseArray(sb.toString());
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                all.add(new Card(obj.getString("name"), obj.getInteger("point"), obj.getInteger("suit")));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
