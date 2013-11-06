package ru.nordmine.crystalmoney.stat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.nordmine.crystalmoney.MainActivity;
import ru.nordmine.crystalmoney.R;

public class StatActivity extends Activity {

    private ListView percentByCategoryListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        this.percentByCategoryListView = (ListView) findViewById(R.id.percentByCategoryListView);

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int parentWidth = metrics.widthPixels;
        StatByCategoriesDao dao = new StatByCategoriesDao(this);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);

        List<StatItem> items = dao.getTotalSumByCategories(c.getTimeInMillis(), null);
        linear.addView(new MyGraphView(this, items, parentWidth), 0);

        SimpleDateFormat df = new SimpleDateFormat("MMMM");
        TextView monthNameTextView = (TextView) findViewById(R.id.monthNameTextView);
        monthNameTextView.setText(df.format(new Date(c.getTimeInMillis())));

        percentByCategoryListView.setAdapter(
                new StatItemAdapter(this, android.R.layout.simple_list_item_1,
                        items.toArray(new StatItem[0])));

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    public class MyGraphView extends View {
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private List<StatItem> items;
        RectF rectf;

        public MyGraphView(Context context, List<StatItem> items, int parentWidth) {
            super(context);

            int padding = 10;
            int diameter = parentWidth / 2;
            int radius = diameter / 2;
            int center = parentWidth / 2 - padding;
            rectf = new RectF(center - radius, padding, center + radius, padding + diameter);
            this.items = items;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float temp = 0;
            for (int i = 0; i < items.size(); i++) {
                StatItem item = items.get(i);
                Log.d(this.getClass().getName(),  "value = " + item.getDegree());
                if (i == 0) {
                    paint.setColor(item.getColor());
                    canvas.drawArc(rectf, 0, (float) item.getDegree(), true, paint);
                } else {
                    temp += items.get(i - 1).getDegree();
                    // todo что будет, если категорий будет больше десяти?
                    paint.setColor(item.getColor());
                    canvas.drawArc(rectf, temp, (float) item.getDegree(), true, paint);
                }
            }
        }

    }

}
