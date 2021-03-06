package ru.nordmine.crystalmoney.exchange;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import ru.nordmine.crystalmoney.db.BasicDao;
import ru.nordmine.crystalmoney.db.JoinTableItem;
import ru.nordmine.crystalmoney.db.MyDb;
import ru.nordmine.crystalmoney.db.WhereClauseItem;
import ru.nordmine.crystalmoney.trx.TransactionItem;

public class ExchangeDao extends BasicDao<ExchangeItem> {

    public ExchangeDao(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected String[] getSelectFields() {
        return new String[] {
                MyDb.EXCHANGE_TABLE_NAME + "." + MyDb.UID + " as ex_id",
                MyDb.EXCHANGE_TABLE_NAME + "." + MyDb.EXCHANGE_CREATED + " as ex_created",
                MyDb.EXCHANGE_FROM_ACCOUNT_ID,
                MyDb.EXCHANGE_TO_ACCOUNT_ID,
                MyDb.EXCHANGE_TABLE_NAME + "." + MyDb.EXCHANGE_AMOUNT + " as ex_amount",
                MyDb.EXCHANGE_TABLE_NAME + "." + MyDb.EXCHANGE_COMMENT + " as ex_comment",
                "acc1." + MyDb.ACCOUNT_PICTURE + " as acc1_icon",
                "acc2." + MyDb.ACCOUNT_PICTURE + " as acc2_icon"
        };
    }

    @Override
    protected String getTableName() {
        return MyDb.EXCHANGE_TABLE_NAME;
    }

    @Override
    protected ExchangeItem parseRow(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("ex_id"));
        long created = cursor.getLong(cursor.getColumnIndex("ex_created"));
        int fromAccountId = cursor.getInt(cursor.getColumnIndex(MyDb.EXCHANGE_FROM_ACCOUNT_ID));
        int toAccountId = cursor.getInt(cursor.getColumnIndex(MyDb.EXCHANGE_TO_ACCOUNT_ID));
        String amountString = cursor.getString(cursor.getColumnIndex("ex_amount"));
        String comment = cursor.getString(cursor.getColumnIndex("ex_comment"));
        BigDecimal amount = new BigDecimal(amountString).setScale(2, RoundingMode.HALF_UP);
        int fromAccountIconId = cursor.getInt(cursor.getColumnIndex("acc1_icon"));
        int toAccountIconId = cursor.getInt(cursor.getColumnIndex("acc2_icon"));
        return new ExchangeItem(id, created, fromAccountId, toAccountId, amount, comment, fromAccountIconId, toAccountIconId);
    }

    @Override
    protected ContentValues getValuesForSave(ExchangeItem exchangeItem) {
        ContentValues cv = new ContentValues();
        cv.put(MyDb.EXCHANGE_CREATED, exchangeItem.getCreated());
        cv.put(MyDb.EXCHANGE_FROM_ACCOUNT_ID, exchangeItem.getFromAccountId());
        cv.put(MyDb.EXCHANGE_TO_ACCOUNT_ID, exchangeItem.getToAccountId());
        cv.put(MyDb.EXCHANGE_AMOUNT, exchangeItem.getAmount().toPlainString());
        cv.put(MyDb.EXCHANGE_COMMENT, exchangeItem.getComment());
        return cv;
    }

    @Override
    protected String getOrderByFieldName() {
        return getTableName() + "." + MyDb.EXCHANGE_CREATED;
    }

    @Override
    protected String getOrderDirection() {
        return "desc";
    }

    @Override
    protected JoinTableItem[] getJoinTables() {
        return new JoinTableItem[] {new JoinTableItem(MyDb.EXCHANGE_FROM_ACCOUNT_ID, MyDb.ACCOUNT_TABLE_NAME, "acc1"),
                                    new JoinTableItem(MyDb.EXCHANGE_TO_ACCOUNT_ID, MyDb.ACCOUNT_TABLE_NAME, "acc2")};
    }

    @Override
    public List<ExchangeItem> getAll() {
        return super.getAll(null);
    }

    public List<ExchangeItem> getAll(long startDate, long finishDate) {
        List<WhereClauseItem> whereClause = new ArrayList<WhereClauseItem>();
        whereClause.add(new WhereClauseItem(getTableName() + "." + MyDb.EXCHANGE_CREATED, ">=", Long.toString(startDate)));
        whereClause.add(new WhereClauseItem(getTableName() + "." + MyDb.EXCHANGE_CREATED, "<", Long.toString(finishDate)));
        return super.getAll(whereClause.toArray(new WhereClauseItem[whereClause.size()]));
    }
}
