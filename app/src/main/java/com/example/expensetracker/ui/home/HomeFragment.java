package com.example.expensetracker.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.PrimaryKey;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.AddRecordActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.data.AppDatabase;
import com.example.expensetracker.data.Record;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A fragment representing a list of Items.
 */
public class HomeFragment extends Fragment {



    // for permission requests
    public static final int REQUEST_PERMISSION = 300;

    // request code for permission requests to the os for image
    public static final int REQUEST_IMAGE = 100;

    public static Date currentDate;

    private static float totalPrice;

    private static int itemCount;

    // hold uri of image obtained from camera
    private Uri imageUri;

    private Bitmap croppedImage;

    ///private List<Record> recordList;
    //private MyRecordRecyclerViewAdapter recordAdapter;
    private RecyclerView recordRecyclerView;
    private TextView dateView;
    private TextView recordCountView;
    private TextView priceView;
    private ProgressBar progressBar;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HomeFragment newInstance(int columnCount) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // request camera permission
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }
        // request write to storage permission
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
        // request write to storage permission
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        // get the current time
        currentDate = Calendar.getInstance().getTime();
        Log.d("Time", String.valueOf(currentDate));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_item_list, container, false);

        // set the text view which display the current month
        dateView = view.findViewById(R.id.textDate);
        String currentMonth = (String) android.text.format.DateFormat.format("MMM", currentDate);
        String displayText = "Start from " + currentMonth + ". 1";
        dateView.setText(displayText);

        priceView = view.findViewById(R.id.textSpend);
        progressBar = view.findViewById(R.id.progressBar);

        //TextView textView = view.findViewById(R.id.textPurchaseRecord);
        recordCountView = view.findViewById(R.id.textNumItem);

        // set the list adapter
        recordRecyclerView = view.findViewById(R.id.recordRecyclerView);
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateData(recordRecyclerView);

        // set the add record button
        ImageView addRecordBtn = view.findViewById(R.id.add_button);
        addRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // refresh the list
        updateData(recordRecyclerView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Request Code", String.valueOf(requestCode));
        if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            try {
                // need to crop the image to fulfil the resolution requirement of the TF lite model
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(4,4).start(getContext(), this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Intent i = new Intent(getContext(), AddRecordActivity.class);
                i.putExtra("image_uri", resultUri);
                startActivity(i);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }

        }
    }

    // checks that the user has allowed all the required permission of read and write and camera. If not, notify the user and close the application
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast toast = Toast.makeText(getContext(),"This application needs read, write, and camera permissions to run. Application now closing.",Toast.LENGTH_LONG);
                toast.show();
                //System.exit(0);
            }
        }
    }

    private void updateData(final RecyclerView recyclerView) {
        class getRecordTask extends AsyncTask<Void, Void, List<Record>> {

            @SuppressLint({"SetTextI18n", "WrongThread"})
            @Override
            protected List<Record> doInBackground(Void... voids) {
                totalPrice = 0;
                itemCount = 0;
                List<Record> recordList = AppDatabase
                        .getDatabase(getContext())
                        .recordDao().getAllRecords();
                // only get the current month records
                List<Record> currentMonthRecord = new ArrayList<>();
                for (Record record : recordList) {
                    Log.d("Date", String.valueOf(record.getCreateTime().getTime()));
                    if(android.text.format.DateFormat.format("MM/yyyy", currentDate).equals(android.text.format.DateFormat.format("MM/yyyy", record.getCreateTime()))) {
                        currentMonthRecord.add(record);
                        totalPrice += record.getPrice();
                        itemCount++;
                    }
                }
                priceView.setText("Total Spend: $" + totalPrice + "/10000");
                recordCountView.setText("Num. of Items: " + itemCount);
                progressBar.setMax(100);
                int currentProgress = (int)((totalPrice/10000)*100);
                progressBar.setProgress(currentProgress);
                if((int) (totalPrice/10000)*100 > 80) {
                    progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                } else {
                    progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                }

                return currentMonthRecord;
            }

            @Override
            protected void onPostExecute(List<Record> records) {
                super.onPostExecute(records);
                recyclerView.setAdapter(new MyRecordRecyclerViewAdapter(records));
            }
        }
        new getRecordTask().execute();
    }

}