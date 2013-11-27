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

	private Context context;

	public AccountItemAdapter(Context context, int textViewResourceId, AccountItem[] objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.row_account_item, parent, false);

        AccountItem item = getItem(position);
		
		TextView label = (TextView) row.findViewById(R.id.nameTextView);
		label.setText(item.getName());

		ImageView icon = (ImageView) row.findViewById(R.id.iconImageView);
		icon.setImageResource(item.getIconId());
		
		DecimalFormat df = new DecimalFormat("###,##0.00");
		TextView amountTextView = (TextView) row.findViewById(R.id.amountTextView);
		amountTextView.setText(df.format(item.getAmount().doubleValue()));

        TextView comment = (TextView) row.findViewById(R.id.commentTextView);
        comment.setText(item.getComment());

		return row;
	}
}
