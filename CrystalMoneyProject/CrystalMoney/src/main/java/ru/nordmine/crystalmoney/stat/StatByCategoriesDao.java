package ru.nordmine.crystalmoney.stat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import ru.nordmine.crystalmoney.db.MyDb;

public class StatByCategoriesDao {

    private Context context;

    public StatByCategoriesDao(Context context) {
        this.context = context;
    }

    public List<StatItem> getTotalSumByCategories(Long createdFrom, Long createdTo) {
        List<StatItem> items = getTrxSumGroupedByCategories(2, createdFrom, createdTo, "amount_sum desc");
        Map<Integer, String> categoryMap = getCategoryMap();

        for (StatItem item : items) {
            item.setCategoryName(categoryMap.get(item.getCategoryId()));
        }

        normalizeData(items);
        return items;
    }

    private List<StatItem> getTrxSumGroupedByCategories(int trxId, Long createdFrom, Long createdTo, String orderByExpression) {

        List<StatItem> items = new ArrayList<StatItem>();

        try {
            MyDb sqh = new MyDb(context);
            SQLiteDatabase sqdb = sqh.getReadableDatabase();

            StringBuilder query = new StringBuilder("select ");
            query.append(MyDb.TRX_CATEGORY_ID).append(", sum(").append(MyDb.TRX_AMOUNT);
            query.append(") as amount_sum");
            query.append(" from ").append(MyDb.TRX_TABLE_NAME);
            query.append(" where ").append(MyDb.TRX_TYPE).append(" = ").append(trxId);
            if (createdFrom != null) {
                query.append(" and ").append(MyDb.TRX_CREATED).append(" >= ").append(createdFrom);
            }
            if (createdTo != null) {
                query.append(" and ").append(MyDb.TRX_CREATED).append(" < ").append(createdTo);
            }
            query.append(" group by ").append(MyDb.TRX_CATEGORY_ID);
            if (orderByExpression != null) {
                query.append(" order by ").append(orderByExpression);
            }
            String queryString = query.toString();

            Log.d(this.getClass().getName(), queryString);

            Cursor cursor = sqdb.rawQuery(queryString, null);

            while (cursor.moveToNext()) {
                int categoryId = cursor.getInt(cursor.getColumnIndex(MyDb.TRX_CATEGORY_ID));
                String sumString = cursor.getString(cursor.getColumnIndex("amount_sum"));
                BigDecimal sum = new BigDecimal(sumString).setScale(2, RoundingMode.HALF_UP);
                StatItem item = new StatItem();
                item.setCategoryId(categoryId);
                item.setSum(sum);
                items.add(item);
            }
            cursor.close();
            sqdb.close();
            sqh.close();
        } catch (Throwable t) {
            Log.d(this.getClass().getName(), t.toString());
        }

        return items;
    }

    private Map<Integer, String> getCategoryMap() {
        Map<Integer, String> categoryMap = new HashMap<Integer, String>();
        try {
            MyDb sqh = new MyDb(context);
            SQLiteDatabase sqdb = sqh.getReadableDatabase();
            StringBuilder query = new StringBuilder("select ");
            query.append(MyDb.UID).append(", ").append(MyDb.CAT_NAME);
            query.append(" from ").append(MyDb.CAT_TABLE_NAME);
            query.append(" where ").append(MyDb.CAT_TYPE).append(" = ").append(2);
            String queryString = query.toString();

            Log.d(this.getClass().getName(), queryString);

            Cursor cursor = sqdb.rawQuery(queryString, null);

            while (cursor.moveToNext()) {
                int categoryId = cursor.getInt(cursor.getColumnIndex(MyDb.UID));
                String categoryName = cursor.getString(cursor.getColumnIndex(MyDb.CAT_NAME));
                categoryMap.put(categoryId, categoryName);
            }

            cursor.close();
            sqdb.close();
            sqh.close();
        } catch (Throwable t) {
            Log.d(this.getClass().getName(), t.toString());
        }
        return categoryMap;
    }

    private static void normalizeData(List<StatItem> items) {
        BigDecimal total = BigDecimal.ZERO;

        for (StatItem item : items) {
            total = total.add(item.getSum()).setScale(2, RoundingMode.HALF_UP);
        }

        Queue<Integer> queue = getColors();

        for (StatItem item : items) {
            item.setPercent(new BigDecimal(100).multiply((item.getSum().divide(total, 4, RoundingMode.HALF_UP))));
            item.setDegree (new BigDecimal(360).multiply((item.getSum().divide(total, 4, RoundingMode.HALF_UP))));
            item.setColor(queue.remove());
        }
    }

    private static Queue<Integer> getColors() {
        Queue<Integer> colorList = new PriorityQueue<Integer>();
        colorList.add(Color.rgb(102, 205, 170));
        colorList.add(Color.rgb(127, 255, 212));
        colorList.add(Color.rgb(85, 107, 47));
        colorList.add(Color.rgb(46, 139, 87));
        colorList.add(Color.rgb(32, 178, 170));
        colorList.add(Color.rgb(0, 255, 127));
        colorList.add(Color.rgb(250, 235, 215));
        colorList.add(Color.rgb(143, 188, 143));
        colorList.add(Color.rgb(139, 131, 120));
        colorList.add(Color.rgb(255, 228, 225));
        colorList.add(Color.rgb(49, 79, 79));
        colorList.add(Color.rgb(100, 149, 237));
        colorList.add(Color.rgb(175, 238, 238));
        colorList.add(Color.rgb(152, 251, 152));
        colorList.add(Color.rgb(189, 183, 107));
        colorList.add(Color.rgb(0, 100, 0));
        colorList.add(Color.rgb(238, 221, 130));
        colorList.add(Color.rgb(60, 179, 113));
        colorList.add(Color.rgb(188, 143, 143));
        colorList.add(Color.rgb(147, 112, 219));
        return colorList;
    }

}
