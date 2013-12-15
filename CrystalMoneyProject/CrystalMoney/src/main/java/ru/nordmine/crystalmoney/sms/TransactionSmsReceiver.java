package ru.nordmine.crystalmoney.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountDao;
import ru.nordmine.crystalmoney.account.AccountItem;
import ru.nordmine.crystalmoney.category.CategoryDao;
import ru.nordmine.crystalmoney.category.CategoryItem;
import ru.nordmine.crystalmoney.exchange.ExchangeDao;
import ru.nordmine.crystalmoney.exchange.ExchangeItem;
import ru.nordmine.crystalmoney.trx.TransactionDao;
import ru.nordmine.crystalmoney.trx.TransactionItem;

public class TransactionSmsReceiver extends BroadcastReceiver {

    private static final String SBERBANK = "900";
    private static final String MTS_BANK1 = "+79660359487";
    private static final String MTS_BANK2 = "+79922000889";

    private Map<String, SmsParser> getParsers() {
        Map<String, SmsParser> parsers = new HashMap<String, SmsParser>();
        parsers.put(SBERBANK, new SberbankParser());
        MtsBankParser mtsBankParser = new MtsBankParser();
        parsers.put(MTS_BANK1, mtsBankParser);
        parsers.put(MTS_BANK2, mtsBankParser);
        return parsers;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            SmsMessage[] messages = getSmsMessages(bundle);
            if (messages.length == 0) {
                Log.d(this.getClass().toString(), "No messages received");
                return;
            }

            StringBuilder completeTextMessage = new StringBuilder("");
            String from = messages[0].getOriginatingAddress();

            for (SmsMessage message : messages) {
                if (from.equals(message.getOriginatingAddress())) {
                    completeTextMessage.append(message.getMessageBody());
                } else {
                    completeTextMessage = null;
                    break; // номера отправителей не совпадают
                }
            }

            if (completeTextMessage == null) {
                Log.d(this.getClass().toString(), "Multiple sender");
                return;
            }

            Map<String, SmsParser> parsers = getParsers();

            if (parsers.containsKey(from)) {
                String completeMessage = completeTextMessage.toString();
                SmsParser smsParser = parsers.get(from);

                // получаем паттерны
                List<PatternData> messagePatterns = smsParser.getMessagePatterns();

                for (PatternData pattern : messagePatterns) {

                    Pattern r = Pattern.compile(pattern.getMessagePattern());
                    Matcher m = r.matcher(completeMessage);

                    if (m.find()) {
                        for (int i = 0; i <= m.groupCount(); i++) {
                            Log.d(this.getClass().toString(), "Found value " + i + ": " + m.group(i));
                        }

                        ParsingResult result = smsParser.getParsingResult(m);
                        if (result == null) {
                            Log.d(this.getClass().toString(), "Match pattern error");
                            return;
                        }

                        int trxType = pattern.getTransactionType();

                        // получаем аккаунт
                        AccountItem selectedAccount = getAccount(context, from, result.getCardNumber());
                        if (selectedAccount == null) {
                            Log.d(this.getClass().toString(), "No selected account by sender and card number");
                            return;
                        }

                        if (pattern.isExchange()) {
                            AccountDao accountDao = new AccountDao(context);
                            List<AccountItem> cashAccounts = accountDao.getByName(context.getResources().getString(R.string.default_account_name));
                            if (cashAccounts.isEmpty()) {
                                processAsTransaction(context, pattern, result, trxType, selectedAccount);
                            } else {
                                processAsExchange(context, result, trxType, selectedAccount, cashAccounts);
                            }
                        } else {
                            processAsTransaction(context, pattern, result, trxType, selectedAccount);
                        }

                        break;
                    } else {
                        Log.d(this.getClass().toString(), "No match");
                    }
                }
            } else {
                Log.d(this.getClass().toString(), "Not found from: " + from);
            }
        }
    }

    private void processAsExchange(Context context, ParsingResult result, int trxType, AccountItem selectedAccount, List<AccountItem> cashAccounts) {
        AccountItem fromAccount = null, toAccount = null;
        if (trxType == SmsParser.INCOME) {
            // внесение наличных на карту
            fromAccount = cashAccounts.get(0);
            toAccount = selectedAccount;
        }
        if (trxType == SmsParser.OUTCOME) {
            // снятие наличных с карты
            fromAccount = selectedAccount;
            toAccount = cashAccounts.get(0);
        }

        ExchangeDao exchangeDao = new ExchangeDao(context);
        exchangeDao.save(0, new ExchangeItem(0, new Date().getTime(), fromAccount.getId(), toAccount.getId(), result.getAmount(), result.getComment()));
    }

    private void processAsTransaction(Context context, PatternData pattern, ParsingResult result, int trxType, AccountItem selectedAccount) {
        TransactionDao trxDao = new TransactionDao(context, pattern.getTransactionType());
        List<TransactionItem> trxItems = trxDao.getAllByComment(result.getComment());

        int categoryId;
        if (trxItems.isEmpty()) {
            CategoryItem selectedCategory = getCategory(context, trxType);
            categoryId = selectedCategory.getId();
        } else {
            // установить ту категорию, которая чаще всего устанавливалась
            // для этого имени магазина (комментария) ранее
            categoryId = getMostFrequentCategoryId(trxItems);
        }

        saveTransaction(context, trxType, result.getAmount(), result.getComment(), categoryId, selectedAccount);
    }

    private int getMostFrequentCategoryId(List<TransactionItem> transactions) {
        Map<String, Integer> counters = new HashMap<String, Integer>();
        for (TransactionItem trx : transactions) {
            String categoryId = Integer.toString(trx.getCategoryId());
            if (!counters.containsKey(categoryId)) {
                counters.put(categoryId, 1);
            } else {
                counters.put(categoryId, counters.get(categoryId) + 1);
            }
        }

        int maxCount = Collections.max(counters.values());
        for (Map.Entry<String, Integer> entry : counters.entrySet()) {
            if (entry.getValue() == maxCount) {
                return Integer.parseInt(entry.getKey());
            }
        }
        return transactions.get(0).getCategoryId();
    }

    private SmsMessage[] getSmsMessages(Bundle bundle) {
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        return messages;
    }

    private void saveTransaction(Context context, int trxType, BigDecimal amount, String comment, int categoryId, AccountItem selectedAccount) {
        TransactionDao trxDao = new TransactionDao(context, trxType);
        TransactionItem trxItem = new TransactionItem(
                0, comment, selectedAccount.getId(), amount, new Date().getTime(),
                0, trxType, categoryId, null);
        trxDao.save(0, trxItem);
    }

    private static CategoryItem getCategory(Context context, int trxType) {
        String categoryNameForSms = context.getResources().getString(R.string.default_category_name_for_sms);

        CategoryDao categoryDao = new CategoryDao(context, trxType);
        List<CategoryItem> categories = categoryDao.getByName(categoryNameForSms);
        if (categories.isEmpty()) {
            categoryDao.save(0, new CategoryItem(0, trxType, categoryNameForSms));
            categories = categoryDao.getByName(categoryNameForSms);
        }
        return categories.get(0);
    }

    private AccountItem getAccount(Context context, String senderAddress, String cardNumber) {
        AccountItem selectedAccount = null;
        Log.d(this.getClass().toString(), senderAddress);
        AccountDao accountDao = new AccountDao(context);
        List<AccountItem> accounts = accountDao.getAll();

        for (AccountItem item : accounts) {
            if (item.getSmsSender().equalsIgnoreCase(senderAddress) && item.getCardNumber().equals(cardNumber)) {
                selectedAccount = item;
                break;
            }
        }
        return selectedAccount;
    }
}
