package ru.nordmine.crystalmoney.exchange;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Spinner;

import java.util.List;

import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.account.AccountDao;
import ru.nordmine.crystalmoney.account.AccountItem;
import ru.nordmine.crystalmoney.account.AccountItemSimpleAdapter;

public class ExchangeActivity extends Activity {

    private AccountDao accountDao = new AccountDao(this);
    private List<AccountItem> accountItems;

    private Spinner fromAccountSpinner, toAccountSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        fromAccountSpinner = (Spinner) findViewById(R.id.fromAccountSpinner);
        toAccountSpinner = (Spinner) findViewById(R.id.toAccountSpinner);

        accountItems = accountDao.getAll();

        addItemsOnAccountTypeSpinner(fromAccountSpinner);
        addItemsOnAccountTypeSpinner(toAccountSpinner);

        Bundle bundle = getIntent().getExtras();
        int fromAccountId = bundle.getInt("fromAccountId");

        for(int i = 0; i < accountItems.size(); i++)
        {
            AccountItem item = accountItems.get(i);
            if(item.getId() == fromAccountId)
            {
                fromAccountSpinner.setSelection(i);
                break;
            }
        }
    }

    private void addItemsOnAccountTypeSpinner(Spinner spinner) {
        AccountItemSimpleAdapter dataAdapter = new AccountItemSimpleAdapter(this,
                R.layout.row_with_icon, accountItems.toArray(new AccountItem[accountItems
                .size()]));
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

}
