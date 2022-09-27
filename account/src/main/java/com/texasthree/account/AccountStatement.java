package com.texasthree.account;


import com.texasthree.utility.utlis.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户流水
 */
@Entity
public class AccountStatement {
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
    /**
     * 账户编号
     **/
    private String accountId;
    /**
     * 货币
     */
    private String currency;

    /**
     * 金额
     **/
    private BigDecimal amount;

    /**
     * 账户余额
     **/
    private BigDecimal balance;

    /**
     * 资金变动方向
     **/
    private String dir;

    /**
     * 请求号
     **/
    private String requestNo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    public AccountStatement() {
    }

    public AccountStatement(Account account, BigDecimal amount, StatDirection direction, String requestNo) {
        if (account == null
                || amount == null
                || direction == null
                || StringUtils.isEmpty(requestNo)) {
            throw new IllegalArgumentException();
        }

        this.createAt = LocalDateTime.now();
        this.amount = amount;
        this.dir = direction.name();
        this.requestNo = requestNo;
        this.balance = account.getBalance();
        this.accountId = account.getId();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("id=").append(this.getAccountId())
                .append(", amount=").append(amount.stripTrailingZeros())
                .append(", balance=").append(balance.stripTrailingZeros())
                .append(", currency=").append(this.currency)
                .append(", dir=").append(this.dir)
                .toString();
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

}