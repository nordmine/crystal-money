package ru.nordmine.crystalmoney.sms;

import java.util.regex.Matcher;

public interface ResultParser {

    ParsingResult getParsingResult(Matcher matcher);

}
