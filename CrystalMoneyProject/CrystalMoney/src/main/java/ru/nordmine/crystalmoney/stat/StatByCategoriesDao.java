package ru.nordmine.crystalmoney.stat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<Integer> palette = generatePalette(items.size());

        for (int i = 0; i < items.size(); i++) {
            StatItem item = items.get(i);
            item.setPercent(new BigDecimal(100).multiply((item.getSum().divide(total, 4, RoundingMode.HALF_UP))));
            item.setDegree(new BigDecimal(360).multiply((item.getSum().divide(total, 4, RoundingMode.HALF_UP))));
            item.setColor(palette.get(i));
        }
    }

    private static List<Integer> generatePalette(int count) {
        Integer baseColor = Color.rgb(102, 205, 170);
        float[] baseHSV = new float[3];
        Color.colorToHSV(baseColor, baseHSV);
        List<Integer> colors = new ArrayList<Integer>();
        colors.add(baseColor);
        float baseHue = baseHSV[0];
        double step = 240.0 / (double) count;
        for (int i = 1; i < count; i++) {
            float[] nextColor = Arrays.copyOf(baseHSV, baseHSV.length);
            nextColor[0] = (float) ((baseHue + step * ((double) i)) % 240.0);
            colors.add(Color.HSVToColor(nextColor));
        }
        return colors;
    }
}
