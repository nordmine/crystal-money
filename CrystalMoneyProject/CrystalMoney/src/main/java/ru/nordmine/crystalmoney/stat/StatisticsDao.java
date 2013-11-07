package ru.nordmine.crystalmoney.stat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.nordmine.crystalmoney.db.MyDb;
import ru.nordmine.crystalmoney.exchange.ExchangeDao;
import ru.nordmine.crystalmoney.exchange.ExchangeItem;

public class StatisticsDao {

    private Context context;

    public StatisticsDao(Context context)
    {
        this.context = context;
    }

    public Map<Integer, Double> getTotalAmount() {
        Map<Integer, Double> amountsByAccountId = new HashMap<Integer, Double>();

        Map<Integer, Double> incomesByAccountId = getTrxSumGroupedByAccounts(1);
        Map<Integer, Double> outcomesByAccountId = getTrxSumGroupedByAccounts(2);

        for (Map.Entry<Integer, Double> entry : incomesByAccountId.entrySet()) {
            amountsByAccountId.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Integer, Double> entry : outcomesByAccountId.entrySet()) {
            if (amountsByAccountId.containsKey(entry.getKey())) {
                Double incomeSum = amountsByAccountId.get(entry.getKey());
                amountsByAccountId.put(entry.getKey(), incomeSum - entry.getValue());
            } else {
                amountsByAccountId.put(entry.getKey(), -1 * entry.getValue());
            }
        }

        ExchangeDao exchangeDao = new ExchangeDao(context);
        List<ExchangeItem> exchanges = exchangeDao.getAll();
        for(ExchangeItem exchange : exchanges)
        {
            double newAmount = exchange.getAmount();

            double fromAmount = 0;
            if(amountsByAccountId.containsKey(exchange.getFromAccountId()))
            {
                fromAmount = amountsByAccountId.get(exchange.getFromAccountId());
            }
            amountsByAccountId.put(exchange.getFromAccountId(), fromAmount - newAmount);

            double toAmount = 0;
            if(amountsByAccountId.containsKey(exchange.getToAccountId()))
            {
                toAmount = amountsByAccountId.get(exchange.getToAccountId());
            }
            amountsByAccountId.put(exchange.getToAccountId(), toAmount + newAmount);
        }

        return amountsByAccountId;
    }

    private Map<Integer, Double> getTrxSumGroupedByAccounts(int trxId) {

        Map<Integer, Double> amountsByAccountId = new HashMap<Integer, Double>();

        try {
            MyDb sqh = new MyDb(context);
            SQLiteDatabase sqdb = sqh.getReadableDatabase();

            StringBuilder query = new StringBuilder("select ");
            query.append(MyDb.TRX_ACCOUNT_ID).append(", sum(").append(MyDb.TRX_AMOUNT);
            query.append(") as amount_sum");
            query.append(" from ").append(MyDb.TRX_TABLE_NAME);
            query.append(" where ").append(MyDb.TRX_TYPE).append(" = ").append(trxId);
            query.append(" group by ").append(MyDb.TRX_ACCOUNT_ID);
            String queryString = query.toString();

            Log.d(this.getClass().getName(), queryString);

            Cursor cursor = sqdb.rawQuery(queryString, null);

            while (cursor.moveToNext()) {
                int accountId = cursor.getInt(cursor.getColumnIndex(MyDb.TRX_ACCOUNT_ID));
                double amount = cursor.getDouble(cursor.getColumnIndex("amount_sum"));
                amountsByAccountId.put(accountId, amount);
            }
            cursor.close();
            sqdb.close();
            sqh.close();
        } catch (Throwable t) {
            Log.d(this.getClass().getName(), t.toString());
        }

        return amountsByAccountId;
    }

    public double getTotalOutcomeBetweenDate(Long createdFrom, Long createdTo)
    {
        double sumBetweenDate = 0.0;

        try {
            MyDb sqh = new MyDb(context);
            SQLiteDatabase sqdb = sqh.getReadableDatabase();

            sumBetweenDate = getAmountSumByDate(sqdb, 2, createdFrom, createdTo);

            sqdb.close();
            sqh.close();
        } catch (Throwable t) {
            Log.d(this.getClass().getName(), t.toString());
        }

        return sumBetweenDate;
    }

    private Double getAmountSumByDate(SQLiteDatabase sqdb, int trxId, Long createdFrom, Long createdTo)
    {
        double amountSum = 0.0;

        StringBuilder query = new StringBuilder("select ");
        query.append("sum(").append(MyDb.TRX_AMOUNT).append(") as total_sum");
        query.append(" from ").append(MyDb.TRX_TABLE_NAME);
        query.append(" where ").append(MyDb.TRX_TYPE).append(" = ").append(trxId);
        if (createdFrom != null) {
            query.append(" and ").append(MyDb.TRX_CREATED).append(" >= ").append(createdFrom);
        }
        if (createdTo != null) {
            query.append(" and ").append(MyDb.TRX_CREATED).append(" <= ").append(createdTo);
        }
        query.append(" group by ").append(MyDb.TRX_ACCOUNT_ID);

        String queryString = query.toString();

        Log.d(this.getClass().getName(), queryString);

        Cursor cursor = sqdb.rawQuery(queryString, null);

        while (cursor.moveToNext()) {
            amountSum += cursor.getDouble(cursor.getColumnIndex("total_sum"));
        }
        cursor.close();

        return amountSum;
    }



}
