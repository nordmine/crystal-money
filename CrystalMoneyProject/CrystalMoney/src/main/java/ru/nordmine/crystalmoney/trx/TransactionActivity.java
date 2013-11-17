package ru.nordmine.crystalmoney.trx;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountDao;
import ru.nordmine.crystalmoney.account.AccountItem;
import ru.nordmine.crystalmoney.account.AccountItemSimpleAdapter;
import ru.nordmine.crystalmoney.category.CategoryDao;
import ru.nordmine.crystalmoney.category.CategoryItem;
import ru.nordmine.crystalmoney.category.CategoryListActivity;

public class TransactionActivity extends Activity {
	
	public static final int EDIT_CATEGORY_LIST = 20;

	private Spinner accountSpinner;
	private DatePicker datePicker;
	private EditText amountEditText;
	private EditText commentEditText;
	private Button categoryButton;

	private List<AccountItem> accountItems;
    private long selectedDate;

	private int id = 0;
	private int categoryId = 0;
	
	private int transactionType = 0;
	
	private AccountDao accountDao = new AccountDao(this);
	private TransactionDao trxDao;
    private Long created;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trx);

		accountSpinner = (Spinner) findViewById(R.id.accountSpinner);
		datePicker = (DatePicker) findViewById(R.id.datePicker3);
		amountEditText = (EditText) findViewById(R.id.amountEditText);
		commentEditText = (EditText) findViewById(R.id.commentEditText);
		categoryButton = (Button) findViewById(R.id.categoryButton);

		addItemsOnAccountTypeSpinner();

		Bundle bundle = getIntent().getExtras();
		transactionType = bundle.getInt("transactionType");

        Calendar c = Calendar.getInstance();
        if (bundle.containsKey("selectedDate")) {
            this.selectedDate = bundle.getLong("selectedDate");
            c.setTimeInMillis(selectedDate);
        } else {
            c.setTime(new Date());
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, month, day, null);
        datePicker.setCalendarViewShown(false);
		
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

	private void setDefaultCategory() {
		CategoryDao categoryDao = new CategoryDao(this, transactionType);
		List<CategoryItem> categories = categoryDao.getAll();
		if (!categories.isEmpty()) {
			CategoryItem categoryItem = categories.get(0);
			this.categoryId = categoryItem.getId();
			categoryButton.setText(categoryItem.getName());
		}
	}

	private void addItemsOnAccountTypeSpinner() {		
		accountItems = accountDao.getAll();

		AccountItemSimpleAdapter dataAdapter = new AccountItemSimpleAdapter(this,
				R.layout.row_with_icon, accountItems.toArray(new AccountItem[accountItems
						.size()]));
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		accountSpinner.setAdapter(dataAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.income, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

	private void loadRecordById(int id) {
		TransactionItem trxItem = trxDao.getById(id);

		commentEditText.setText(trxItem.getComment());
		amountEditText.setText(Double.toString(trxItem.getAmount()));
        this.created = trxItem.getCreated();
		final Calendar c = Calendar.getInstance();
		c.setTimeInMillis(trxItem.getCreated());
		datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), null);
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

	public void onSaveButtonClick(View v) {
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
		Double amount = Double.parseDouble(amountText);
		
		int accountId = accountItems.get(
				accountSpinner.getSelectedItemPosition()).getId();
		Calendar cal = Calendar.getInstance();
        if (this.created != null) {
            cal.setTimeInMillis(this.created);
        }
        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
		long created = cal.getTimeInMillis();

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
		}
	}

}
