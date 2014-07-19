package ru.nordmine.crystalmoney.sms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class AlfabankParser implements SmsParser {

    @Override
    public List<PatternData> getMessagePatterns() {
        ResultParser parser = new AlfabankResultParser();
        List<PatternData> patterns = new ArrayList<PatternData>();
        patterns.add(new PatternData(INCOME, false, "\\d\\*(\\d{4}); Postupleniye; Summa: ([\\d,]+) RUR;", parser));
        patterns.add(new PatternData(OUTCOME, true, "\\d\\*(\\d{4}); Vydacha nalichnyh; Uspeshno; Summa: ([\\d,]+) RUR; Ostatok: [\\d,]+ RUR; (.*?);", parser));
        return patterns;
    }

    private class AlfabankResultParser implements ResultParser {

        @Override
        public ParsingResult getParsingResult(Matcher matcher) {
            if (matcher.groupCount() >= 2) {
                ParsingResult result = new ParsingResult();
                result.setCardNumber(matcher.group(1));
                result.setAmount(new BigDecimal(matcher.group(2).replace(",", ".")));
                if (matcher.groupCount() == 3) {
                    result.setComment(matcher.group(3));
                }
                return result;
            }
            return null;
        }
    }
}
