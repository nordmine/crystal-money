package ru.nordmine.crystalmoney.sms;

public class PatternData {

    private int transactionType;
    private boolean isExchange;
    private String messagePattern;

    public PatternData(int transactionType, boolean isExchange, String messagePattern) {
        this.transactionType = transactionType;
        this.isExchange = isExchange;
        this.messagePattern = messagePattern;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public boolean isExchange() {
        return isExchange;
    }

    public void setExchange(boolean isExchange) {
        this.isExchange = isExchange;
    }

    public String getMessagePattern() {
        return messagePattern;
    }

    public void setMessagePattern(String messagePattern) {
        this.messagePattern = messagePattern;
    }
}
