package com.example.expensetracker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.AppDatabase;
import com.example.expensetracker.data.Record;
import com.example.expensetracker.fragments.home.MyRecordRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MonthRecordsActivity extends AppCompatActivity {

    // views
    private RecyclerView recyclerView;
    private TextView dateView;
    private TextView numOfItems;
    private TextView totalSpend;
    private ProgressBar progressBar;
    // variables
    private long startDate;
    private long endDate;
    private String month;
    private String year;
    private int itemsNum;
    private float total;
    private float monthLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_records);

        // find views
        recyclerView = findViewById(R.id.recordRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dateView = findViewById(R.id.textDate);
        numOfItems = findViewById(R.id.textNumItem);
        totalSpend = findViewById(R.id.textSpend);
//        progressBar = findViewById(R.id.progressBar);

        // get variables from intent
        month = getIntent().getStringExtra("MONTH");
        year = getIntent().getStringExtra("YEAR");
        itemsNum = getIntent().getIntExtra("ITEM_NUM", 0);
        total = getIntent().getFloatExtra("TOTAL_PRICE", 0);
        startDate = getIntent().getLongExtra("START_DATE", 0);
        endDate = getIntent().getLongExtra("END_DATE", 0);
        monthLimit = getIntent().getFloatExtra("LIMIT", 0);

        // set text views
//        progressBar.setMax(100);
//        int currentProgress = (int)((total/monthLimit)*100);
//        progressBar.setProgress(currentProgress);
//        if((int) (total/monthLimit)*100 > 80) {
//            progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
//        } else {
//            progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
//        }

        String dateText = month + ", " + year;
        dateView.setText(dateText);
        String numText = "Num. of items: " + itemsNum;
        numOfItems.setText(numText);
        String priceText = "Total Calories: " + total + "kcal";
        totalSpend.setText(priceText);

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(startDate);
        Log.d("Start date", str);

        updateList();
    }

    private void updateList() {
        class getRecordTask extends AsyncTask<Void, Void, List<Record>> {

            @Override
            protected List<Record> doInBackground(Void... voids) {
                Date sd = new Date(startDate);
                Date ed = new Date(endDate);
                List<Record> recordList = null;
                recordList = AppDatabase
                        .getDatabase(getApplicationContext())
                        .recordDao().getRecordByDate(sd, ed);
                return recordList;
            }

            @Override
            protected void onPostExecute(List<Record> records) {
                super.onPostExecute(records);
                if(records != null) {
                    recyclerView.setAdapter(new MyRecordRecyclerViewAdapter(records));
                }
            }
        }
        new getRecordTask().execute();
    }

}