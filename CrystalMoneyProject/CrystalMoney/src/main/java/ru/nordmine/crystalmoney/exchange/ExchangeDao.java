package ru.nordmine.crystalmoney.exchange;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.nordmine.crystalmoney.db.BasicDao;
import ru.nordmine.crystalmoney.db.MyDb;

public class ExchangeDao extends BasicDao<ExchangeItem> {

    public ExchangeDao(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected String[] getSelectFields() {
        return new String[] { MyDb.UID, MyDb.EXCHANGE_CREATED, MyDb.EXCHANGE_FROM_ACCOUNT_ID,
                MyDb.EXCHANGE_TO_ACCOUNT_ID, MyDb.EXCHANGE_AMOUNT };
    }

    @Override
    protected String getTableName() {
        return MyDb.EXCHANGE_TABLE_NAME;
    }

    @Override
    protected ExchangeItem parseRow(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(MyDb.UID));
        long created = cursor.getLong(cursor.getColumnIndex(MyDb.EXCHANGE_CREATED));
        int fromAccountId = cursor.getInt(cursor.getColumnIndex(MyDb.EXCHANGE_FROM_ACCOUNT_ID));
        int toAccountId = cursor.getInt(cursor.getColumnIndex(MyDb.EXCHANGE_TO_ACCOUNT_ID));
        double amount = cursor.getDouble(cursor.getColumnIndex(MyDb.EXCHANGE_AMOUNT));
        return new ExchangeItem(id, created, fromAccountId, toAccountId, amount);
    }

    @Override
    protected ContentValues getValuesForSave(ExchangeItem exchangeItem) {
        ContentValues cv = new ContentValues();
        cv.put(MyDb.EXCHANGE_CREATED, exchangeItem.getCreated());
        cv.put(MyDb.EXCHANGE_FROM_ACCOUNT_ID, exchangeItem.getFromAccountId());
        cv.put(MyDb.EXCHANGE_TO_ACCOUNT_ID, exchangeItem.getToAccountId());
        cv.put(MyDb.EXCHANGE_AMOUNT, exchangeItem.getAmount());
        return cv;
    }
}
