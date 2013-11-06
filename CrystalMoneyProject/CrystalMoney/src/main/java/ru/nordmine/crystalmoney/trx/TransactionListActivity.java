package ru.nordmine.crystalmoney.trx;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

import ru.nordmine.crystalmoney.MainActivity;
import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountDao;

public abstract class TransactionListActivity extends Activity {
	
	protected ListView listView;
	protected int categoryType = 0;
	protected int transactionType = 0;
	protected List<TransactionItem> trxItems;
	protected TransactionDao dao;
	protected AccountDao accountDao = new AccountDao(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trx_list);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	protected void refreshItems() {
		trxItems = dao.getAll();
		
		listView.setAdapter(new TransactionItemAdapter(this,
				android.R.layout.simple_list_item_1, trxItems
						.toArray(new TransactionItem[trxItems.size()])));
	}
	
	protected void deleteRecordById(int id) {
		dao.removeById(id);
		refreshItems();
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int menuItemIndex = item.getItemId();
		if (menuItemIndex == 0) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();
			deleteRecordById(trxItems.get(info.position).getId());
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.categoryListView) {
			AdapterView.AdapterContextMenuInfo info = 
					(AdapterView.AdapterContextMenuInfo) menuInfo;
			DecimalFormat df = new DecimalFormat("0.00");
			menu.setHeaderTitle(df.format(trxItems.get(info.position).getAmount()));
			menu.add(R.string.caption_delete);		
		}		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.income_list, menu);
		return true;
	}
	
	protected boolean checkAccountListForEmpty() {
		if (accountDao.getTotalCount() > 0) {
			return true;
		}
		Toast.makeText(this, R.string.caption_zero_account_exception,
				Toast.LENGTH_LONG).show();
		return false;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.action_add:
                onAddButtonClick();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    protected abstract void onAddButtonClick();

}
