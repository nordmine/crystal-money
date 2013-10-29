package ru.nordmine.crystalmoney.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CategoryItemAdapter extends ArrayAdapter<CategoryItem> {
	
	private CategoryItem[] items;
	private Context context;

	public CategoryItemAdapter(Context context, int textViewResourceId,
			CategoryItem[] objects) {
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
		View row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		TextView label = (TextView) row.findViewById(android.R.id.text1);
		label.setText(items[position].getName());
		return row;
	}

}
