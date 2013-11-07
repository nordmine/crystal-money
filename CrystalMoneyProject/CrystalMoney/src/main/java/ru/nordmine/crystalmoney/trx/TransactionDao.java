package ru.nordmine.crystalmoney.trx;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.nordmine.crystalmoney.db.BasicDao;
import ru.nordmine.crystalmoney.db.JoinTableItem;
import ru.nordmine.crystalmoney.db.MyDb;
import ru.nordmine.crystalmoney.db.WhereClauseItem;

public class TransactionDao extends BasicDao<TransactionItem> {
	
	private int transactionType = 0;

	public TransactionDao(Context context, int transactionType) {
		super(context);
		this.transactionType = transactionType;
	}

	@Override
	protected String[] getSelectFields() {
		return new String[] { MyDb.TRX_TABLE_NAME + "." + MyDb.UID, 
				MyDb.TRX_ACCOUNT_ID, 
				MyDb.TRX_TABLE_NAME + "." + MyDb.TRX_AMOUNT,
				MyDb.TRX_CATEGORY_ID, 
				MyDb.TRX_COMMENT, 
				MyDb.TRX_CREATED,
				MyDb.TRX_TYPE, 
				"acc." + MyDb.ACCOUNT_PICTURE,
				"cat." + MyDb.CAT_NAME };
	}
	
	@Override
	protected WhereClauseItem[] getClauseForList() {
		return new WhereClauseItem[] {new WhereClauseItem(MyDb.TRX_TYPE, "=", Integer.toString(this.transactionType))};
	}

	@Override
	protected String getTableName() {
		return MyDb.TRX_TABLE_NAME;
	}

	@Override
	protected TransactionItem parseRow(Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex(MyDb.UID));
		String comment = cursor.getString(cursor
				.getColumnIndex(MyDb.TRX_COMMENT));
		Double amount = cursor.getDouble(cursor
				.getColumnIndex(MyDb.TRX_AMOUNT));
		int categoryId = cursor.getInt(cursor
				.getColumnIndex(MyDb.TRX_CATEGORY_ID));
		int transactionType = cursor.getInt(cursor
				.getColumnIndex(MyDb.TRX_TYPE));
		long created = cursor.getLong(cursor
				.getColumnIndex(MyDb.TRX_CREATED));
		int accountId = cursor.getInt(cursor
				.getColumnIndex(MyDb.TRX_ACCOUNT_ID));
		
		int iconId = cursor.getInt(cursor.getColumnIndex("acc." + MyDb.ACCOUNT_PICTURE));
		String categoryName = cursor.getString(cursor.getColumnIndex("cat." + MyDb.CAT_NAME));
		return new TransactionItem(id, comment, accountId, amount, created, iconId, transactionType, categoryId, categoryName);
	}

	@Override
	protected ContentValues getValuesForSave(TransactionItem t) {		
		ContentValues cv = new ContentValues();
		cv.put(MyDb.TRX_COMMENT, t.getComment());
		cv.put(MyDb.TRX_AMOUNT, t.getAmount());
		cv.put(MyDb.TRX_ACCOUNT_ID, t.getAccountId());
		cv.put(MyDb.TRX_CATEGORY_ID, t.getCategoryId());
		cv.put(MyDb.TRX_CREATED, t.getCreated());
		cv.put(MyDb.TRX_TYPE, t.getTransactionType());		
		return cv;
	}

	@Override
	protected JoinTableItem[] getJoinTables() {
		JoinTableItem categoryItem = new JoinTableItem(MyDb.TRX_CATEGORY_ID, MyDb.CAT_TABLE_NAME, "cat");
		JoinTableItem accountItem = new JoinTableItem(MyDb.TRX_ACCOUNT_ID, MyDb.ACCOUNT_TABLE_NAME, "acc");
		return new JoinTableItem[] { categoryItem, accountItem };
	}

	@Override
	protected String getOrderByFieldName() {		
		return getTableName() + "." + MyDb.TRX_CREATED;
	}

	@Override
	protected String getOrderDirection() {
		return "desc";
	}

}
