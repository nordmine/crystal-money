package ru.nordmine.crystalmoney.exchange;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ru.nordmine.crystalmoney.MainActivity;
import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountDao;

public class ExchangeListActivity extends Activity {

    public static final int EXCHANGE_AMOUNT_EDIT = 41;
    private static final String EXCHANGE_START_DATE = "exchange_start_date";
    private static final String MY_PREFS = "my_prefs2";

    private ListView listView;
    private List<ExchangeItem> items;
    private ExchangeDao dao = new ExchangeDao(this);
    private AccountDao accountDao = new AccountDao(this);
    private Calendar startCalendar;
    private int calendarField = Calendar.MONTH;

    private Resources res;
    private SharedPreferences preferences;

    protected TextView pagerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_list);

        listView = (ListView) findViewById(R.id.exchangeListView);
        pagerTextView = (TextView) findViewById(R.id.pagerTextView);

        preferences = getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);

        startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        startCalendar.setTimeInMillis(preferences.getLong(EXCHANGE_START_DATE, startCalendar.getTimeInMillis()));

        res = getResources();

        loadExchangesFromDatabase();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {
                Intent intent = new Intent();
                intent.setClass(ExchangeListActivity.this, ExchangeActivity.class);
                Bundle b = new Bundle();
                b.putInt("id", items.get(position).getId());
                b.putInt("fromAccountId", items.get(position).getFromAccountId());
                intent.putExtras(b);
                startActivityForResult(intent, EXCHANGE_AMOUNT_EDIT);
            }
        });

        registerForContextMenu(listView);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void loadExchangesFromDatabase() {
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(startCalendar.getTimeInMillis());
        endCalendar.add(calendarField, 1);

        String[] monthNames = res.getStringArray(R.array.month_names);
        DateFormatSymbols russianSymbols = new DateFormatSymbols();
        russianSymbols.setMonths(monthNames);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", russianSymbols);
        pagerTextView.setText(sdf.format(startCalendar.getTime()));

        items = dao.getAll(startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis());

        listView.setAdapter(new ExchangeItemAdapter(this, android.R.layout.simple_list_item_1,
                items.toArray(new ExchangeItem[items.size()])));
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

    private void onAddButtonClick() {
        if (!checkAccountListForEmpty()) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(ExchangeListActivity.this, ExchangeActivity.class);
        Bundle b = new Bundle();
        b.putInt("fromAccountId", accountDao.getAll().get(0).getId());
        intent.putExtras(b);
        startActivityForResult(intent, EXCHANGE_AMOUNT_EDIT);
    }

    private boolean checkAccountListForEmpty() {
        if (accountDao.getTotalCount() >= 2) {
            return true;
        }
        Toast.makeText(this, R.string.caption_zero_account_exception_in_exchange,
                Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.exchangeListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            DecimalFormat df = new DecimalFormat("###,##0.00");
            menu.setHeaderTitle(df.format(items.get(info.position).getAmount().doubleValue()));
            menu.add(0, 0, 0, R.string.caption_delete);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(menuItemIndex == 0)
        {
            deleteRecordById(items.get(info.position).getId());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteRecordById(int id) {
        dao.removeById(id);
        loadExchangesFromDatabase();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXCHANGE_AMOUNT_EDIT && resultCode == RESULT_OK) {
            loadExchangesFromDatabase();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exchange_list, menu);
        return true;
    }

    public void onNextButtonClick(View v) {
        startCalendar.add(calendarField, 1);
        loadExchangesFromDatabase();
    }

    public void onPrevButtonClick(View v) {
        startCalendar.add(calendarField, -1);
        loadExchangesFromDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(EXCHANGE_START_DATE, startCalendar.getTimeInMillis());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCalendar.setTimeInMillis(preferences.getLong(EXCHANGE_START_DATE, startCalendar.getTimeInMillis()));
    }

}
