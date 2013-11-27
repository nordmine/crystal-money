package ru.nordmine.crystalmoney.trx;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.nordmine.crystalmoney.MainActivity;
import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountDao;

public abstract class TransactionListActivity extends Activity {
	
	protected ListView listView;
    protected TextView pagerTextView;
	protected int categoryType = 0;
	protected int transactionType = 0;
	protected List<TransactionItem> trxItems;
	protected TransactionDao dao;
	protected AccountDao accountDao = new AccountDao(this);
    protected long startDate;

    private static final long ONE_DAY = 1000 * 3600 * 24;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trx_list);

        pagerTextView = (TextView) findViewById(R.id.pagerTextView);

        Bundle bundle = getIntent().getExtras();

        Calendar c = Calendar.getInstance();
        if (bundle != null && bundle.containsKey("selectedDate")) {
            startDate = bundle.getLong("selectedDate");
            c.setTimeInMillis(startDate);
        }
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        startDate = c.getTimeInMillis();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	protected void refreshItems() {
		trxItems = dao.getAll(startDate, startDate + ONE_DAY);

        Date d = new Date(startDate);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy");
        pagerTextView.setText(sdf.format(d));
		
		listView.setAdapter(new TransactionItemAdapter(this, android.R.layout.simple_list_item_1,
                            trxItems.toArray(new TransactionItem[trxItems.size()])));
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
			DecimalFormat df = new DecimalFormat("###,##0.00");
			menu.setHeaderTitle(df.format(trxItems.get(info.position).getAmount().toPlainString()));
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

    public void onPrevButtonClick(View v) {
        startDate -= ONE_DAY;
        refreshItems();
    }

    public void onNextButtonClick(View v) {
        startDate += ONE_DAY;
        refreshItems();
    }

}
