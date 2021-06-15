package com.texasthree.round.texas;

import lombok.Data;

/**
 * 数据统计
 *
 * @author: neo
 * @create: 2021-06-14 15:07
 */
@Data
public class Statistic {
    Integer id;
    boolean allin;
    boolean allinWin;
    boolean flopRaise;
    boolean inpot;
}
