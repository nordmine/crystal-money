package ru.nordmine.crystalmoney.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.nordmine.crystalmoney.account.AccountDao;
import ru.nordmine.crystalmoney.account.AccountItem;
import ru.nordmine.crystalmoney.category.CategoryDao;
import ru.nordmine.crystalmoney.category.CategoryItem;
import ru.nordmine.crystalmoney.trx.TransactionDao;
import ru.nordmine.crystalmoney.trx.TransactionItem;

public class TransactionSmsReceiver extends BroadcastReceiver {

    private static final String CATEGORY_NAME_FOR_SMS = "разное";
    private static final int TRX_TYPE = 2; // расход
    private static final String SENDER = "900"; // Сбербанк
    private static final String MESSAGE_PATTERN = " на сумму (\\d+\\.\\d+) руб\\. (.*?) выполнена успешно\\.";

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

            if (from.equals(SENDER)) {
                String completeMessage = completeTextMessage.toString();

                Pattern r = Pattern.compile(MESSAGE_PATTERN);
                Matcher m = r.matcher(completeMessage);

                if (m.find()) {
                    for (int i = 0; i <= m.groupCount(); i++) {
                        Log.d(this.getClass().toString(), "Found value " + i + ": " + m.group(i));
                    }
                    if (m.groupCount() == 2) {
                        BigDecimal amount = new BigDecimal(m.group(1));
                        String comment = m.group(2);

                        AccountItem selectedAccount = getAccount(context);
                        if (selectedAccount == null) {
                            Log.d(this.getClass().toString(), "No selected account");
                            return;
                        }

                        CategoryItem selectedCategory = getCategory(context);

                        saveTransaction(context, amount, comment, selectedCategory, selectedAccount);
                    }
                } else {
                    Log.d(this.getClass().toString(), "No match");
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

    private void saveTransaction(Context context, BigDecimal amount, String comment, CategoryItem selectedCategory, AccountItem selectedAccount) {
        TransactionDao trxDao = new TransactionDao(context, TRX_TYPE);
        TransactionItem trxItem = new TransactionItem(
                0, comment, selectedAccount.getId(), amount, new Date().getTime(),
                0, TRX_TYPE, selectedCategory.getId(), null);
        trxDao.save(0, trxItem);
    }

    private CategoryItem getCategory(Context context) {
        CategoryDao categoryDao = new CategoryDao(context, TRX_TYPE);
        List<CategoryItem> categories = categoryDao.getByName(CATEGORY_NAME_FOR_SMS);
        if (categories.isEmpty()) {
            categoryDao.save(0, new CategoryItem(0, TRX_TYPE, CATEGORY_NAME_FOR_SMS));
            categories = categoryDao.getByName(CATEGORY_NAME_FOR_SMS);
            Log.d(this.getClass().toString(), "created misc category");
        }
        return categories.get(0);
    }

    private AccountItem getAccount(Context context) {
        AccountItem selectedAccount = null;
        AccountDao accountDao = new AccountDao(context);
        List<AccountItem> accounts = accountDao.getAll();

        for (AccountItem item : accounts) {
            if (item.getComment().equals(SENDER)) {
                selectedAccount = item;
                break;
            }
        }
        return selectedAccount;
    }
}
