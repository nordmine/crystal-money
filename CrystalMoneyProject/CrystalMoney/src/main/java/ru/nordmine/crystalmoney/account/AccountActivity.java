package ru.nordmine.crystalmoney.account;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.nordmine.crystalmoney.IconWithTextAdapter;
import ru.nordmine.crystalmoney.NumberWithText;
import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.handlers.OnAmountFocusChangeListener;
import ru.nordmine.crystalmoney.stat.StatisticsDao;

public class AccountActivity extends Activity {

	private EditText editAccountName;
	private EditText editAmount;
	private EditText editComment;
	private CheckBox isCardCheckBox;
	private Spinner accountTypeSpinner;
    private TextView cardNumberTextView;
    private EditText cardNumberEditText;
    private TextView smsSenderTextView;
    private EditText smsSenderEditText;

	private AccountDao dao = new AccountDao(this);

    private BigDecimal amountFromStat = BigDecimal.ZERO;

	private int id = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		editAccountName = (EditText) findViewById(R.id.editAccountName);
		editComment = (EditText) findViewById(R.id.accountCommentEditText);
		isCardCheckBox = (CheckBox) findViewById(R.id.isCardCheckBox);
		accountTypeSpinner = (Spinner) findViewById(R.id.accountSpinner);
        cardNumberTextView = (TextView) findViewById(R.id.cardNumberTextView);
        cardNumberEditText = (EditText) findViewById(R.id.cardNumberEditText);
        smsSenderTextView = (TextView) findViewById(R.id.smsSenderTextView);
        smsSenderEditText = (EditText) findViewById(R.id.smsSenderEditText);

        editAmount = (EditText) findViewById(R.id.editAmount);
        editAmount.setOnFocusChangeListener(new OnAmountFocusChangeListener());

        isCardCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                processIsCardCheckBox();
            }
        });

		addItemsOnAccountTypeSpinner();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("defStrID")) {
			id = bundle.getInt("defStrID");

            StatisticsDao statistics = new StatisticsDao(this);
            Map<Integer, BigDecimal> totalAmount = statistics.getTotalAmount();
            amountFromStat = totalAmount.containsKey(id) ? totalAmount.get(id) : BigDecimal.ZERO;

			loadRecordById(id);
		} else {
			editAmount.setText("0");
		}

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	}

	public static NumberWithText[] getAccountIcons() {
		List<NumberWithText> list = new ArrayList<NumberWithText>();
		list.add(new NumberWithText(R.drawable.cash, "Наличные"));
		list.add(new NumberWithText(R.drawable.wallet_icon, "Кошелёк"));
		list.add(new NumberWithText(R.drawable.bank, "Банк"));
		list.add(new NumberWithText(R.drawable.dollar, "Доллары"));
		list.add(new NumberWithText(R.drawable.visa, "Visa"));
        list.add(new NumberWithText(R.drawable.smartcard, "Смарткарта"));
        list.add(new NumberWithText(R.drawable.money_envelope, "Конверт"));
		return list.toArray(new NumberWithText[list.size()]);
	}

	private void addItemsOnAccountTypeSpinner() {
		IconWithTextAdapter dataAdapter = new IconWithTextAdapter(this,
				R.layout.row_with_icon, getAccountIcons());
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		accountTypeSpinner.setAdapter(dataAdapter);
	}

	private void loadRecordById(int id) {
		AccountItem item = dao.getById(id);
		editAccountName.setText(item.getName());
		editAmount.setText(amountFromStat.add(item.getAmount()).toPlainString());
		editComment.setText(item.getComment());
		isCardCheckBox.setChecked(item.isCard());
        processIsCardCheckBox();
		accountTypeSpinner.setSelection(item.getIconId());
        cardNumberEditText.setText(item.getCardNumber());
        smsSenderEditText.setText(item.getSmsSender());
	}

    private void processIsCardCheckBox() {
        if (isCardCheckBox.isChecked()) {
            cardNumberTextView.setVisibility(View.VISIBLE);
            cardNumberEditText.setVisibility(View.VISIBLE);
            smsSenderTextView.setVisibility(View.VISIBLE);
            smsSenderEditText.setVisibility(View.VISIBLE);
        } else {
            cardNumberTextView.setVisibility(View.GONE);
            cardNumberEditText.setVisibility(View.GONE);
            smsSenderTextView.setVisibility(View.GONE);
            smsSenderEditText.setVisibility(View.GONE);
        }
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.account, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, AccountListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

	private void onSaveClick() {
		String name = editAccountName.getText().toString();
		
		String amountText = editAmount.getText().toString();
		if (amountText.trim().isEmpty()) {
			amountText = "0.00";
		}
		BigDecimal amount = new BigDecimal(amountText).setScale(2, RoundingMode.HALF_UP);
        // корректировка баланса с учётом существующих транзакций
        amount = amount.subtract(amountFromStat);

		String comment = editComment.getText().toString();
		boolean isCard = isCardCheckBox.isChecked();
		int iconId = accountTypeSpinner.getSelectedItemPosition();
        String cardNumber = cardNumberEditText.getText().toString();
        String smsSender = smsSenderEditText.getText().toString();

		AccountItem item = new AccountItem(id, name, iconId, amount, isCard, comment, cardNumber, smsSender);
		dao.save(id, item);

		setResult(RESULT_OK);
		finish();
	}
}
