package com.texasthree.zone.room;

import com.texasthree.game.texas.Card;
import com.texasthree.game.texas.Optype;
import com.texasthree.zone.room.round.TexasInsurance;
import com.texasthree.zone.room.round.TexasRound;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: neo
 * @create: 2022-08-28 00:07
 */
public class Protocal {

    /**
     * 房间数据
     */
    public static class RoomData {
        public String id;
        public String name;
        public Integer number = 112;
        public Integer capacity;
        public Integer button;
        public Integer smallBlind;
        public Integer ante;
        public List<Seat> seats;
        public RoundData round;

        public RoomData(Room room, String uid) {
            this.id = room.getId();
            this.name = "test";
            this.ante = room.getAnte();
            this.smallBlind = room.getSmallBlind();
            this.button = room.getButton();
            this.capacity = room.getCapacity();
            this.seats = room.getSeats().stream()
                    .filter(com.texasthree.zone.room.Seat::occupied)
                    .map(v -> new Seat(v, room))
                    .collect(Collectors.toList());
            // 牌局
            var round = room.getRound();
            if (round != null) {
                this.round = new Protocal.RoundData(round, uid);
            }
        }
    }

    /**
     * 牌局数据
     */
    public static class RoundData {
        public Integer dealer;
        public Integer sbSeatId;
        public Integer bbSeatId;
        public Integer sumPot;
        public String circle;
        public List<Integer> pots;
        public List<Integer> communityCards;
        public List<Player> players;
        public Operator operator;
        public Insurance insurance;
        public Buyer buyer;

        public RoundData(TexasRound round, String uid) {
            this.dealer = round.dealer();
            this.sbSeatId = round.sbSeatId();
            this.bbSeatId = round.bbSeatId();
            this.sumPot = round.sumPot();
            this.circle = round.circle();
            this.pots = round.getPots();
            this.communityCards = toCardIds(round.getCommunityCards());
            this.players = new ArrayList<>();
            for (var v : round.getPlayers()) {
                var chips = round.getPlayerChips(v.seatId);
                var hand = v.getId().equals(uid) ? round.getPlayerHand(v.seatId) : null;
                var action = round.getAction(v.seatId);
                var info = new Protocal.Player(v.seatId, chips, action, hand);
                this.players.add(info);
            }

            // 操作人
            if (round.getOperator() != null) {
                this.operator = new Protocal.Operator(round);
            }

            // 保险
            var ins = round.getInsurance();
            if (ins != null) {
                this.insurance = new Insurance(ins);
                this.buyer = new Buyer(ins);
            }
        }
    }

    /**
     * 用户
     */
    public static class User {
        public String uid;
        public String name;
        public String avatar;
        public Integer chips;

        public User(com.texasthree.zone.user.User user, Room room) {
            this.uid = user.getId();
            this.name = user.getName();
            this.avatar = user.getAvatar();
            this.chips = room.getUserBalance(user.getId());
        }
    }

    /**
     * 牌局玩家
     */
    public static class Player {
        public Integer seatId;
        public Integer chips;
        public Integer betChips;
        public Optype op;
        public Hand hand;

        public Player(int seatId, int chips, com.texasthree.game.texas.Action action, com.texasthree.game.texas.Hand h) {
            this.seatId = seatId;
            this.chips = chips;

            // 押注
            if (action != null) {
                this.betChips = action.chipsBet;
                this.op = action.op;
            }

            // 主角手牌
            if (h != null) {
                this.hand = new Protocal.Hand(h);
            }
        }
    }

    /**
     * 座位
     */
    public static class Seat {
        public Integer seatId;
        public User user;

        public Seat(com.texasthree.zone.room.Seat s, Room room) {
            this.seatId = s.id;
            if (s.occupied()) {
                this.user = new User(s.getUser(), room);
            }
        }
    }

    /**
     * 手牌
     */
    public static class Hand {
        public List<Integer> cards;
        public String type;
        public List<Integer> best;
        public List<Integer> keys;

        public Hand(com.texasthree.game.texas.Hand h) {
            this.cards = toCardIds(h.getHold());
            this.best = toCardIds(h.getBest());
            this.keys = toCardIds(h.getKeys());
            this.type = h.getType().name();
        }
    }

    /**
     * 开局
     */
    public static class Start {
        public Integer sbSeatId;
        public Integer bbSeatId;
        public Integer dealer;
        public Integer smallBlind;
        public Integer ante;
        public Integer sumPot;
        public List<Integer> players;

