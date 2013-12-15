package ru.nordmine.crystalmoney.sms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class MtsBankParser implements SmsParser {

    @Override
    public List<PatternData> getMessagePatterns() {
        List<PatternData> patterns = new ArrayList<PatternData>();
        patterns.add(new PatternData(INCOME, false, "Приход по счету карты \\w+ \\*(\\d{4}); ([\\s\\d,]+) RUB;"));
        patterns.add(new PatternData(OUTCOME, true, "Наличные \\w+ \\*(\\d{4}); \\d{2}\\.\\d{2} \\d{2}:\\d{2}; .*?; ([\\d\\s\\,]+) RUB;"));
        patterns.add(new PatternData(OUTCOME, false, "Оплата \\w+ \\*(\\d{4}); \\d{2}\\.\\d{2} \\d{2}:\\d{2}; .*?; ([\\d\\s\\,]+) RUB;"));
        return patterns;
    }

    @Override
    public ParsingResult getParsingResult(Matcher matcher) {
        if (matcher.groupCount() == 2) {
            ParsingResult result = new ParsingResult();
            result.setCardNumber(matcher.group(1));
            result.setAmount(new BigDecimal(matcher.group(2).replaceAll(" ", "").replace(",", ".")));
            return result;
        }
        return null;
    }
}
