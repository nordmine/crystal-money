package ru.nordmine.crystalmoney.exchange;

import java.math.BigDecimal;

public class ExchangeItem {

    private int id;
    private long created;
    private int fromAccountId;
    private int toAccountId;
    private BigDecimal amount;
    private Integer fromAccountIconId;
    private Integer toAccountIconId;
    private String comment;

    public ExchangeItem(int id, long created, int fromAccountId, int toAccountId, BigDecimal amount, String comment) {
        this.id = id;
        this.created = created;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.comment = comment;
    }

    public ExchangeItem(int id, long created, int fromAccountId, int toAccountId, BigDecimal amount, String comment, Integer fromAccountIconId, Integer toAccountIconId) {
        this(id, created, fromAccountId, toAccountId, amount, comment);
        this.fromAccountIconId = fromAccountIconId;
        this.toAccountIconId = toAccountIconId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public int getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(int fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public int getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(int toAccountId) {
        this.toAccountId = toAccountId;
    }

    public Integer getFromAccountIconId() {
        return fromAccountIconId;
    }

    public void setFromAccountIconId(Integer fromAccountIconId) {
        this.fromAccountIconId = fromAccountIconId;
    }

    public Integer getToAccountIconId() {
        return toAccountIconId;
    }

    public void setToAccountIconId(Integer toAccountIconId) {
        this.toAccountIconId = toAccountIconId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
