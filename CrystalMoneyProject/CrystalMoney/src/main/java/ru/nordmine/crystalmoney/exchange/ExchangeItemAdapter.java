package ru.nordmine.crystalmoney.exchange;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import ru.nordmine.crystalmoney.NumberWithText;
import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountActivity;

public class ExchangeItemAdapter extends ArrayAdapter<ExchangeItem> {

    private Context context;

    public ExchangeItemAdapter(Context context, int textViewResourceId, ExchangeItem[] objects) {
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
        View row = inflater.inflate(R.layout.row_exchange_item, parent, false);

        NumberWithText[] icons = AccountActivity.getAccountIcons();

        ExchangeItem item = getItem(position);

        TextView fromTextView = (TextView) row.findViewById(R.id.exchangeDateTextView);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        fromTextView.setText(sdf.format(item.getCreated()));

        ImageView toAccountIconImageView = (ImageView) row.findViewById(R.id.toAccountIconImageView);
        toAccountIconImageView.setImageResource(icons[item.getToAccountIconId()].getNumber());

        ImageView fromAccountIconImageView = (ImageView) row.findViewById(R.id.fromAccountIconImageView);
        fromAccountIconImageView.setImageResource(icons[item.getFromAccountIconId()].getNumber());

        DecimalFormat df = new DecimalFormat("###,##0.00");
        TextView amountTextView = (TextView) row.findViewById(R.id.amountTextView);
        amountTextView.setText(df.format(item.getAmount().doubleValue()));

        return row;
    }

}
