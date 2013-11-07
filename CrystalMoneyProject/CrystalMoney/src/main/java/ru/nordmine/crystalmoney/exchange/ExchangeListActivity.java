package ru.nordmine.crystalmoney.exchange;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import ru.nordmine.crystalmoney.MainActivity;
import ru.nordmine.crystalmoney.R;

public class ExchangeListActivity extends Activity {

    public static final int EXCHANGE_AMOUNT_EDIT = 41;

    private ListView listView;
    private List<ExchangeItem> items;
    private ExchangeDao dao = new ExchangeDao(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_list);

        listView = (ListView) findViewById(R.id.exchangeListView);

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
        items = dao.getAll();

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
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.exchangeListView) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(Double.toString(items.get(info.position).getAmount())); // todo formatting
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

}
