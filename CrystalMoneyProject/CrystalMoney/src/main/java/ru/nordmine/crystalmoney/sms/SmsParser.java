package ru.nordmine.crystalmoney.sms;

import java.util.List;
import java.util.regex.Matcher;

public interface SmsParser {

    public static final int INCOME = 1;
    public static final int OUTCOME = 2;

    public List<PatternData> getMessagePatterns();
    public ParsingResult getParsingResult(Matcher matcher);

}
