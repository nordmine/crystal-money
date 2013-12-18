package ru.nordmine.crystalmoney.trx;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountDao;
import ru.nordmine.crystalmoney.account.AccountItem;
import ru.nordmine.crystalmoney.account.AccountItemSpinnerAdapter;
import ru.nordmine.crystalmoney.category.CategoryDao;
import ru.nordmine.crystalmoney.category.CategoryItem;
import ru.nordmine.crystalmoney.category.CategoryListActivity;
import ru.nordmine.crystalmoney.handlers.OnAmountFocusChangeListener;

public class TransactionActivity extends Activity {

    public static final int EDIT_CATEGORY_LIST = 20;

    private Spinner accountSpinner;
    private EditText amountEditText;
    private EditText commentEditText;
    private Button categoryButton;
    private Button dateButton;

    private List<AccountItem> accountItems;
    private long selectedDate;
    private Calendar calendar = Calendar.getInstance();

    private int id = 0;
    private int categoryId = 0;

    private int transactionType = 0;

    private AccountDao accountDao = new AccountDao(this);
    private TransactionDao trxDao;

    private static final String MY_PREFS = "my_prefs";
    private SharedPreferences preferences;

    private static String defaultCategoryPreferenceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trx);

        accountSpinner = (Spinner) findViewById(R.id.accountSpinner);
        commentEditText = (EditText) findViewById(R.id.commentEditText);
        categoryButton = (Button) findViewById(R.id.categoryButton);
        dateButton = (Button) findViewById(R.id.dateButton);

        amountEditText = (EditText) findViewById(R.id.amountEditText);
        amountEditText.setOnFocusChangeListener(new OnAmountFocusChangeListener());

        this.preferences = getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);

        addItemsOnAccountTypeSpinner();

        Bundle bundle = getIntent().getExtras();
        transactionType = bundle.getInt("transactionType");

        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("selectedAccount", i);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        defaultCategoryPreferenceName = "defaultCategoryId" + transactionType;

        if (bundle.containsKey("selectedDate")) {
            this.selectedDate = bundle.getLong("selectedDate");
            calendar.setTimeInMillis(selectedDate);
        } else {
            calendar.setTime(new Date());
        }
        setTextForDateButton();

        trxDao = new TransactionDao(this, transactionType);
        if (bundle.containsKey("defStrID")) {
            id = bundle.getInt("defStrID");
            loadRecordById(id);
        } else {
            setDefaultCategory();
            amountEditText.setText("0");
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (transactionType == 1) {
            actionBar.setTitle(R.string.title_activity_income);
        }
        if (transactionType == 2) {
            actionBar.setTitle(R.string.title_activity_outcome);
        }
    }

    private void setTextForDateButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        dateButton.setText(sdf.format(new Date(calendar.getTimeInMillis())));
    }

    private void setDefaultCategory() {
        CategoryDao categoryDao = new CategoryDao(this, transactionType);
        CategoryItem categoryItem = null;
        // сначала пытаемся получить категорию из общих настроек
        if (preferences.contains(defaultCategoryPreferenceName)) {
            categoryItem = categoryDao.getById(preferences.getInt(defaultCategoryPreferenceName, 0));
        }
        // затем можно попробовать получить первую из доступных категорий
        if (categoryItem == null) {
            List<CategoryItem> categories = categoryDao.getAll();
            if (!categories.isEmpty()) {
                categoryItem = categories.get(0);
            }
        }

        if (categoryItem != null) {
            this.categoryId = categoryItem.getId();
            categoryButton.setText(categoryItem.getName());
        }
    }

    private void addItemsOnAccountTypeSpinner() {
        accountItems = accountDao.getAll();
        AccountItemSpinnerAdapter dataAdapter = new AccountItemSpinnerAdapter(this, R.layout.row_with_icon,
                accountItems.toArray(new AccountItem[accountItems.size()]));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(dataAdapter);

        if (preferences.contains("selectedAccount")) {
            accountSpinner.setSelection(preferences.getInt("selectedAccount", 0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trx, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = null;
                if (transactionType == 1) {
                    intent = new Intent(this, IncomeListActivity.class);
                }
                if (transactionType == 2) {
                    intent = new Intent(this, OutcomeListActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (this.selectedDate > 0) {
                    // чтобы список транзакций был открыт на той же дате
                    intent.putExtra("selectedDate", this.selectedDate);
                }
                startActivity(intent);
                break;
            case R.id.action_save:
                onSaveClick();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void loadRecordById(int id) {
        TransactionItem trxItem = trxDao.getById(id);

        commentEditText.setText(trxItem.getComment());
        amountEditText.setText(trxItem.getAmount().toPlainString());
        calendar.setTimeInMillis(trxItem.getCreated());
        for (int i = 0; i < accountItems.size(); i++) {
            AccountItem kvi = accountItems.get(i);
            if (kvi.getId() == trxItem.getAccountId()) {
                accountSpinner.setSelection(i);
            }
        }

        this.categoryId = trxItem.getCategoryId();
        this.categoryButton.setText(trxItem.getCategoryName());
        this.transactionType = trxItem.getTransactionType();
    }

    private void onSaveClick() {
        if (this.categoryId == 0) {
            Toast.makeText(this, R.string.caption_no_selected_category,
                    Toast.LENGTH_LONG).show();
            return;
        }

        String comment = commentEditText.getText().toString();

        String amountText = amountEditText.getText().toString();
        if (amountText.trim().isEmpty()) {
            amountText = "0.00";
        }
        BigDecimal amount = new BigDecimal(amountText).setScale(2, RoundingMode.HALF_UP);

        int accountId = accountItems.get(accountSpinner.getSelectedItemPosition()).getId();
        long created = calendar.getTimeInMillis();

        TransactionItem trxItem = new TransactionItem(id, comment, accountId,
                amount, created, accountId, transactionType, categoryId, null);
        trxDao.save(id, trxItem);

        setResult(RESULT_OK);
        finish();
    }

    public void onCategoryButtonClick(View v) {
        Intent intent = new Intent(TransactionActivity.this,
                CategoryListActivity.class);
        intent.putExtra("categoryType", transactionType);
        startActivityForResult(intent, EDIT_CATEGORY_LIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_CATEGORY_LIST && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String categoryName = bundle.getString("categoryName");
            this.categoryId = bundle.getInt("categoryId");
            categoryButton.setText(categoryName);
            // установка категории по умолчанию
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(defaultCategoryPreferenceName, this.categoryId);
            editor.apply();
        }
    }

    public void onDateButtonClick(View v) {
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
