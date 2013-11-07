package ru.nordmine.crystalmoney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import ru.nordmine.crystalmoney.account.AccountListActivity;
import ru.nordmine.crystalmoney.exchange.ExchangeListActivity;
import ru.nordmine.crystalmoney.stat.StatActivity;
import ru.nordmine.crystalmoney.trx.IncomeListActivity;
import ru.nordmine.crystalmoney.trx.OutcomeListActivity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onAccountsButtonClick(View v)
	{
		Intent intent = new Intent(MainActivity.this, AccountListActivity.class);
		startActivity(intent);
	}
	
	public void onIncomeButtonClick(View v)
	{
		Intent intent = new Intent(MainActivity.this, IncomeListActivity.class);
		startActivity(intent);
	}
	
	public void onOutcomeListButtonClick(View v)
	{
		Intent intent = new Intent(MainActivity.this, OutcomeListActivity.class);
		startActivity(intent);
	}

    public void onStatButtonClick(View v)
    {
        Intent intent = new Intent(MainActivity.this, StatActivity.class);
        startActivity(intent);
    }

    public void onExchangeButtonClick(View v)
    {
        Intent intent = new Intent(MainActivity.this, ExchangeListActivity.class);
        startActivity(intent);
    }

}
