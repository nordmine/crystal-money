package ru.nordmine.crystalmoney.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.nordmine.crystalmoney.R;

public abstract class BasicDao<T> {

    protected Context context;

    public BasicDao(Context context) {
        this.context = context;
    }

    protected abstract String[] getSelectFields();

    protected String getOrderByFieldName() {
        return null;
    }

    protected String getOrderDirection() {
        return "asc";
    }

    protected JoinTableItem[] getJoinTables() {
        return null;
    }

    protected abstract String getTableName();

    protected abstract T parseRow(Cursor cursor);

    protected void addJoinTables(StringBuilder query) {
        JoinTableItem[] joinTables = getJoinTables();
        if (joinTables != null && joinTables.length > 0) {
            for (JoinTableItem joinItem : joinTables) {
                query.append(" left join ").append(joinItem.getJoinTableName()).append(" ").append(joinItem.getAlias());
                query.append(" on (").append(getTableName()).append(".");
                query.append(joinItem.getForeignKeyName()).append(" = ");
                query.append(joinItem.getAlias()).append(".");
                query.append(MyDb.UID).append(") ");
            }
        }
    }

    protected StringBuilder clauseToString(List<String> whereArgs,
                                           WhereClauseItem[] whereClause) {
        StringBuilder query = new StringBuilder("");
        if (whereClause != null && whereClause.length > 0) {
            List<String> whereClauseList = new ArrayList<String>();
            for (WhereClauseItem i : whereClause) {
                whereClauseList.add(i.toString());
                whereArgs.add(i.getValue());
            }
            query.append(" where ").append(TextUtils.join(" and ", whereClauseList));
        }
        return query;
    }

    protected abstract ContentValues getValuesForSave(T t);

    public T getById(int id) {
        T result = null;

        try {
            MyDb sqh = new MyDb(context);
            SQLiteDatabase sqdb = sqh.getReadableDatabase();

            StringBuilder query = new StringBuilder("select ");
            query.append(TextUtils.join(", ", getSelectFields()));
            query.append(" FROM ").append(getTableName());

            addJoinTables(query);

            List<String> whereArgs = new ArrayList<String>();
            WhereClauseItem[] whereClause = new WhereClauseItem[]{new WhereClauseItem(
                    getTableName() + "." + MyDb.UID, "=", Integer.toString(id))};
            query.append(clauseToString(whereArgs, whereClause));

            String queryString = query.toString();

            Log.d(this.getClass().getName(), queryString);

            Cursor cursor = sqdb.rawQuery(queryString,
                    whereArgs.toArray(new String[whereArgs.size()]));

            while (cursor.moveToNext()) {
                result = parseRow(cursor);
            }
            cursor.close();

            sqdb.close();
            sqh.close();

        } catch (Throwable t) {
            Log.d(this.getClass().getName(), t.toString());
        }
        return result;
    }

    public abstract List<T> getAll();

    protected List<T> getAll(WhereClauseItem[] whereClause) {
        List<T> items = new ArrayList<T>();

        try {
            MyDb sqh = new MyDb(context);
            SQLiteDatabase sqdb = sqh.getReadableDatabase();
            StringBuilder query = new StringBuilder("select ");
            query.append(TextUtils.join(", ", getSelectFields()));

            query.append(" FROM ").append(getTableName());

            addJoinTables(query);

            List<String> whereArgs = new ArrayList<String>();
            query.append(clauseToString(whereArgs, whereClause));

            String orderByFieldName = getOrderByFieldName();
            if (orderByFieldName != null) {
                query.append(" order by ").append(orderByFieldName);
                query.append(" ").append(getOrderDirection());
            }

            String queryString = query.toString();

//            Log.d(this.getClass().getName(), queryString);

            System.out.println(queryString);

            Cursor cursor = sqdb.rawQuery(queryString,
                    whereArgs.toArray(new String[whereArgs.size()]));

            while (cursor.moveToNext()) {
                items.add(parseRow(cursor));
            }
            cursor.close();
            sqdb.close();
            sqh.close();
        } catch (Throwable t) {
            Log.d(this.getClass().getName(), t.toString());
        }
        return items;
    }

    public void save(int id, T t) {
        try {
            MyDb sqh = new MyDb(context);
            SQLiteDatabase sqdb = sqh.getWritableDatabase();

            ContentValues cv = getValuesForSave(t);

            if (id > 0) {
                String[] args = new String[]{Integer.toString(id)};
                sqdb.update(getTableName(), cv, MyDb.UID + " = ?", args);
            } else {
                sqdb.insert(getTableName(), null, cv);
            }

            sqdb.close();
            sqh.close();

        } catch (Throwable tr) {
            Log.d(this.getClass().getName(), tr.toString());
        }
    }

    public void removeById(int id) {
        try {
            MyDb sqh = new MyDb(context);
            SQLiteDatabase sqdb = sqh.getWritableDatabase();

            sqdb.delete(getTableName(), MyDb.UID + " = ?",
                    new String[]{Integer.toString(id)});

            sqdb.close();
            sqh.close();
        } catch (SQLiteConstraintException e) {
            Toast.makeText(context, R.string.caption_constraint_exception,
                    Toast.LENGTH_LONG).show();
        } catch (Throwable t) {
            Log.d(this.getClass().getName(), t.toString());
        }
    }

    public int getTotalCount() {
        return this.getTotalCountWithClause(null);
    }

    public int getTotalCountWithClause(WhereClauseItem[] whereClause) {
        int totalCount = 0;

        try {
            MyDb sqh = new MyDb(context);
            SQLiteDatabase sqdb = sqh.getReadableDatabase();

            StringBuilder query = new StringBuilder("select count(");
            query.append(MyDb.UID).append(") as count_id FROM ").append(getTableName());

            List<String> whereArgs = new ArrayList<String>();
            query.append(clauseToString(whereArgs, whereClause));

            String queryString = query.toString();


            Log.d(this.getClass().getName(), queryString);

            Cursor cursor = sqdb.rawQuery(queryString, whereArgs.toArray(new String[0]));

            while (cursor.moveToNext()) {
                totalCount = cursor.getInt(cursor.getColumnIndex("count_id"));
            }
            cursor.close();

            sqdb.close();
            sqh.close();

        } catch (Throwable t) {
            Log.d(this.getClass().getName(), t.toString());
        }
        return totalCount;
    }

}
