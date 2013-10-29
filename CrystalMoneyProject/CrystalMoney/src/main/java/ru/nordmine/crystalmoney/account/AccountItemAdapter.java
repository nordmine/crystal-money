package ru.nordmine.crystalmoney.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

import ru.nordmine.crystalmoney.R;

public class AccountItemAdapter extends ArrayAdapter<AccountItem> {
	
	private AccountItem[] items;
	private Context context;

	public AccountItemAdapter(Context context, int textViewResourceId,
			AccountItem[] objects) {
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
		View row = inflater.inflate(R.layout.row_account_item, parent, false);
		
		TextView label = (TextView) row.findViewById(R.id.text);
		label.setText(items[position].getName());

		ImageView icon = (ImageView) row.findViewById(R.id.icon);
		icon.setImageResource(items[position].getIconId());
		
		DecimalFormat df = new DecimalFormat("0.00");
		TextView amountTextView = (TextView) row.findViewById(R.id.amount);
		amountTextView.setText(df.format(items[position].getAmount()));

		return row;
	}
}
