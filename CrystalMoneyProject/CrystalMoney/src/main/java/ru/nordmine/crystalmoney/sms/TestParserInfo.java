package ru.nordmine.crystalmoney.sms;

import java.util.ArrayList;
import java.util.List;

public class TestParserInfo implements SmsParserInfo {

    @Override
    public List<String> getMessagePatterns() {
        List<String> patterns = new ArrayList<String>();
        patterns.add(" amount (\\d+\\.\\d+) usd\\. (.*?) successful\\.");
        return patterns;
    }
}
