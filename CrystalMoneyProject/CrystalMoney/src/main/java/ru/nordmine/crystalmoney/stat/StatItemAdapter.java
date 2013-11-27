package ru.nordmine.crystalmoney.stat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

import ru.nordmine.crystalmoney.R;

public class StatItemAdapter extends ArrayAdapter<StatItem> {

    private Context context;

    public StatItemAdapter(Context context, int textViewResourceId, StatItem[] objects) {
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
        View row = inflater.inflate(R.layout.row_stat_item, parent, false);

        StatItem item = getItem(position);

        TextView colorTextView = (TextView) row.findViewById(R.id.colorTextView);
        colorTextView.setBackgroundColor(item.getColor());

        DecimalFormat df = new DecimalFormat("###,##0.00");
        TextView label = (TextView) row.findViewById(R.id.captionTextView);
        label.setText(item.getCategoryName() + " - " + df.format(item.getSum().doubleValue()) + " - " + df.format(item.getPercent().doubleValue()) + "%");

        return row;
    }

}
