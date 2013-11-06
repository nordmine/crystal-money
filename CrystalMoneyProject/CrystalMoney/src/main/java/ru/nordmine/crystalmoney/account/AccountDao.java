package ru.nordmine.crystalmoney.account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.nordmine.crystalmoney.db.BasicDao;
import ru.nordmine.crystalmoney.db.MyDb;

public class AccountDao extends BasicDao<AccountItem> {

	public AccountDao(Context context) {
		super(context);
	}

	@Override
	protected String[] getSelectFields() {
		return new String[] { MyDb.UID, MyDb.ACCOUNT_NAME, MyDb.ACCOUNT_COMMENT,
				MyDb.ACCOUNT_IS_CARD, MyDb.ACCOUNT_PICTURE, MyDb.ACCOUNT_AMOUNT };
	}

	@Override
	protected String getTableName() {
		return MyDb.ACCOUNT_TABLE_NAME;
	}

	@Override
	protected AccountItem parseRow(Cursor cursor) {

		int id = cursor.getInt(cursor.getColumnIndex(MyDb.UID));

		String accountName = cursor.getString(cursor
				.getColumnIndex(MyDb.ACCOUNT_NAME));

		String comment = cursor.getString(cursor
				.getColumnIndex(MyDb.ACCOUNT_COMMENT));

		int iconId = cursor.getInt(cursor.getColumnIndex(MyDb.ACCOUNT_PICTURE));

		Double amount = cursor.getDouble(cursor
				.getColumnIndex(MyDb.ACCOUNT_AMOUNT));

		boolean isCard = cursor.getInt(cursor
				.getColumnIndex(MyDb.ACCOUNT_IS_CARD)) == 0 ? false : true;

		return new AccountItem(id, accountName, iconId, amount, isCard, comment);
	}

	@Override
	protected ContentValues getValuesForSave(AccountItem t) {
		ContentValues cv = new ContentValues();
		cv.put(MyDb.ACCOUNT_NAME, t.getName()
				.toString());
		cv.put(MyDb.ACCOUNT_AMOUNT, Double.toString(t.getAmount()));
		cv.put(MyDb.ACCOUNT_COMMENT, t.getComment());
		cv.put(MyDb.ACCOUNT_IS_CARD, t.isCard());
		cv.put(MyDb.ACCOUNT_PICTURE, t.getIconId());
		return cv;
	}

}