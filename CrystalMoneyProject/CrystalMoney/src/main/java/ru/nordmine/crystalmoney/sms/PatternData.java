package ru.nordmine.crystalmoney.sms;

public class PatternData {

    private int transactionType;
    private boolean isExchange;
    private String messagePattern;
    private ResultParser resultParser;

    public PatternData(int transactionType, boolean isExchange, String messagePattern, ResultParser resultParser) {
        this.transactionType = transactionType;
        this.isExchange = isExchange;
        this.messagePattern = messagePattern;
        this.resultParser = resultParser;
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

    public ResultParser getResultParser() {
        return resultParser;
    }

    public void setResultParser(ResultParser resultParser) {
        this.resultParser = resultParser;
    }
}
