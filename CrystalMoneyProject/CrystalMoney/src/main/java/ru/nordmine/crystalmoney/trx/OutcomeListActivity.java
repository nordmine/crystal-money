package ru.nordmine.crystalmoney.trx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import ru.nordmine.crystalmoney.R;

public class OutcomeListActivity extends TransactionListActivity {
	
	private static final int EDIT_OUTCOME = 31;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		transactionType = 2;
		this.dao = new TransactionDao(this, transactionType);
		
		categoryType = 2;
		listView = (ListView) findViewById(R.id.categoryListView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Intent intent = new Intent();
				intent.setClass(OutcomeListActivity.this, TransactionActivity.class);
				Bundle b = new Bundle();
				b.putInt("defStrID", trxItems.get(position).getId());
                b.putLong("selectedDate", startDate);
				intent.putExtras(b);
				startActivityForResult(intent, EDIT_OUTCOME);
			}
		});
		
		registerForContextMenu(listView);
		refreshItems();		
	}

	@Override
	protected void onAddButtonClick() {
		if (!super.checkAccountListForEmpty()) {
			return;
		}
		Intent intent = new Intent(OutcomeListActivity.this, TransactionActivity.class);
		intent.putExtra("transactionType", transactionType);
        intent.putExtra("selectedDate", startDate);
		startActivityForResult(intent, EDIT_OUTCOME);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == EDIT_OUTCOME && resultCode == RESULT_OK) {
			refreshItems();
		}
	}

}
