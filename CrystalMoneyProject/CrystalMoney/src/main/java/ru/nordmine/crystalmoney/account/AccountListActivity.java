package ru.nordmine.crystalmoney.account;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import ru.nordmine.crystalmoney.MainActivity;
import ru.nordmine.crystalmoney.NumberWithText;
import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.exchange.ExchangeActivity;
import ru.nordmine.crystalmoney.stat.StatisticsDao;

public class AccountListActivity extends Activity {

	private static final int EDIT_ACCOUNT = 10;
    public static final int EXCHANGE_AMOUNT = 40;

	private ListView listView;
    private TextView totalSumPerDayTextView;
    private TextView totalSumPerMonthTextView;
	private List<AccountItem> items;
	private AccountDao dao = new AccountDao(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_list);

		listView = (ListView) findViewById(R.id.categoryListView);
        totalSumPerDayTextView = (TextView) findViewById(R.id.totalSumPerDayTextView);
        totalSumPerMonthTextView = (TextView) findViewById(R.id.totalSumPerMonthTextView);

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

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void loadAccountsFromDatabase() {
	 	items = dao.getAll();
		NumberWithText[] iconsOriginal = AccountActivity.getAccountIcons();
		List<AccountItem> icons = new ArrayList<AccountItem>();

        StatisticsDao statistics = new StatisticsDao(this);
        Map<Integer, Double> totalAmount = statistics.getTotalAmount();
		
		for (AccountItem ai : items) {
			NumberWithText item = iconsOriginal[ai.getIconId()];
            Double statAmount = totalAmount.containsKey(ai.getId()) ? totalAmount.get(ai.getId()) : 0;
			icons.add(new AccountItem(ai.getId(), ai.getName(), item.getNumber(), statAmount, ai.isCard(), ai.getComment()));
		}

		listView.setAdapter(new AccountItemAdapter(this,
					android.R.layout.simple_list_item_1, icons
							.toArray(new AccountItem[icons.size()])));

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        double totalSumPerDay = statistics.getTotalOutcomeBetweenDate(c.getTimeInMillis(), null);
        totalSumPerDayTextView.setText("Расходы за день: " + totalSumPerDay);

        c.set(Calendar.DAY_OF_MONTH, 1);
        double totalSumPerMonth = statistics.getTotalOutcomeBetweenDate(c.getTimeInMillis(), null);
        totalSumPerMonthTextView.setText("Расходы за месяц: " + totalSumPerMonth);
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
                break;
        }
        return true;
    }
	
	public void onAddButtonClick() {
		Intent intent = new Intent(AccountListActivity.this, AccountActivity.class);
		startActivityForResult(intent, EDIT_ACCOUNT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == EDIT_ACCOUNT || requestCode == EXCHANGE_AMOUNT) && resultCode == RESULT_OK) {
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
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(menuItemIndex == 0)
        {
            Intent intent = new Intent(AccountListActivity.this, ExchangeActivity.class);
            intent.putExtra("fromAccountId", items.get(info.position).getId());
            startActivityForResult(intent, EXCHANGE_AMOUNT);
        }
		if(menuItemIndex == 1)
		{
			deleteRecordById(items.get(info.position).getId());
		}
		return super.onContextItemSelected(item);
	}

	private void deleteRecordById(int id) {
		dao.removeById(id);
		loadAccountsFromDatabase();
	}

}
