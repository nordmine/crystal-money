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

	private Context context;

	public TransactionItemAdapter(Context context, int textViewResourceId, TransactionItem[] objects) {
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
		View row = inflater.inflate(R.layout.row_trx_item, parent, false);

        TransactionItem item = getItem(position);
		
		TextView categoryTextView = (TextView) row.findViewById(R.id.categoryTextView);
		categoryTextView.setText(item.getCategoryName());
		
		DecimalFormat df = new DecimalFormat("###,##0.00");
		TextView amountTextView = (TextView) row.findViewById(R.id.amountTextView);
		amountTextView.setText(df.format(item.getAmount().doubleValue()));

        TextView commentTextView = (TextView)row.findViewById(R.id.commentTextView);
        commentTextView.setText(item.getComment());

		NumberWithText[] accountTypes = AccountActivity.getAccountIcons();
		
		ImageView icon = (ImageView) row.findViewById(R.id.iconImageView);
		icon.setImageResource(accountTypes[item.getAccountIconId()].getNumber());
		
		SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
		
		TextView createdTextView = (TextView) row.findViewById(R.id.dateTextView);
        createdTextView.setText(f.format(item.getCreated()));

		return row;
	}
}