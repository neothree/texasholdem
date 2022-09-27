package com.texasthree.account;

public class AccountException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final AccountException ACCOUNT_UNACTIVE = new AccountException(100, "账户已被冻结");

    public static final AccountException ACCOUNT_SUB_AMOUNT_OUTLIMIT = new AccountException(101, "余额不足");

    public static final AccountException ACCOUNT_UN_FROZEN_AMOUNT_OUTLIMIT = new AccountException(102, "解冻金额超限");

    /**
     * 具体异常码
     */
    protected int code;

    public AccountException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public AccountException newInstance() {
        return new AccountException(this.code, this.getMessage());
    }

    public AccountException newInstance(String msg) {
        return new AccountException(this.code, msg);
    }

    public AccountException print() {
        return this;
    }

    public int getCode() {
        return code;
    }
}
