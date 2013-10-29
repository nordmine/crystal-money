package ru.nordmine.crystalmoney.trx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import ru.nordmine.crystalmoney.R;

public class IncomeListActivity extends TransactionListActivity {

	private static final int EDIT_INCOME = 11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		transactionType = 1;
		this.dao = new TransactionDao(this, transactionType);
		
		categoryType = 1;
		listView = (ListView) findViewById(R.id.categoryListView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Intent intent = new Intent();
				intent.setClass(IncomeListActivity.this, TransactionActivity.class);
				Bundle b = new Bundle();
				b.putInt("defStrID", trxItems.get(position).getId());
				intent.putExtras(b);
				startActivityForResult(intent, EDIT_INCOME);
			}
		});
		
		registerForContextMenu(listView);		
		refreshItems();
	}

	public void onAddButtonClick(View v) {
		if (!super.checkAccountListForEmpty()) {
			return;
		}
		Intent intent = new Intent(IncomeListActivity.this,
				TransactionActivity.class);
		intent.putExtra("transactionType", transactionType);		
		startActivityForResult(intent, EDIT_INCOME);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == EDIT_INCOME && resultCode == RESULT_OK) {
			refreshItems();
		}
	}

}
