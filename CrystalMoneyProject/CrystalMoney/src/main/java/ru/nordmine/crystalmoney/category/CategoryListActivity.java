package ru.nordmine.crystalmoney.category;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ru.nordmine.crystalmoney.R;
import ru.nordmine.crystalmoney.db.MyDb;
import ru.nordmine.crystalmoney.db.WhereClauseItem;

public class CategoryListActivity extends Activity implements CategoryDialog.OnCategoryRenameListener {

	private ListView listView;
	private List<CategoryItem> items;
	private int categoryType = 0;
	private EditText categoryNameEditText;
	private CategoryDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_list);

		categoryNameEditText = (EditText) findViewById(R.id.categoryNameText);
		listView = (ListView) findViewById(R.id.categoryListView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
                CategoryItem category = items.get(position);
                setCategoryAndClose(category);
			}
		});

		Bundle bundle = getIntent().getExtras();
		categoryType = bundle.getInt("categoryType");
		
		this.dao = new CategoryDao(this, categoryType);

		registerForContextMenu(listView);

		loadCategoriesFromDatabase();
	}

    private void setCategoryAndClose(CategoryItem category) {
        Intent intent = new Intent();
        intent.putExtra("categoryId", category.getId());
        intent.putExtra("categoryName", category.getName());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void loadCategoriesFromDatabase() {
		items = dao.getAll();
		
		CategoryItemAdapter adapter = new CategoryItemAdapter(this,
				android.R.layout.simple_list_item_1, items
				.toArray(new CategoryItem[items.size()]));

		listView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.category_list, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.categoryListView) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(items.get(info.position).getName());
            menu.add(0, 0, 0, R.string.caption_rename);
            menu.add(0, 1, 1, R.string.caption_delete);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int menuItemIndex = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final CategoryItem category = items.get(info.position);
        if(menuItemIndex == 0) {
            showCategoryDialog(category);
        }
        if (menuItemIndex == 1) {
            deleteRecordById(category.getId());
        }
		return super.onContextItemSelected(item);
	}

	private void deleteRecordById(int id) {
		dao.removeById(id);
		loadCategoriesFromDatabase();
	}
	
	public void addNewCategory(View v)
	{
		String categoryName = categoryNameEditText.getText().toString().trim();
        categoryNameEditText.setText("");

        if (!checkCategoryName(categoryName)) {
            return;
        }

        CategoryItem item = new CategoryItem(0, categoryType, categoryName);
		dao.save(0, item);

        List<CategoryItem> categories = dao.getByName(categoryName);
        setCategoryAndClose(categories.get(0));
	}

    private boolean checkCategoryName(String categoryName) {
        if (categoryName.length() == 0) {
            Toast.makeText(this, R.string.message_empty_category_name, Toast.LENGTH_LONG).show();
            return false;
        }

        int categoryNameCount = dao.getTotalCountWithClause(new WhereClauseItem[]{new WhereClauseItem(MyDb.CAT_NAME, "like", categoryName)});
        if (categoryNameCount > 0) {
            Toast.makeText(this, R.string.message_category_already_exists, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    void showCategoryDialog(CategoryItem item) {
        DialogFragment dialog = CategoryDialog.newInstance(item);
        dialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onCategoryRenamed(CategoryItem category) {
        if (checkCategoryName(category.getName())) {
            dao.save(category.getId(), category);
            loadCategoriesFromDatabase();
        }
    }
}
