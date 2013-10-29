package ru.nordmine.crystalmoney.trx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.nordmine.crystalmoney.NumberWithText;
import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountActivity;

public class TransactionItemAdapter extends ArrayAdapter<TransactionItem> {
	
	private TransactionItem[] items;
	private Context context;

	public TransactionItemAdapter(Context context, int textViewResourceId,
			TransactionItem[] objects) {
		super(context, textViewResourceId, objects);
		this.items = objects;
		this.context = context;
	}

	@Override
	public View getDropDownView(int position, View convertView,
			ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView,
			ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.row2, parent, false);
		
		TextView label1 = (TextView) row.findViewById(R.id.text1);
		label1.setText(items[position].getCategoryName());
		
		DecimalFormat df = new DecimalFormat("0.00");
		TextView label2 = (TextView) row.findViewById(R.id.text2);
		label2.setText(df.format(items[position].getAmount()));

		NumberWithText[] accountTypes = AccountActivity.getAccountIcons();
		
		ImageView icon = (ImageView) row.findViewById(R.id.iconImage);
		icon.setImageResource(accountTypes[items[position].getAccountIconId()].getNumber());
		
		SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
		
		TextView amountTextView = (TextView) row.findViewById(R.id.dateText);
		amountTextView.setText(f.format(items[position].getCreated()));
		
		return row;
	}
}