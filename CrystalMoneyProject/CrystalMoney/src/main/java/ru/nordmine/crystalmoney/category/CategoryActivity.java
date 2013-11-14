package ru.nordmine.crystalmoney.category;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import ru.nordmine.crystalmoney.R;

public class CategoryActivity extends Activity {

    private EditText categoryNameEditText;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryNameEditText = (EditText) findViewById(R.id.categoryNameEditText);

        Bundle bundle = getIntent().getExtras();
        String categoryName = bundle.getString("categoryName");
        categoryId = bundle.getInt("categoryId");

        categoryNameEditText.setText(categoryName);
    }

    public void onSaveButtonClick(View v) {
        Intent intent = new Intent();
        intent.putExtra("categoryName", categoryNameEditText.getText().toString());
        intent.putExtra("categoryId", categoryId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(CategoryActivity.this, CategoryListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
