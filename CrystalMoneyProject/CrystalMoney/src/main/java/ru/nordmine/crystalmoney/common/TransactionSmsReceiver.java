package ru.nordmine.crystalmoney.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.math.BigDecimal;
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
import ru.nordmine.crystalmoney.trx.TransactionDao;
import ru.nordmine.crystalmoney.trx.TransactionItem;

public class TransactionSmsReceiver extends BroadcastReceiver {

    private static final int TRX_TYPE = 2; // расход
    private static final String SBERBANK = "900"; // Сбербанк

    private Map<String, SmsParserInfo> populatePatterns() {
        Map<String, SmsParserInfo> patterns = new HashMap<String, SmsParserInfo>();
        patterns.put(SBERBANK, new SberbankParserInfo());
        return patterns;
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

            Map<String, SmsParserInfo> patterns = populatePatterns();

            if (patterns.containsKey(from)) {
                String completeMessage = completeTextMessage.toString();

                // получаем паттерны
                List<String> messagePatterns = patterns.get(from).getMessagePatterns();

                // получаем аккаунт
                AccountItem selectedAccount = getAccount(context, from);
                if (selectedAccount == null) {
                    Log.d(this.getClass().toString(), "No selected account");
                    return;
                }

                for (String messagePattern : messagePatterns) {

                    Pattern r = Pattern.compile(messagePattern);
                    Matcher m = r.matcher(completeMessage);

                    if (m.find()) {
                        for (int i = 0; i <= m.groupCount(); i++) {
                            Log.d(this.getClass().toString(), "Found value " + i + ": " + m.group(i));
                        }
                        if (m.groupCount() == 2) {
                            BigDecimal amount = new BigDecimal(m.group(1));
                            String comment = m.group(2);

                            TransactionDao trxDao = new TransactionDao(context, TRX_TYPE);
                            List<TransactionItem> trxItems = trxDao.getAllByComment(comment);

                            int categoryId;
                            if (trxItems.isEmpty()) {
                                CategoryItem selectedCategory = getCategory(context);
                                categoryId = selectedCategory.getId();
                            } else {
                                // установить ту категорию, которая уже была выбрана для этого имени магазина (комментария) ранее
                                categoryId = trxItems.get(0).getCategoryId();
                            }

                            saveTransaction(context, amount, comment, categoryId, selectedAccount);
                            break;
                        }
                    } else {
                        Log.d(this.getClass().toString(), "No match");
                    }
                }
            }
        }
    }

    private SmsMessage[] getSmsMessages(Bundle bundle) {
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        return messages;
    }

    private void saveTransaction(Context context, BigDecimal amount, String comment, int categoryId, AccountItem selectedAccount) {
        TransactionDao trxDao = new TransactionDao(context, TRX_TYPE);
        TransactionItem trxItem = new TransactionItem(
                0, comment, selectedAccount.getId(), amount, new Date().getTime(),
                0, TRX_TYPE, categoryId, null);
        trxDao.save(0, trxItem);
    }

    private static CategoryItem getCategory(Context context) {
        String categoryNameForSms = context.getResources().getString(R.string.default_category_name_for_sms);

        CategoryDao categoryDao = new CategoryDao(context, TRX_TYPE);
        List<CategoryItem> categories = categoryDao.getByName(categoryNameForSms);
        if (categories.isEmpty()) {
            categoryDao.save(0, new CategoryItem(0, TRX_TYPE, categoryNameForSms));
            categories = categoryDao.getByName(categoryNameForSms);
        }
        return categories.get(0);
    }

    private static AccountItem getAccount(Context context, String senderAddress) {
        AccountItem selectedAccount = null;
        AccountDao accountDao = new AccountDao(context);
        List<AccountItem> accounts = accountDao.getAll();

        for (AccountItem item : accounts) {
            if (item.getComment().equals(senderAddress)) {
                selectedAccount = item;
                break;
            }
        }
        return selectedAccount;
    }
}
