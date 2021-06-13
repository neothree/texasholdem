package com.texasthree.round.texas;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 分池
 *
 * @author: neo
 * @create: 2021-06-13 17:38
 */
@Data
public class Divide {
    private int chips;

    private Map<Integer, Integer> members = new HashMap<>();

    private Map<Integer, Integer> putin = new HashMap<>();
}
