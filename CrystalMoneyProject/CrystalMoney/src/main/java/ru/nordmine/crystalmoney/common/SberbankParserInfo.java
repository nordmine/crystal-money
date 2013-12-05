package ru.nordmine.crystalmoney.common;

import java.util.ArrayList;
import java.util.List;

public class SberbankParserInfo implements SmsParserInfo {

    @Override
    public List<String> getMessagePatterns() {
        List<String> patterns = new ArrayList<String>();
        patterns.add(" на сумму (\\d+\\.\\d+) руб\\. (.*?) выполнена успешно\\.");
        return patterns;
    }
}
