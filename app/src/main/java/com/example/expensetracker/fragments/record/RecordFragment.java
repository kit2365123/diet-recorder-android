package com.example.expensetracker.fragments.record;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.expensetracker.function.DateFunctions;
import com.example.expensetracker.R;
import com.example.expensetracker.data.AppDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * A fragment representing a list of Items.
 */
public class RecordFragment extends Fragment {

    // views
    private RecyclerView allRecordRecyclerView;
    private TextView dateRangeView;
    private TextView totalRecordView;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters

    // variables
    private int mColumnCount = 1;
    private Date minDate;
    private Date maxDate;
    private int recordNum = 0;

    private List<MonthlyRecordDetail> recordDetailList = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecordFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecordFragment newInstance(int columnCount) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_fragment_item_list, container, false);

        getDataFromDB();

        allRecordRecyclerView = view.findViewById(R.id.allRecordRecyclerView);
        allRecordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //allRecordRecyclerView.setAdapter(new AllRecordRecyclerViewAdapter(DummyContent.ITEMS));

        dateRangeView = view.findViewById(R.id.dateRange);
        totalRecordView = view.findViewById(R.id.totalRecord);
        return view;
    }

    private void getDataFromDB() {
        class getRecordTask extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                minDate = AppDatabase.getDatabase(getContext()).recordDao().getMinDate();
                maxDate = AppDatabase.getDatabase(getContext()).recordDao().getMaxDate();
                recordNum = AppDatabase.getDatabase(getContext()).recordDao().getNumOfRecords();
                return "Executed";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // set text to text views
                String totalRecordText = "Total Record: " + recordNum;
                totalRecordView.setText(totalRecordText);
                if (minDate != null && maxDate != null) {
                    String startDate = (String) android.text.format.DateFormat.format("MMM yyyy", minDate);
                    String endDate = (String) android.text.format.DateFormat.format("MMM yyyy", maxDate);
                    String dateRangeText = startDate + " - " + endDate;
                    dateRangeView.setText(dateRangeText);
                    // get records based on the result
                    getRecordFromDB();
                }
            }
        }
        new getRecordTask().execute();
    }

    private void getRecordFromDB() {
        int startYear = parseInt((String) android.text.format.DateFormat.format("yyyy", minDate));
        int endYear = parseInt((String) android.text.format.DateFormat.format("yyyy", maxDate));
        int startMonth = parseInt((String) android.text.format.DateFormat.format("MM", minDate));
        int endMonth = parseInt((String) android.text.format.DateFormat.format("MM", maxDate));

        Log.d("Start end", startMonth + "/" + startYear + ", " + endMonth + "/" + endYear);

        // count how many month
        int monthCount = DateFunctions.countMonth(startYear, startMonth, endYear, endMonth);

        Log.d("Num of Month", monthCount+ "");

        Calendar c = Calendar.getInstance();
        int cMonth = startMonth;
        int cYear = startYear;

        recordDetailList.clear();

        for(int i = 0; i < monthCount; i++) {
            Date sDate = parseDate("01-" + cMonth + "-" + cYear + " 00:00:00");
            c.setTime(sDate);
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            String eDateString = (String) android.text.format.DateFormat.format("dd-MM-yyyy 23:59:59", c.getTime());
            Date eDate = parseDate(eDateString);

            MonthlyRecordDetail recordDetail = new MonthlyRecordDetail(sDate, eDate);
            recordDetailList.add(recordDetail);

            if(cMonth == 12) {
                cMonth = 1;
                cYear++;
            } else {
                cMonth++;
            }
        }

        addToList();
    }

    private void addToList() {
        class getRecordDetailTask extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                for(int i = 0; i < recordDetailList.size(); i++) {
                    MonthlyRecordDetail m = recordDetailList.get(i);
                    recordDetailList.get(i).totalPrice = AppDatabase.getDatabase(getContext()).recordDao().getTotalPriceByDate(m.startDate, m.endDate);
                    recordDetailList.get(i).itemNum = AppDatabase.getDatabase(getContext()).recordDao().getRecordCountByDate(m.startDate, m.endDate);
                    String currentMonthYear = (String) android.text.format.DateFormat.format("MM-yyyy", m.startDate);
                    //recordDetailList.get(i).monthLimit = AppDatabase.getDatabase(getContext()).monthLimitDao().getLimitByDate(currentMonthYear);
                    Log.d("Price", String.valueOf(recordDetailList.get(i).totalPrice));
                    String month = (String) android.text.format.DateFormat.format("MMM", m.startDate);
                    String year = (String) android.text.format.DateFormat.format("yyyy", m.startDate);
                    recordDetailList.get(i).month = month;
                    recordDetailList.get(i).year = year;
                }
                return "Executed";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                allRecordRecyclerView.setAdapter(new AllRecordRecyclerViewAdapter(recordDetailList));
            }
        }
        new getRecordDetailTask().execute();
    }

    private void setTextView() {

    }

    @SuppressLint("SimpleDateFormat")
    private static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

}