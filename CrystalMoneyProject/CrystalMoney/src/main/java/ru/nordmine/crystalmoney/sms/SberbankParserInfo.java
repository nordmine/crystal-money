package ru.nordmine.crystalmoney.sms;

import java.util.ArrayList;
import java.util.List;

public class SberbankParserInfo implements SmsParserInfo {

    @Override
    public List<String> getMessagePatterns() {
        List<String> patterns = new ArrayList<String>();
        patterns.add(" покупка на сумму (\\d+\\.\\d+) руб\\. (.*?) выполнена успешно\\.");
        patterns.add(" оплата услуг на сумму (\\d+\\.\\d+) руб\\. (.*?) выполнена успешно\\.");
        return patterns;
    }
}
