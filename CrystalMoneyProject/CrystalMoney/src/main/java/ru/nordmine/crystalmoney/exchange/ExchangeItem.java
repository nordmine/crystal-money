package ru.nordmine.crystalmoney.exchange;

public class ExchangeItem {

    private int id;
    private long created;
    private int fromAccountId;
    private int toAccountId;
    private double amount;
    private Integer fromAccountIconId;
    private Integer toAccountIconId;

    public ExchangeItem(int id, long created, int fromAccountId, int toAccountId, double amount, Integer fromAccountIconId, Integer toAccountIconId) {
        this.id = id;
        this.created = created;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
}
