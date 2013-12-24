package ru.nordmine.crystalmoney.sms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class MtsBankParser implements SmsParser {

    @Override
    public List<PatternData> getMessagePatterns() {
        ResultParser twoGroups = new TwoGroupsResultParser();
        ResultParser threeGroups = new ThreeGroupsResultParser();
        List<PatternData> patterns = new ArrayList<PatternData>();
        patterns.add(new PatternData(INCOME, false, "Приход по счету карты \\w+ \\*(\\d{4}); ([\\s\\d,]+) RUB;", twoGroups));
        patterns.add(new PatternData(INCOME, false, "Prikhod for account \\w+ \\*(\\d{4}); ([\\s\\d,]+) RUB;", twoGroups));
        patterns.add(new PatternData(INCOME, true, "Пополнение \\w+ \\*(\\d{4}); \\d{2}\\.\\d{2} \\d{2}:\\d{2}; (.*?); ([\\d\\s\\,]+) RUB;", threeGroups));
        patterns.add(new PatternData(OUTCOME, false, "Оплата \\w+ \\*(\\d{4}); \\d{2}\\.\\d{2} \\d{2}:\\d{2}; (.*?); ([\\d\\s\\,]+) RUB;", threeGroups));
        patterns.add(new PatternData(OUTCOME, false, "Oplata \\w+ \\*(\\d{4}); \\d{2}\\.\\d{2} \\d{2}:\\d{2}; (.*?); ([\\d\\s\\,]+) RUB;", threeGroups));
        patterns.add(new PatternData(OUTCOME, true, "Наличные \\w+ \\*(\\d{4}); \\d{2}\\.\\d{2} \\d{2}:\\d{2}; (.*?); ([\\d\\s\\,]+) RUB;", threeGroups));
        return patterns;
    }

    private class TwoGroupsResultParser implements ResultParser {

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

    private class ThreeGroupsResultParser implements ResultParser {

        @Override
        public ParsingResult getParsingResult(Matcher matcher) {
            if (matcher.groupCount() == 3) {
                ParsingResult result = new ParsingResult();
                result.setCardNumber(matcher.group(1));
                result.setComment(matcher.group(2));
                result.setAmount(new BigDecimal(matcher.group(3).replaceAll(" ", "").replace(",", ".")));
                return result;
            }
            return null;
        }
    }
}
