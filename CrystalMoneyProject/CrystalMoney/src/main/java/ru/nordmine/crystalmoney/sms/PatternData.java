package ru.nordmine.crystalmoney.sms;

public class PatternData {

    private String messagePattern;
    private int transactionType;

    public PatternData(int transactionType, String messagePattern) {
        this.messagePattern = messagePattern;
        this.transactionType = transactionType;
    }

    public String getMessagePattern() {
        return messagePattern;
    }

    public void setMessagePattern(String messagePattern) {
        this.messagePattern = messagePattern;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }
}