        public Start(TexasRound round) {
            this.ante = round.ante();
            this.sbSeatId = round.sbSeatId();
            this.bbSeatId = round.bbSeatId();
            this.dealer = round.dealer();
            this.smallBlind = round.smallBlind();
            this.sumPot = round.sumPot();
            this.players = round.getPlayers().stream()
                    .map(v -> v.seatId)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 押注
     */
    public static class Action {
        public Optype op;
        public Integer seatId;
        public Integer chipsBet;
        public Integer chips;
        public Integer sumPot;

        public Action(com.texasthree.game.texas.Action action, int sumPot) {
            this.op = action.op;
            this.seatId = action.id;
            this.chipsBet = action.chipsBet;
            this.chips = action.chipsLeft;
            this.sumPot = sumPot;
        }

        Action(Optype op, Integer chipsBet) {
            this.op = op;
            this.chipsBet = chipsBet;
        }
    }

    /**
     * 新的操作人
     */
    public static class Operator {
        public Integer seatId;
        public long leftSec;
        public List<Action> actions;

        public Operator(TexasRound round) {
            this.leftSec = round.leftSec();
            this.seatId = round.getOperator().seatId;
            this.actions = round.authority()
                    .entrySet().stream()
                    .map(v -> new Protocal.Action(v.getKey(), v.getValue()))
                    .collect(Collectors.toList());
        }

    }

    /**
     * 一圈结束
     */
    public static class CircleEnd {
        public List<Integer> communityCards;
        public List<Integer> pots;

        public CircleEnd(TexasRound round) {
            this.communityCards = toCardIds(round.getCommunityCards());
            this.pots = round.getPots();
        }
    }

    /**
     * 亮牌
     */
    public static class Showdown {
        public List<Integer> winners;
        public List<ShowdownHand> hands;

        public Showdown(TexasRound round) {
            var result = round.settle();
            this.winners = new ArrayList<>();
            this.hands = new ArrayList<>();
            for (var v : result) {
                var sh = new Protocal.ShowdownHand();
                sh.seatId = v.id;
                sh.profits = v.pot.entrySet().stream()
                        .map(e -> new Protocal.PotProfit(e.getKey(), e.getValue()))
                        .collect(Collectors.toList());
                if (round.isCompareShowdown() && !round.isFold(v.id)) {
                    sh.hand = new Protocal.Hand(round.getPlayerHand(v.id));
                }
                this.hands.add(sh);
            }
        }
    }

    public static class ShowdownHand {
        public Integer seatId;
        public Hand hand;
        public List<PotProfit> profits;

        ShowdownHand() {
        }

        ShowdownHand(com.texasthree.game.texas.Player p) {
            this.seatId = p.getId();
            this.hand = new Hand(p.getHand());
        }
    }

    public static class PotProfit {
        public int potId;
        public int profit;

        PotProfit(int potId, int profit) {
            this.potId = potId;
            this.profit = profit;
        }
    }

    private static List<Integer> toCardIds(List<Card> cards) {
        return cards.stream().map(Card::getId).collect(Collectors.toList());
    }

    /**
     * 保险
     */
    public static class Insurance {
        public List<ShowdownHand> hands;
        public List<Integer> communityCards;

        public Insurance(TexasInsurance ins) {
            this.hands = ins.getPlayers().stream().map(ShowdownHand::new).collect(Collectors.toList());
            this.communityCards = toCardIds(ins.getCommunityCards());
        }
    }

    /**
     * 保险购买人
     */
    public static class Buyer {
        public long leftSec;
        /**
         * 可以购买的保险池
         */
        public List<InsurancePot> buying = new ArrayList<>();
        /**
         * 已经购买的保险池
         */
        public List<InsurancePot> bought = new ArrayList<>();

        public Buyer(TexasInsurance ins) {
            this.leftSec = ins.leftSec();
            var pots = ins.getCirclePots();
            for (var v : pots) {
                if (v.finished()) {
                    var p = new InsurancePot(v.id, v.applicant, v.getAmount());
                    this.bought.add(p);
                } else {
                    this.buying.add(new InsurancePot(v));
                }
            }
        }
    }

    /**
     * 保险池
     */
    static class InsurancePot {
        public Integer potId;
        public Integer amount;
        public Integer seatId;
        public List<Integer> outs;
        public BigDecimal odds;
        public Integer fullPot;
        public Integer breakEven;
        public Integer max;
        public Integer min;

        InsurancePot(int potId, int seatId, Integer amount) {
            this.potId = potId;
            this.amount = amount;
            this.seatId = seatId;
        }

        InsurancePot(com.texasthree.game.insurance.InsurancePot p) {
            this.potId = p.id;
            this.seatId = p.applicant;
            this.outs = toCardIds(p.getOuts());
            this.odds = p.getOdds();
            this.fullPot = p.fullPot();
            this.breakEven = p.breakEven();
            this.max = p.getMax();
            this.min = p.getMin();
        }
    }

    /**
     * 保险购买结束
     */
    public static class BuyEnd extends Insurance {
        public Integer seatId;
        public Integer amount;

        public BuyEnd(TexasInsurance ins, int seatId, Integer amount) {
            super(ins);
            this.seatId = seatId;
            this.amount = amount;
        }
    }

    /**
     * 房间排名
     */
    public static class Rank {
        public final int insurance;
        public final List<Buyin> buyins;

        public Rank(Room room) {
            this.insurance = room.getInsurance();
            // 按照 balance, sum 降序排列
            this.buyins = room.buyins().stream()
                    .sorted(Comparator.comparing(com.texasthree.zone.room.Buyin::getProfit, Comparator.reverseOrder())
                            .thenComparing(com.texasthree.zone.room.Buyin::getSum, Comparator.reverseOrder()))
                    .map(Buyin::new)
                    .collect(Collectors.toList());
        }
    }

    public static class Buyin {
        public String name;
        public int buyin;
        public int profit;
        public boolean settle;

        Buyin(com.texasthree.zone.room.Buyin v) {
            this.name = v.getName();
            this.buyin = v.getSum();
            this.profit = v.getProfit();
            this.settle = v.isSettle();
        }
    }
}
