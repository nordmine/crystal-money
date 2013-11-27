package ru.nordmine.crystalmoney.exchange;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountDao;
import ru.nordmine.crystalmoney.account.AccountItem;
import ru.nordmine.crystalmoney.account.AccountItemSpinnerAdapter;

public class ExchangeActivity extends Activity {

    private AccountDao accountDao = new AccountDao(this);
    private ExchangeDao exchangeDao = new ExchangeDao(this);

    private List<AccountItem> accountItems;
    private List<AccountItem> anotherItems;

    private int id = 0;

    private Spinner fromAccountSpinner, toAccountSpinner;
    private EditText amountEditText;
    private Button dateButton;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        fromAccountSpinner = (Spinner) findViewById(R.id.fromAccountSpinner);
        toAccountSpinner = (Spinner) findViewById(R.id.toAccountSpinner);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        dateButton = (Button) findViewById(R.id.dateButton);

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
        if (bundle.containsKey("fromAccountId")) {
            int fromAccountId = bundle.getInt("fromAccountId");
            onFromAccountSpinnerSelectionChanged(fromAccountId);
        }
        if (bundle.containsKey("id")) {
            this.id = bundle.getInt("id");
            loadRecordById(id);
        }
        setTextForDateButton();
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
        AccountItemSpinnerAdapter dataAdapter = new AccountItemSpinnerAdapter(this,
                R.layout.row_with_icon, dataSource.toArray(new AccountItem[dataSource.size()]));
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void loadRecordById(int id) {
        ExchangeItem item = exchangeDao.getById(id);

        amountEditText.setText(item.getAmount().toPlainString());
        setSelection(fromAccountSpinner, accountItems, item.getFromAccountId());
        setSelection(toAccountSpinner, anotherItems, item.getToAccountId());

        calendar.setTimeInMillis(item.getCreated());
    }

    private void setTextForDateButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        dateButton.setText(sdf.format(new Date(calendar.getTimeInMillis())));
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

        String amountText = amountEditText.getText().toString();
        if (amountText.trim().isEmpty()) {
            amountText = "0.00";
        }

        BigDecimal amount = new BigDecimal(amountText).setScale(2, RoundingMode.HALF_UP);

        long created = calendar.getTimeInMillis();

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

    public void onDateButtonClick(View view) {
        new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        setTextForDateButton();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}
