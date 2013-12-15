package ru.nordmine.crystalmoney;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IconWithTextAdapter extends ArrayAdapter<NumberWithText> {
	
	private NumberWithText[] items;
	private Context context;

	public IconWithTextAdapter(Context context, int textViewResourceId,	NumberWithText[] objects) {
		super(context, textViewResourceId, objects);
		this.items = objects;
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
		View row = inflater.inflate(R.layout.row_with_icon, parent, false);
		TextView label = (TextView) row.findViewById(R.id.text);
		label.setText(items[position].getText());

		ImageView icon = (ImageView) row.findViewById(R.id.icon);
		icon.setImageResource(items[position].getNumber());

		return row;
	}
}
