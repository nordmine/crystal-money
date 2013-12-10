package ru.nordmine.crystalmoney.sms;

import android.util.Log;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class TestParser implements SmsParser {

    @Override
    public List<PatternData> getMessagePatterns() {
        List<PatternData> patterns = new ArrayList<PatternData>();
        patterns.add(new PatternData(INCOME, "\\w+(\\d{4}): (\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}) amount (\\d+\\.\\d+) usd\\. Shop - (.*?) successful\\."));
        patterns.add(new PatternData(INCOME, "Income to account \\w+ \\*(\\d{4}); ([\\s\\d,]+) RUB;"));
        return patterns;
    }
/*
    @Override
    public ParsingResult getParsingResult(Matcher matcher) {
        if (matcher.groupCount() == 4) {
            ParsingResult result = new ParsingResult();
            result.setCardNumber(matcher.group(1));
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm");
            try {
                result.setDate(format.parse(matcher.group(2)));
            } catch (ParseException e) {
                Log.d(this.getClass().toString(), "error while parsing date in sms");
            }
            result.setAmount(new BigDecimal(matcher.group(3)));
            result.setComment(matcher.group(4));
            return result;
        }
        return null;
    }
    */

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
