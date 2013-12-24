package ru.nordmine.crystalmoney.sms;

import java.util.List;

public interface SmsParser {

    public static final int INCOME = 1;
    public static final int OUTCOME = 2;

    public List<PatternData> getMessagePatterns();

}
