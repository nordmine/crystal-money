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
        ResultParser commentLast = new CommentLastResultParser();
        ResultParser commentInMiddle = new CommentInMiddleResultParser();
	    // todo поправить другие шаблоны при необходимости
        patterns.add(new PatternData(OUTCOME, false, cardNumberWithDateTimePattern + " покупка на сумму (\\d+\\.\\d+)\\s?р\\. (.*?) Баланс", commentLast));
        patterns.add(new PatternData(OUTCOME, false, cardNumberWithDateTimePattern + " оплата услуг на сумму (\\d+\\.\\d+)\\s?р\\. (.*?) Баланс", commentLast));
        patterns.add(new PatternData(OUTCOME, true, cardNumberWithDateTimePattern + " выдача наличных на сумму (\\d+\\.\\d+) руб\\. (.*?) выполнена успешно\\.", commentLast));
        patterns.add(new PatternData(INCOME, false, cardNumberWithDateTimePattern + " операция зачисления на сумму (\\d+\\.\\d+) руб\\. PEREVOD WEB-BANK (.*?) выполнена успешно\\.", commentLast));
        patterns.add(new PatternData(INCOME, true, cardNumberWithDateTimePattern + " операция зачисления на сумму (\\d+\\.\\d+) руб\\. (.*?) выполнена успешно\\.", commentLast));
        patterns.add(new PatternData(OUTCOME, false, cardNumberWithDateTimePattern + " операция списания на сумму (\\d+\\.\\d+) руб\\. (.*?) выполнена успешно\\.", commentLast));
        patterns.add(new PatternData(OUTCOME, false, cardNumberWithDatePattern + " оплата Мобильного банка за (.*?) на сумму (\\d+\\.\\d+)\\s?р\\. Баланс", commentInMiddle));
        return patterns;
    }

    private class CommentLastResultParser implements ResultParser {

        @Override
        public ParsingResult getParsingResult(Matcher matcher) {
            if (matcher.groupCount() == 3) {
                ParsingResult parsingResult = new ParsingResult();
                parsingResult.setCardNumber(matcher.group(1));
                parsingResult.setAmount(new BigDecimal(matcher.group(2)));
                parsingResult.setComment(matcher.group(3));
                return parsingResult;
            }
            return null;
        }
    }

    private class CommentInMiddleResultParser implements ResultParser {

        @Override
        public ParsingResult getParsingResult(Matcher matcher) {
            if (matcher.groupCount() == 3) {
                ParsingResult parsingResult = new ParsingResult();
                parsingResult.setCardNumber(matcher.group(1));
                parsingResult.setComment(matcher.group(2));
                parsingResult.setAmount(new BigDecimal(matcher.group(3)));
                return parsingResult;
            }
            return null;
        }
    }
}
