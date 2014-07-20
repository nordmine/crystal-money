package ru.nordmine.crystalmoney.stat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ru.nordmine.crystalmoney.MainActivity;
import ru.nordmine.crystalmoney.R;

public class StatActivity extends Activity {

    private ListView percentByCategoryListView;
    private LinearLayout linear;
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        linear = (LinearLayout) findViewById(R.id.linear);
        this.percentByCategoryListView = (ListView) findViewById(R.id.percentByCategoryListView);

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        drawChart();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void drawChart() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int parentWidth = metrics.widthPixels - 50;
        linear.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, parentWidth / 3 * 2 + 30));
        StatByCategoriesDao dao = new StatByCategoriesDao(this);

        long fromDate = calendar.getTimeInMillis();

        Calendar toCalendar = (Calendar) calendar.clone();
        toCalendar.set(Calendar.MONTH, toCalendar.get(Calendar.MONTH) + 1);
        long toDate = toCalendar.getTimeInMillis();

        linear.removeAllViews();

        List<StatItem> items = dao.getTotalSumByCategories(fromDate, toDate);
        if (items.isEmpty()) {
            TextView noItemsTextView = new TextView(this);
            noItemsTextView.setText(R.string.caption_empty_month_stat);
            noItemsTextView.setGravity(Gravity.CENTER);
            linear.addView(noItemsTextView);
        } else {
            linear.addView(new MyGraphView(this, items, parentWidth), 0);
        }

        Resources res = getResources();
        String[] monthNames = res.getStringArray(R.array.month_names);
        DateFormatSymbols russianSymbols = new DateFormatSymbols();
        russianSymbols.setMonths(monthNames);

        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy", russianSymbols);
        TextView monthNameTextView = (TextView) findViewById(R.id.monthNameTextView);
        monthNameTextView.setText(df.format(new Date(calendar.getTimeInMillis())));

	    BigDecimal totalSum = BigDecimal.ZERO;
	    for (StatItem item : items) {
			totalSum = totalSum.add(item.getSum());
	    }

	    List<StatItem> itemsWithTotal = new LinkedList<StatItem>(items);
	    if(!items.isEmpty()) {
		    StatItem totalSumStatItem = new StatItem();
		    totalSumStatItem.setColor(0xFFFFFF);
		    totalSumStatItem.setSum(totalSum);
		    totalSumStatItem.setPercent(new BigDecimal("100"));
		    totalSumStatItem.setCategoryName("Итого");
		    itemsWithTotal.add(totalSumStatItem);
	    }
        percentByCategoryListView.setAdapter(
                new StatItemAdapter(this, android.R.layout.simple_list_item_1,
		                itemsWithTotal.toArray(new StatItem[itemsWithTotal.size()])));
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

    public void onPrevButtonClick(View view) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        drawChart();
    }

    public void onNextButtonClick(View view) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        drawChart();
    }

    public class MyGraphView extends View {
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private List<StatItem> items;
        RectF rectf;

        public MyGraphView(Context context, List<StatItem> items, int parentWidth) {
            super(context);

            int padding = 10;
            int diameter = parentWidth / 3 * 2;
            int radius = diameter / 2;
            int center = parentWidth / 2 - padding;
            rectf = new RectF(center - radius, padding, center + radius, padding + diameter);
            this.items = items;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // начинаем рисовать диаграмму с верхней точки круга (с 12-ти часов)
            float startAngle = -90;
            for (int i = 0; i < items.size(); i++) {
                StatItem item = items.get(i);
                Log.d(this.getClass().getName(),  "value = " + item.getDegree().toPlainString());
                if (i == 0) {
                    paint.setColor(item.getColor());
                    canvas.drawArc(rectf, startAngle, item.getDegree().floatValue(), true, paint);
                } else {
                    startAngle += items.get(i - 1).getDegree().floatValue();
                    paint.setColor(item.getColor());
                    canvas.drawArc(rectf, startAngle, item.getDegree().floatValue(), true, paint);
                }
            }
        }

    }

}
