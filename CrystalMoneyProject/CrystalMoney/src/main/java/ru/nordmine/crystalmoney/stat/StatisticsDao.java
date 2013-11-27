package ru.nordmine.crystalmoney.stat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public Map<Integer, BigDecimal> getTotalAmount() {
        Map<Integer, BigDecimal> amountsByAccountId = new HashMap<Integer, BigDecimal>();

        Map<Integer, BigDecimal> incomesByAccountId = getTrxSumGroupedByAccounts(1);
        Map<Integer, BigDecimal> outcomesByAccountId = getTrxSumGroupedByAccounts(2);

        for (Map.Entry<Integer, BigDecimal> entry : incomesByAccountId.entrySet()) {
            amountsByAccountId.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Integer, BigDecimal> entry : outcomesByAccountId.entrySet()) {
            if (amountsByAccountId.containsKey(entry.getKey())) {
                BigDecimal incomeSum = amountsByAccountId.get(entry.getKey());
                amountsByAccountId.put(entry.getKey(), incomeSum.subtract(entry.getValue()).setScale(2, RoundingMode.HALF_UP));
            } else { // todo нужна ли эта ветка?
                amountsByAccountId.put(entry.getKey(), entry.getValue().multiply(new BigDecimal(-1)).setScale(2, RoundingMode.HALF_UP));
            }
        }

        ExchangeDao exchangeDao = new ExchangeDao(context);
        List<ExchangeItem> exchanges = exchangeDao.getAll();
        for(ExchangeItem exchange : exchanges)
        {
            BigDecimal newAmount = exchange.getAmount();

            BigDecimal fromAmount = BigDecimal.ZERO;
            if(amountsByAccountId.containsKey(exchange.getFromAccountId()))
            {
                fromAmount = amountsByAccountId.get(exchange.getFromAccountId());
            }
            amountsByAccountId.put(exchange.getFromAccountId(), fromAmount.subtract(newAmount).setScale(2, RoundingMode.HALF_UP));

            BigDecimal toAmount = BigDecimal.ZERO;
            if(amountsByAccountId.containsKey(exchange.getToAccountId()))
            {
                toAmount = amountsByAccountId.get(exchange.getToAccountId());
            }
            amountsByAccountId.put(exchange.getToAccountId(), toAmount.add(newAmount).setScale(2, RoundingMode.HALF_UP));
        }

        return amountsByAccountId;
    }

    private Map<Integer, BigDecimal> getTrxSumGroupedByAccounts(int trxId) {

        Map<Integer, BigDecimal> amountsByAccountId = new HashMap<Integer, BigDecimal>();

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
                String amountString = cursor.getString(cursor.getColumnIndex("amount_sum"));
                BigDecimal amount = new BigDecimal(amountString).setScale(2, RoundingMode.HALF_UP);
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

    public BigDecimal getTotalOutcomeBetweenDate(Long createdFrom, Long createdTo)
    {
        BigDecimal sumBetweenDate = BigDecimal.ZERO;

        try {
            MyDb sqh = new MyDb(context);
            SQLiteDatabase sqdb = sqh.getReadableDatabase();

            sumBetweenDate = getAmountSumBetweenDate(sqdb, 2, createdFrom, createdTo);

            sqdb.close();
            sqh.close();
        } catch (Throwable t) {
            Log.d(this.getClass().getName(), t.toString());
        }

        return sumBetweenDate;
    }

    private BigDecimal getAmountSumBetweenDate(SQLiteDatabase sqdb, int trxId, Long createdFrom, Long createdTo)
    {
        BigDecimal amountSum = BigDecimal.ZERO;

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
            String totalSumString = cursor.getString(cursor.getColumnIndex("total_sum"));
            amountSum = amountSum.add(new BigDecimal(totalSumString).setScale(2, RoundingMode.HALF_UP));
        }
        cursor.close();

        return amountSum;
    }



}
