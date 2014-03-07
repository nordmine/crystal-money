package ru.nordmine.crystalmoney.trx;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ru.nordmine.crystalmoney.MainActivity;
import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountDao;

public abstract class TransactionListActivity extends Activity implements ActionBar.TabListener {

    protected ListView listView;
    protected TextView pagerTextView;
    protected int categoryType = 0;
    protected int transactionType = 0;
    protected List<TransactionItem> trxItems;
    protected TransactionDao dao;
    protected AccountDao accountDao = new AccountDao(this);
    protected Calendar startCalendar = Calendar.getInstance();
    protected Calendar endCalendar = Calendar.getInstance();
    protected int calendarField;
    protected int dayOfMonthInDailyMode;
    private Resources res;

    private static final String MY_PREFS = "my_prefs1";
    private static final String START_DATE = "start_date";
    private static final String CALENDAR_FIELD = "calendar_field";
    private static final String DAY_OF_MONTH_IN_DAILY_MODE = "day_of_month_daily_mode";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trx_list);

        initTransactionDao();

        listView = (ListView) findViewById(R.id.categoryListView);
        pagerTextView = (TextView) findViewById(R.id.pagerTextView);

        Bundle bundle = getIntent().getExtras();

        startCalendar = Calendar.getInstance();
        if (bundle != null && bundle.containsKey("selectedDate")) {
            long startDate = bundle.getLong("selectedDate");
            startCalendar.setTimeInMillis(startDate);
        }
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        initEndCalendar();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        res = getResources();
        preferences = getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);
        startCalendar.setTimeInMillis(preferences.getLong(START_DATE, startCalendar.getTimeInMillis()));
        calendarField = preferences.getInt(CALENDAR_FIELD, Calendar.DAY_OF_MONTH);
        dayOfMonthInDailyMode = preferences.getInt(DAY_OF_MONTH_IN_DAILY_MODE, 1);

        ActionBar.Tab dayTab = actionBar.newTab().setText(res.getString(R.string.caption_day)).setTabListener(this);
        actionBar.addTab(dayTab);
        ActionBar.Tab monthTab = actionBar.newTab().setText(res.getString(R.string.caption_month)).setTabListener(this);
        actionBar.addTab(monthTab);

        selectTabByPreferences();
        initEndCalendar();
    }

    private void selectTabByPreferences() {
        ActionBar actionBar = getActionBar();
        int selectedTabIndex = -1;
        if (calendarField == Calendar.DAY_OF_MONTH) {
            selectedTabIndex = 0;
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonthInDailyMode);
        }
        if (calendarField == Calendar.MONTH) {
            selectedTabIndex = 1;
        }
        actionBar.setSelectedNavigationItem(selectedTabIndex);
    }

    private void initEndCalendar() {
        endCalendar.setTimeInMillis(startCalendar.getTimeInMillis());
        endCalendar.add(calendarField, 1);
    }

    protected void refreshItems() {
        trxItems = dao.getAll(startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis());

        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy");
        if (calendarField == Calendar.MONTH) {
            String[] monthNames = res.getStringArray(R.array.month_names);
            DateFormatSymbols russianSymbols = new DateFormatSymbols();
            russianSymbols.setMonths(monthNames);
            sdf = new SimpleDateFormat("MMMM yyyy", russianSymbols);
        }
        pagerTextView.setText(sdf.format(startCalendar.getTime()));

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
            menu.setHeaderTitle(df.format(trxItems.get(info.position).getAmount().doubleValue()));
            menu.add(R.string.caption_delete);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trx_list, menu);
        return true;
    }

    protected boolean checkAccountListForEmpty() {
        if (accountDao.getTotalCount() > 0) {
            return true;
        }
        Toast.makeText(this, R.string.caption_zero_account_exception, Toast.LENGTH_LONG).show();
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
        startCalendar.add(calendarField, -1);
        initEndCalendar();
        refreshItems();
    }

    public void onNextButtonClick(View v) {
        startCalendar.add(calendarField, 1);
        initEndCalendar();
        refreshItems();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (tab.getText().equals(res.getString(R.string.caption_day))) {
            calendarField = Calendar.DAY_OF_MONTH;
            startCalendar.set(calendarField, dayOfMonthInDailyMode);
        }
        if (tab.getText().equals(res.getString(R.string.caption_month))) {
            startCalendar.set(Calendar.DAY_OF_MONTH, 1);
            calendarField = Calendar.MONTH;
        }
        initEndCalendar();
        refreshItems();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (tab.getText().equals(res.getString(R.string.caption_day))) {
            dayOfMonthInDailyMode = startCalendar.get(Calendar.DAY_OF_MONTH);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    protected abstract void initTransactionDao();

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        int selectedIndex = getActionBar().getSelectedNavigationIndex();
        editor.putInt(CALENDAR_FIELD, calendarField);
        if (selectedIndex == 0) {
            editor.putInt(DAY_OF_MONTH_IN_DAILY_MODE, startCalendar.get(Calendar.DAY_OF_MONTH));
        } else {
            editor.putInt(DAY_OF_MONTH_IN_DAILY_MODE, dayOfMonthInDailyMode);
        }
        editor.putLong(START_DATE, startCalendar.getTimeInMillis());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectTabByPreferences();
    }
}
