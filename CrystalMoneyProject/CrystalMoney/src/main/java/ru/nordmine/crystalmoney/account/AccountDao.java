package ru.nordmine.crystalmoney.account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import ru.nordmine.crystalmoney.db.BasicDao;
import ru.nordmine.crystalmoney.db.MyDb;

public class AccountDao extends BasicDao<AccountItem> {

	public AccountDao(Context context) {
		super(context);
	}

	@Override
	protected String[] getSelectFields() {
		return new String[] { MyDb.UID, MyDb.ACCOUNT_NAME, MyDb.ACCOUNT_COMMENT,
				MyDb.ACCOUNT_IS_CARD, MyDb.ACCOUNT_PICTURE, MyDb.ACCOUNT_AMOUNT,
                MyDb.ACCOUNT_CARD_NUMBER, MyDb.ACCOUNT_SMS_SENDER };
	}

	@Override
	protected String getTableName() {
		return MyDb.ACCOUNT_TABLE_NAME;
	}

	@Override
	protected AccountItem parseRow(Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex(MyDb.UID));
		String accountName = cursor.getString(cursor.getColumnIndex(MyDb.ACCOUNT_NAME));
		String comment = cursor.getString(cursor.getColumnIndex(MyDb.ACCOUNT_COMMENT));
		int iconId = cursor.getInt(cursor.getColumnIndex(MyDb.ACCOUNT_PICTURE));
		String amountString = cursor.getString(cursor.getColumnIndex(MyDb.ACCOUNT_AMOUNT));
        BigDecimal amount = new BigDecimal(amountString).setScale(2, RoundingMode.HALF_UP);
		boolean isCard = cursor.getInt(cursor.getColumnIndex(MyDb.ACCOUNT_IS_CARD)) == 0 ? false : true;
        String cardNumber = cursor.getString(cursor.getColumnIndex(MyDb.ACCOUNT_CARD_NUMBER));
        String smsSender = cursor.getString(cursor.getColumnIndex(MyDb.ACCOUNT_SMS_SENDER));
		return new AccountItem(id, accountName, iconId, amount, isCard, comment, cardNumber, smsSender);
	}

	@Override
	protected ContentValues getValuesForSave(AccountItem t) {
		ContentValues cv = new ContentValues();
		cv.put(MyDb.ACCOUNT_NAME, t.getName());
		cv.put(MyDb.ACCOUNT_AMOUNT, t.getAmount().toPlainString());
		cv.put(MyDb.ACCOUNT_COMMENT, t.getComment());
		cv.put(MyDb.ACCOUNT_IS_CARD, t.isCard());
		cv.put(MyDb.ACCOUNT_PICTURE, t.getIconId());
        cv.put(MyDb.ACCOUNT_CARD_NUMBER, t.getCardNumber());
        cv.put(MyDb.ACCOUNT_SMS_SENDER, t.getSmsSender());
		return cv;
	}

    @Override
    public List<AccountItem> getAll() {
        return super.getAll(null);
    }

}
