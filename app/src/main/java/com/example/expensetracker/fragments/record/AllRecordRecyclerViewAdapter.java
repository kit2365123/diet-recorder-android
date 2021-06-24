package com.example.expensetracker.fragments.record;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.expensetracker.activity.MonthRecordsActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.fragments.record.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AllRecordRecyclerViewAdapter extends RecyclerView.Adapter<AllRecordRecyclerViewAdapter.ViewHolder> {

    private final List<MonthlyRecordDetail> mValues;

    public AllRecordRecyclerViewAdapter(List<MonthlyRecordDetail> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MonthlyRecordDetail item = mValues.get(position);
        holder.mItem = mValues.get(position);
        holder.mMonthView.setText(item.month);
        holder.mYearView.setText(item.year);
        holder.mNumItem.setText("Number of items: " + item.itemNum);
        holder.mSpent.setText("Total Calories: " + item.totalPrice + "kcal");

        holder.mView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MonthRecordsActivity.class);
                intent.putExtra("START_DATE", item.startDate.getTime());
                intent.putExtra("END_DATE", item.endDate.getTime());
                intent.putExtra("TOTAL_PRICE", item.totalPrice);
                intent.putExtra("ITEM_NUM", item.itemNum);
                intent.putExtra("MONTH", item.month);
                intent.putExtra("YEAR", item.year);
                intent.putExtra("LIMIT", item.monthLimit);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mMonthView;
        public final TextView mYearView;
        public final TextView mNumItem;
        public final TextView mSpent;
        public MonthlyRecordDetail mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMonthView = (TextView) view.findViewById(R.id.record_month);
            mYearView = (TextView) view.findViewById(R.id.record_year);
            mNumItem = view.findViewById(R.id.item_count);
            mSpent = view.findViewById(R.id.total_spent);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMonthView.getText() + "'";
        }
    }
}