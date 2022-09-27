package com.texasthree.account;


import com.texasthree.utility.utlis.DateUtils;
import com.texasthree.utility.utlis.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户信息
 */
@Entity
public class Account {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
    /**
     * 版本
     */
    @Version
    private int version = 0;

    @Column(nullable = false, updatable = false)
    private String name;
    /**
     * 账户余额
     **/
    private BigDecimal balance = BigDecimal.ZERO;
    /**
     * 冻结金额
     **/
    private BigDecimal pendingBalance = BigDecimal.ZERO;
    /**
     * 总收益
     **/
    private BigDecimal totalIncome = BigDecimal.ZERO;
    /**
     * 总支出
     **/
    private BigDecimal totalExpend = BigDecimal.ZERO;
    /**
     * 今日收益
     **/
    private BigDecimal todayIncome = BigDecimal.ZERO;
    /**
     * 今日支出
     **/
    private BigDecimal todayExpend = BigDecimal.ZERO;
    /**
     * 余额上限
     */
    private BigDecimal balanceLimit = BigDecimal.ZERO;


    @Column(nullable = false, updatable = false)
    private LocalDateTime editAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    private boolean enableNegative = false;

    public Account() {
    }

    public Account(String name, boolean enableNegative) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException();
        }

        // 生成账户信息
        this.name = name;
        this.editAt = LocalDateTime.now();
        this.createAt = this.editAt;
        this.enableNegative = enableNegative;
    }

    public AccountStatement credit(BigDecimal amount, String requestNo) {
        assertNonNegative(amount);

        this.balance = balance.add(amount);
        this.totalIncome = totalIncome.add(amount);
        this.todayIncome = todayIncome.add(amount);
        this.setEditAt(LocalDateTime.now());

        return new AccountStatement(this, amount, StatDirection.ADD, requestNo);
    }

    public AccountStatement debit(BigDecimal amount, String requestNo, boolean negative) {
        if (!negative || !this.isEnableNegative()) {
            this.assertAvailable(amount, "账户减款错误");
        }

        this.balance = this.balance.subtract(amount);
        this.todayExpend = this.todayExpend.add(amount);
        this.totalExpend = this.totalExpend.add(amount);
        this.setEditAt(LocalDateTime.now());

        return new AccountStatement(this, amount, StatDirection.SUB, requestNo);
    }

    public void freeze(BigDecimal amount) {
        this.assertAvailable(amount, "冻结账户资金错误");
        this.pendingBalance = this.pendingBalance.add(amount);
    }

    public void unfreeze(BigDecimal amount) {
        assertNonNegative(amount);
        var after = this.pendingBalance.subtract(amount);
        if (after.compareTo(BigDecimal.ZERO) < 0) {
            throw AccountException.ACCOUNT_UN_FROZEN_AMOUNT_OUTLIMIT.newInstance(
                    new StringBuilder("解冻金额超限")
                            .append(" id=").append(this.getId())
                            .append(" pendingBalance=").append(pendingBalance)
                            .append(" amount=").append(amount)
                            .toString()
            );
        }

        this.pendingBalance = after;
    }

    private void assertAvailable(BigDecimal amount, String error) {
        if (!this.availableBalanceIsEnough(amount)) {
            throw AccountException.ACCOUNT_SUB_AMOUNT_OUTLIMIT.newInstance(
                    new StringBuilder(error)
                            .append(" id=").append(this.getId())
                            .append(" available=").append(this.getAvailableBalance())
                            .append(" amount=").append(amount)
                            .toString()
            );
        }
    }

    private void assertNonNegative(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException();
        }
    }


    /**
     * 验证可用余额是否足够
     */
    public boolean availableBalanceIsEnough(BigDecimal amount) {
        assertNonNegative(amount);
        return this.getAvailableBalance().compareTo(amount) >= 0;
    }

    /**
     * 获取可用余额
     */
    public BigDecimal getAvailableBalance() {
        return this.balance.subtract(pendingBalance);
    }

    public BigDecimal getTodayIncome() {
        return this.todayDecimal(this.todayIncome);
    }

    public BigDecimal getTodayExpend() {
        return this.todayDecimal(this.todayExpend);
    }

    private BigDecimal todayDecimal(BigDecimal decimal) {
        // 不是同一天直接清0
        if (this.getEditAt() == null || !DateUtils.isSameDayWithToday(this.getEditAt())) {
            return BigDecimal.ZERO;
        }
        return decimal;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(", accountId=").append(this.getId())
                .append(", avail=").append(this.getAvailableBalance())
                .append(", pendingBalance=").append(pendingBalance)
                .toString();
    }

    public static String getMain(String id) {
        return id.length() == 8 ? id : id.substring(0, 8);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getPendingBalance() {
        return pendingBalance;
    }

    public void setPendingBalance(BigDecimal pendingBalance) {
        this.pendingBalance = pendingBalance;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpend() {
        return totalExpend;
    }

    public void setTotalExpend(BigDecimal totalExpend) {
        this.totalExpend = totalExpend;
    }

    public void setTodayIncome(BigDecimal todayIncome) {
        this.todayIncome = todayIncome;
    }

    public void setTodayExpend(BigDecimal todayExpend) {
        this.todayExpend = todayExpend;
    }

    public BigDecimal getBalanceLimit() {
        return balanceLimit;
    }

    public void setBalanceLimit(BigDecimal balanceLimit) {
        this.balanceLimit = balanceLimit;
    }

    public LocalDateTime getEditAt() {
        return editAt;
    }

    public void setEditAt(LocalDateTime editAt) {
        this.editAt = editAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnableNegative() {
        return enableNegative;
    }

    public void setEnableNegative(boolean enableNegative) {
        this.enableNegative = enableNegative;
    }
}