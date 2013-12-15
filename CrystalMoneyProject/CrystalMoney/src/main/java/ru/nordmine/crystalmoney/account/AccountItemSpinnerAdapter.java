package ru.nordmine.crystalmoney.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.nordmine.crystalmoney.NumberWithText;
import ru.nordmine.crystalmoney.R;

public class AccountItemSpinnerAdapter extends ArrayAdapter<AccountItem> {

	private Context context;

	public AccountItemSpinnerAdapter(Context context, int textViewResourceId, AccountItem[] objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}

	@Override
	public View getDropDownView(int position, View convertView,	ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.row_with_icon, parent, false);

        AccountItem item = getItem(position);

		TextView label = (TextView) row.findViewById(R.id.text);
		label.setText(item.getName());

		Integer[] accountTypes = AccountActivity.getAccountIcons();
		ImageView icon = (ImageView) row.findViewById(R.id.icon);
		icon.setImageResource(accountTypes[item.getIconId()]);

		return row;
	}
}
