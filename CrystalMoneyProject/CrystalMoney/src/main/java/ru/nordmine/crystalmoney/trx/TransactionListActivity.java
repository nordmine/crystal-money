package ru.nordmine.crystalmoney.trx;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import java.util.Date;
import java.util.List;

import ru.nordmine.crystalmoney.MainActivity;
import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountDao;

public abstract class TransactionListActivity extends FragmentActivity implements ActionBar.TabListener {

    protected ListView listView;
    protected TextView pagerTextView;
    protected int categoryType = 0;
    protected int transactionType = 0;
    protected List<TransactionItem> trxItems;
    protected TransactionDao dao;
    protected AccountDao accountDao = new AccountDao(this);
    protected Calendar startCalendar = Calendar.getInstance();
    protected Calendar endCalendar = Calendar.getInstance();
    protected int calendarField = Calendar.DAY_OF_MONTH;
    private Resources res;

    private static final String MY_PREFS = "my_prefs1";
    private static final String SELECTED_TAB_INDEX = "selected_tab_name";
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

        ActionBar.Tab dayTab = actionBar.newTab().setText(res.getString(R.string.caption_day)).setTabListener(this);
        actionBar.addTab(dayTab);
        ActionBar.Tab monthTab = actionBar.newTab().setText(res.getString(R.string.caption_month)).setTabListener(this);
        actionBar.addTab(monthTab);

        selectTabByPreferences();
    }

    private void selectTabByPreferences() {
        ActionBar actionBar = getActionBar();
        int selectedTabIndex = preferences.getInt(SELECTED_TAB_INDEX, 0);
        actionBar.setSelectedNavigationItem(selectedTabIndex);
    }

    private void initEndCalendar() {
        endCalendar.setTimeInMillis(startCalendar.getTimeInMillis());
        endCalendar.set(calendarField, endCalendar.get(calendarField) + 1);
    }

    protected void refreshItems() {
        trxItems = dao.getAll(startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis());

        Date d = new Date(startCalendar.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy");
        if (calendarField == Calendar.MONTH) {
            String[] monthNames = res.getStringArray(R.array.month_names);
            DateFormatSymbols russianSymbols = new DateFormatSymbols();
            russianSymbols.setMonths(monthNames);
            sdf = new SimpleDateFormat("MMMM yyyy", russianSymbols);
        }
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
        startCalendar.set(calendarField, startCalendar.get(calendarField) - 1);
        initEndCalendar();
        refreshItems();
    }

    public void onNextButtonClick(View v) {
        startCalendar.set(calendarField, startCalendar.get(calendarField) + 1);
        initEndCalendar();
        refreshItems();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (tab.getText().equals(res.getString(R.string.caption_day))) {
            calendarField = Calendar.DAY_OF_MONTH;
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
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    protected abstract void initTransactionDao();

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SELECTED_TAB_INDEX, getActionBar().getSelectedNavigationIndex());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectTabByPreferences();
    }
}
