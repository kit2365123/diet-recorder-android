package com.example.expensetracker.fragments.home;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.Record;
import android.graphics.BitmapFactory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class MyRecordRecyclerViewAdapter extends RecyclerView.Adapter<MyRecordRecyclerViewAdapter.ViewHolder> {

    private final List<Record> mValues;

    public MyRecordRecyclerViewAdapter(List<Record> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Record item = mValues.get(position);
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(item.getItemName());
        String price = item.getPrice() + "kcal";
        holder.mPriceView.setText(price);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        holder.mDateView.setText(dateFormat.format(item.getCreateTime()));
        if (mValues.get(position).getImageByteArray() != "") {
            byte[] bis = Base64.decode(item.getImageByteArray(), Base64.DEFAULT);
            Bitmap recordImage = BitmapFactory.decodeByteArray(bis, 0, bis.length);
            holder.mImageView.setImageBitmap(recordImage);
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mNameView;
        public final TextView mPriceView;
        public final TextView mDateView;
        public Record mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.record_image);
            mNameView = (TextView) view.findViewById(R.id.record_name);
            mPriceView = (TextView) view.findViewById(R.id.record_price);
            mDateView = (TextView) view.findViewById(R.id.record_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}