package ru.nordmine.crystalmoney.sms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class SberbankParser implements SmsParser {

    @Override
    public List<PatternData> getMessagePatterns() {
        List<PatternData> patterns = new ArrayList<PatternData>();
        String cardNumberWithDatePattern = "\\w+(\\d{4}): \\d{2}\\.\\d{2}\\.\\d{2}";
        String cardNumberWithDateTimePattern = cardNumberWithDatePattern + " \\d{2}:\\d{2}";
        patterns.add(new PatternData(OUTCOME, false, cardNumberWithDateTimePattern + " покупка на сумму (\\d+\\.\\d+) руб\\. (.*?) выполнена успешно\\."));
        patterns.add(new PatternData(OUTCOME, false, cardNumberWithDateTimePattern + " оплата услуг на сумму (\\d+\\.\\d+) руб\\. (.*?) выполнена успешно\\."));
        patterns.add(new PatternData(INCOME, true, cardNumberWithDateTimePattern + " операция зачисления на сумму (\\d+\\.\\d+) руб\\. (.*?) выполнена успешно\\."));
        patterns.add(new PatternData(OUTCOME, false, cardNumberWithDatePattern + " оплата Мобильного банка за (.*?) на сумму (\\d+\\.\\d+) руб\\. выполнена успешно\\."));
        return patterns;
    }

    @Override
    public ParsingResult getParsingResult(Matcher matcher) {
        if (matcher.groupCount() >= 2) {
            ParsingResult parsingResult = new ParsingResult();
            parsingResult.setCardNumber(matcher.group(1));
            parsingResult.setAmount(new BigDecimal(matcher.group(2)));
            if (matcher.groupCount() >= 3) {
                parsingResult.setComment(matcher.group(3));
            }
            return parsingResult;
        }
        return null;
    }
}
