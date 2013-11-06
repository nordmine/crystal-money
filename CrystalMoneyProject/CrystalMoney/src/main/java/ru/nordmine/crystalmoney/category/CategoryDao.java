package ru.nordmine.crystalmoney.category;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.nordmine.crystalmoney.db.BasicDao;
import ru.nordmine.crystalmoney.db.MyDb;
import ru.nordmine.crystalmoney.db.WhereClauseItem;

public class CategoryDao extends BasicDao<CategoryItem> {
	
	private int categoryType;

	public CategoryDao(Context context, int categoryType) {
		super(context);
		this.categoryType = categoryType;
	}

	@Override
	protected String[] getSelectFields() {
		return new String[] { MyDb.UID, MyDb.CAT_NAME, MyDb.CAT_TYPE };
	}

	@Override
	protected String getTableName() {
		return MyDb.CAT_TABLE_NAME;
	}

	@Override
	protected CategoryItem parseRow(Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex(MyDb.UID));
		int categoryType = cursor.getInt(cursor.getColumnIndex(MyDb.CAT_TYPE));
		String name = cursor.getString(cursor.getColumnIndex(MyDb.CAT_NAME));
		return new CategoryItem(id, categoryType, name);
	}

	@Override
	protected ContentValues getValuesForSave(CategoryItem t) {
		ContentValues cv = new ContentValues();
		cv.put(MyDb.CAT_NAME, t.getName());
		cv.put(MyDb.CAT_TYPE, t.getCategoryType());		
		return cv;
	}

	@Override
	protected WhereClauseItem[] getClauseForList() {
		return new WhereClauseItem[] { new WhereClauseItem(MyDb.CAT_TYPE, "=",
				Integer.toString(categoryType)) };
	}

	@Override
	protected String getOrderByFieldName() {
		return MyDb.CAT_NAME;
	}

}