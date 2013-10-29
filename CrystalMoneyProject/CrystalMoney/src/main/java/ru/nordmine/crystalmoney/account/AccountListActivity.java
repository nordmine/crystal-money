package ru.nordmine.crystalmoney.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.nordmine.crystalmoney.NumberWithText;
import ru.nordmine.crystalmoney.R;

public class AccountListActivity extends Activity {

	private static final int EDIT_ACCOUNT = 10;
	private ListView listView;
	private List<AccountItem> items;
	private AccountDao dao = new AccountDao(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_list);

		listView = (ListView) findViewById(R.id.categoryListView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Intent intent = new Intent();
				intent.setClass(AccountListActivity.this, AccountActivity.class);
				Bundle b = new Bundle();
				b.putInt("defStrID", items.get(position).getId());
				intent.putExtras(b);
				startActivityForResult(intent, EDIT_ACCOUNT);
			}
		});
		
		registerForContextMenu(listView);

		loadAccountsFromDatabase();
	}

	private void loadAccountsFromDatabase() {
	 	items = dao.getAll();
		NumberWithText[] iconsOriginal = AccountActivity.getAccountIcons();
		List<AccountItem> icons = new ArrayList<AccountItem>();
		
		for (AccountItem kvi : items) {
			NumberWithText item = iconsOriginal[kvi.getIconId()];
			icons.add(new AccountItem(kvi.getId(), kvi.getName(), item
					.getNumber(), kvi.getAmount()));
		}

		listView.setAdapter(new AccountItemAdapter(this,
					android.R.layout.simple_list_item_1, icons
							.toArray(new AccountItem[icons.size()])));
	}
	
	public void onAddButtonClick(View v) {
		Intent intent = new Intent(AccountListActivity.this,
				AccountActivity.class);
		startActivityForResult(intent, EDIT_ACCOUNT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == EDIT_ACCOUNT && resultCode == RESULT_OK) {
			loadAccountsFromDatabase();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.account_list, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.categoryListView) {
			AdapterView.AdapterContextMenuInfo info = 
					(AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(items.get(info.position).getName());
			menu.add(0, 0, 0, R.string.caption_move_to_another_account);
			menu.add(0, 1, 1, R.string.caption_delete);
		}		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int menuItemIndex = item.getItemId();
		if(menuItemIndex == 1)
		{
			AdapterView.AdapterContextMenuInfo info = 
					(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();			
			deleteRecordById(items.get(info.position).getId());
		}
		return super.onContextItemSelected(item);
	}

	private void deleteRecordById(int id) {
		dao.removeById(id);
		loadAccountsFromDatabase();
	}

}
