package com.example.expensetracker.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.expensetracker.function.DateFunctions;
import com.example.expensetracker.R;
import com.example.expensetracker.data.AppDatabase;
import com.example.expensetracker.data.Record;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    // variables
    private String mParam1;
    private String mParam2;
    private int dayCount;
    private float avgExpenses;

    private Date minDate;
    private Date maxDate;
    private Date currentDate;
    private String currentDataText;

    private List<Detail> detailList = new ArrayList<>();
    private static List<Record> currentMonthDetailList = new ArrayList<>();
    private ArrayList<String> xAxisLabel = new ArrayList<>();
    private List<TypeDetail> typeDetails = new ArrayList<>();
    private static String[] TYPE_LIST = {"bread", "dairy product", "dessert", "egg", "fried food", "meat", "noodles-pasta", "rice", "seafood", "soup", "vegetable-fruit"};
    private String favoriteType = "";
    private static float totalExpenses = 0;

    // View
    private TextView dateRangeView;
    private BarChart barChart;
    private PieChart pieChart;
    private TextView totalExpensesView;
    private TextView averageExpensesView;
    private TextView favoriteTypeView;
    private TextView topTextView;

    public StatisticFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        getDetailFromDB();

        dateRangeView = view.findViewById(R.id.monthRange);
        totalExpensesView = view.findViewById(R.id.totalExpensesTextView);
        averageExpensesView = view.findViewById(R.id.average_expenses);
        favoriteTypeView = view.findViewById(R.id.favoriteTypeView);
        topTextView = view.findViewById(R.id.textPurchaseRecord);

        currentDate = DateFunctions.getCurrentDate();
        currentDataText = (String) android.text.format.DateFormat.format("MMM yyyy", currentDate);
        dayCount = Integer.parseInt((String) android.text.format.DateFormat.format("dd", currentDate));
        String topText = "Statistic (" + currentDataText + ") ";
        topTextView.setText(topText);

        for(int i = 0; i < TYPE_LIST.length; i++) {
            TypeDetail td = new TypeDetail(TYPE_LIST[i]);
            typeDetails.add(td);
        }

        // set bar chart
        barChart = view.findViewById(R.id.barChart);
        barChart.setMaxVisibleValueCount(12);
        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(700);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.getDescription().setTextColor(Color.GRAY);
        pieChart.getDescription().setTextSize(10);
        pieChart.setEntryLabelColor(Color.GRAY);

        Legend l = pieChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setTextColor(Color.GRAY);
        l.setYOffset(0f);
        return view;
    }



    private void getDetailFromDB() {
        class getDetail extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {

                currentMonthDetailList.clear();

                // data that used for pie chart
                int currentMonth = DateFunctions.getCurrentMonth();
                int currentYear = DateFunctions.getCurrentYear();
                int dayCount = DateFunctions.countMonthDay(currentMonth, currentYear);
                Date startDate = parseDate("01-"+currentMonth+"-"+currentYear+" 00:00:00");
                Date endDate = parseDate(dayCount+"-"+currentMonth+"-"+currentYear+" 23:59:59");

                currentMonthDetailList = AppDatabase.getDatabase(getContext()).recordDao().getRecordByDate(startDate, endDate);

                for(int i = 0; i < typeDetails.size(); i++) {
                    typeDetails.get(i).num = AppDatabase.getDatabase(getContext()).recordDao().getRecordTypeCountByDate(startDate, endDate, typeDetails.get(i).type);
                    Log.d("PieData", typeDetails.get(i).type + ": " + typeDetails.get(i).num );
                }

                // data that used for bar chart
                totalExpenses = AppDatabase.getDatabase(getContext()).recordDao().getTotalPrice();


                minDate = AppDatabase.getDatabase(getContext()).recordDao().getMinDate();
                maxDate = AppDatabase.getDatabase(getContext()).recordDao().getMaxDate();


                for(int i = 0; i < dayCount; i++) {
                    Date sDate = parseDate(i+1 + "-" + currentMonth + "-" + currentYear + " 00:00:00");
                    Date eDate = parseDate(i+1 + "-" + currentMonth + "-" + currentYear + " 23:59:59");
                    float cal = AppDatabase.getDatabase(getContext()).recordDao().getTotalPriceByDate(sDate, eDate);
                    xAxisLabel.add(i+"");
                    Detail detail = new Detail(i, cal);
                    Log.d("Detail", String.valueOf((detail.totalPrice)));
                    detailList.add(detail);
                    Log.d("Start Date", String.valueOf(sDate));
                    Log.d("End Date", String.valueOf(eDate));

                }

//                if(minDate != null && maxDate != null) {
//                    int startYear = parseInt((String) android.text.format.DateFormat.format("yyyy", minDate));
//                    int endYear = parseInt((String) android.text.format.DateFormat.format("yyyy", maxDate));
//                    int startMonth = parseInt((String) android.text.format.DateFormat.format("MM", minDate));
//                    int endMonth = parseInt((String) android.text.format.DateFormat.format("MM", maxDate));
//
//                    monthCount = DateFunctions.countMonth(startYear, startMonth, endYear, endMonth);
//
//                    Calendar c = Calendar.getInstance();
//                    int cMonth = startMonth;
//                    int cYear = startYear;
//
//                    for(int i = 0; i < monthCount; i++) {
//                        Date sDate = parseDate("01-" + cMonth + "-" + cYear + " 00:00:00");
//                        c.setTime(sDate);
//                        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
//                        String eDateString = (String) android.text.format.DateFormat.format("dd-MM-yyyy 23:59:59", c.getTime());
//                        Date eDate = parseDate(eDateString);
//
//                        float price = AppDatabase.getDatabase(getContext()).recordDao().getTotalPriceByDate(sDate, eDate);
//                        String month = (String) android.text.format.DateFormat.format("MMM", sDate);
//                        String year = (String) android.text.format.DateFormat.format("yyyy", sDate);
//                        xAxisLabel.add(month.toUpperCase() + " " + year);
//                        Detail detail = new Detail(year, month, price);
//                        Log.d("Detail", String.valueOf((detail.totalPrice)));
//                        detailList.add(detail);
//                        Log.d("Start Date", String.valueOf(sDate));
//                        Log.d("End Date", String.valueOf(eDate));
//
//                        if(cMonth == 12) {
//                            cMonth = 1;
//                            cYear++;
//                        } else {
//                            cMonth++;
//                        }
//                    }
//                }

                favoriteType = AppDatabase.getDatabase(getContext()).recordDao().getFavoriteType();
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String totalExpensesText =  totalExpenses + "kcal";
                totalExpensesView.setText(totalExpensesText);
                float avgExpenses = totalExpenses/dayCount;
                String avgExpensesText = avgExpenses + "kcal";
                averageExpensesView.setText(avgExpensesText);
                if(minDate != null && maxDate != null) {
                    setDateRangeView(dateRangeView, minDate, maxDate);
                    setBarData(barChart);
                }
                setPieChartData(pieChart);
                String favoriteText = favoriteType.substring(0,1).toUpperCase() + favoriteType.substring(1);
                favoriteTypeView.setText(favoriteText);
            }
        }

        new getDetail().execute();
    }

    @SuppressLint("SimpleDateFormat")
    private static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private void setDateRangeView(TextView textView, Date startDate, Date endDate) {

        int startYear = parseInt((String) android.text.format.DateFormat.format("yyyy", startDate));
        int endYear = parseInt((String) android.text.format.DateFormat.format("yyyy", endDate));
        String startMonth = (String) android.text.format.DateFormat.format("MMM", startDate);
        String endMonth = (String) android.text.format.DateFormat.format("MMM", endDate);
        String viewText = startMonth + " " + startYear + " - " + endMonth + " " + endYear;
        //textView.setText(viewText);
    }

    private void setPieChartData(PieChart chart) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < typeDetails.size() ; i++) {
            if(typeDetails.get(i).num != 0) {
                entries.add(new PieEntry((float) (typeDetails.get(i).num),
                        typeDetails.get(i).type));
            }

        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);
        data.setValueTextSize(10);

        data.setValueFormatter(new PercentFormatter(chart));
        chart.setUsePercentValues(true);
        //data.setValueTypeface(tfLight);

        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

    private void setBarData(BarChart chart) {
        ArrayList<BarEntry> values = new ArrayList<>();
        for(int i = 0; i < detailList.size(); i++) {
            values.add(new BarEntry(i, detailList.get(i).totalPrice));
        }

        BarDataSet set;
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set.setValues(values);
            set.setHighLightAlpha(0);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(values, "Data Set");
            set.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set.setDrawValues(false);
            set.setHighLightAlpha(0);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set);
            BarData data = new BarData(dataSets);
            chart.setData(data);
            chart.setVisibleXRangeMaximum(10);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            chart.setFitBars(true);
        }


//        ArrayList dataSets = null;
//        ArrayList xAxis = new ArrayList();
//        List<BarEntry> valueSets = new ArrayList<>();
//        for(int i = 0; i < detailList.size(); i++) {
//            xAxis.add(detailList.get(i).month);
//            valueSets.add(new BarEntry(detailList.get(i).totalPrice, i));
//        }
//        BarDataSet barDataSet = new BarDataSet(valueSets, "Spent");
//        List<BarDataSet> dataSet = new ArrayList<>();
//        dataSets = new ArrayList();
//        dataSet.add(barDataSet);
//        BarData barData = new BarData(xAxis, dataSets);
//        chart.setData(barData);
    }

    // object that record the detail of each month
    static class Detail {
        //String year;
        //String month;
        int day;
        float totalPrice;

        public Detail(int day, float totalPrice) {
            //this.year = year;
            //this.month = month;
            this.day = day;
            this.totalPrice = totalPrice;
        }
    }

    // object that record the detail of each month
    static class TypeDetail {
        String type;
        int num = 0;

        public TypeDetail(String type) {
            this.type = type;
        }
    }

}