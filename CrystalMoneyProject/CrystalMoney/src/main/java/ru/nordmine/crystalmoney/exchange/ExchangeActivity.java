package ru.nordmine.crystalmoney.exchange;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountDao;
import ru.nordmine.crystalmoney.account.AccountItem;
import ru.nordmine.crystalmoney.account.AccountItemSimpleAdapter;

public class ExchangeActivity extends Activity {

    private AccountDao accountDao = new AccountDao(this);
    private ExchangeDao exchangeDao = new ExchangeDao(this);

    private List<AccountItem> accountItems;
    private List<AccountItem> anotherItems;

    private int id = 0;

    private Spinner fromAccountSpinner, toAccountSpinner;
    private EditText amountEditText;

    private ExchangeItem loadedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        fromAccountSpinner = (Spinner) findViewById(R.id.fromAccountSpinner);
        toAccountSpinner = (Spinner) findViewById(R.id.toAccountSpinner);
        amountEditText = (EditText) findViewById(R.id.amountEditText);

        fromAccountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedAccountId = accountItems.get(fromAccountSpinner.getSelectedItemPosition()).getId();
                onFromAccountSpinnerSelectionChanged(selectedAccountId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        accountItems = accountDao.getAll();

        addItemsOnAccountTypeSpinner(fromAccountSpinner, accountItems);

        Bundle bundle = getIntent().getExtras();
        int fromAccountId = bundle.getInt("fromAccountId");
        onFromAccountSpinnerSelectionChanged(fromAccountId);
        if(bundle.containsKey("id"))
        {
            this.id = bundle.getInt("id");
            loadedItem = loadRecordById(id);
        }
    }

    private void onFromAccountSpinnerSelectionChanged(int fromAccountId) {
        anotherItems = new ArrayList<AccountItem>();

        for (int i = 0; i < accountItems.size(); i++) {
            AccountItem item = accountItems.get(i);
            if (item.getId() == fromAccountId) {
                fromAccountSpinner.setSelection(i);
            } else {
                anotherItems.add(item);
            }
        }

        addItemsOnAccountTypeSpinner(toAccountSpinner, anotherItems);
    }

    private void addItemsOnAccountTypeSpinner(Spinner spinner, List<AccountItem> dataSource) {
        AccountItemSimpleAdapter dataAdapter = new AccountItemSimpleAdapter(this,
                R.layout.row_with_icon, dataSource.toArray(new AccountItem[dataSource.size()]));
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private ExchangeItem loadRecordById(int id) {
        ExchangeItem item = exchangeDao.getById(id);

        DecimalFormat df = new DecimalFormat("0.00");
        amountEditText.setText(df.format(item.getAmount()));
        setSelection(fromAccountSpinner, accountItems, item.getFromAccountId());
        setSelection(toAccountSpinner, anotherItems, item.getToAccountId());

        return item;
    }

    private static void setSelection(Spinner spinner, List<AccountItem> items, int itemId) {
        for (int i = 0; i < items.size(); i++) {
            AccountItem item = items.get(i);
            if (item.getId() == itemId) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public void onSaveButtonClick(View v) {
        int fromAccountId = accountItems.get(fromAccountSpinner.getSelectedItemPosition()).getId();
        int toAccountId = anotherItems.get(toAccountSpinner.getSelectedItemPosition()).getId();
        double amount = Double.parseDouble(amountEditText.getText().toString());
        long created;
        if (loadedItem != null) {
            created = loadedItem.getCreated();
        } else {
            created = new Date().getTime();
        }
        ExchangeItem item = new ExchangeItem(id, created, fromAccountId, toAccountId, amount, null, null);
        exchangeDao.save(id, item);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ExchangeListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
