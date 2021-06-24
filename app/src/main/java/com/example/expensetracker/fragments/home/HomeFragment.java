package com.example.expensetracker.fragments.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.expensetracker.function.DateFunctions;
import com.example.expensetracker.activity.AddRecordActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.data.AppDatabase;
import com.example.expensetracker.data.Record;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 */
public class HomeFragment extends Fragment {

    // for permission requests
    public static final int REQUEST_PERMISSION = 300;
    public static final int REQUEST_SELECT_IMAGE = 100;
    public static final int REQUEST_TAKE_PICTURE = 200;
    public static final String CHANNEL_ID = "1000";
    // request code for permission requests to the os for image
    public static final int REQUEST_IMAGE = 100;
    public static Date currentDate;
    private static float totalPrice;
    private static int itemCount;
    private static float limitAmount;
    private static float limit;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_item_list, container, false);
        createNotificationChannel();

        // set the text view which display the current month
        dateView = view.findViewById(R.id.textDate);
        String currentMonth = (String) android.text.format.DateFormat.format("MMM", currentDate);
        //String displayText = "Start from " + currentMonth + ". 1";
        //dateView.setText(displayText);

        priceView = view.findViewById(R.id.textSpend);
        progressBar = view.findViewById(R.id.progressBar);

        //TextView textView = view.findViewById(R.id.textPurchaseRecord);
        recordCountView = view.findViewById(R.id.textNumItem);

        // set the list adapter
        recordRecyclerView = view.findViewById(R.id.recordRecyclerView);
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateData(recordRecyclerView);

        // set the add record button
        ImageView cameraBtn = view.findViewById(R.id.camera_button);
        ImageView imageBtn = view.findViewById(R.id.image_button);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                startActivityForResult(intent, REQUEST_TAKE_PICTURE);
            }
        });
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , REQUEST_SELECT_IMAGE);

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
        if(requestCode == REQUEST_TAKE_PICTURE && resultCode == RESULT_OK) {
            try {
                Intent i = new Intent(getContext(), AddRecordActivity.class);
                i.putExtra("image_uri", imageUri);
                //i.putExtra("image_uri", resultUri);
                startActivity(i);
//                CropImage.activity(imageUri)
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .setAspectRatio(4,4).start(getContext(), this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK) {
            try {
                Uri resultUri = data.getData();
                Intent i = new Intent(getContext(), AddRecordActivity.class);
                i.putExtra("image_uri", resultUri);
                startActivity(i);
//                CropImage.activity(imageUri)
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .setAspectRatio(4,4).start(getContext(), this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // checks that the user has allowed all the required permission of read and write and camera. If not, notify the user and close the application
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //Toast toast = Toast.makeText(getContext(),"This application needs read, write, and camera permissions to run. Application now closing.",Toast.LENGTH_LONG);
                //toast.show();
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

                // start and end date
                Date[] startEndDate = DateFunctions.getCurrentStartEndDate();
//                String startDate = (String) android.text.format.DateFormat.format("01/MM/yyyy", currentDate);
//                Calendar c = Calendar.getInstance();
//                c.setTime(currentDate);
//                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
//                String endDate = (String) android.text.format.DateFormat.format("dd/MM/yyyy", c.getTime());

                // get number of items
                itemCount = AppDatabase.getDatabase(getContext()).recordDao().getRecordCountByDate(startEndDate[0], startEndDate[1]);
                // get all records
                List<Record> recordList = null;
                recordList = AppDatabase
                        .getDatabase(getContext())
                        .recordDao().getRecordByDate(startEndDate[0], startEndDate[1]);
                for(Record record : recordList) {
                    Log.d("Id", record.getId()+ "");
                }

                String currentDate = (String) android.text.format.DateFormat.format("dd-MM-yyyy", DateFunctions.getCurrentDate());
                dateView.setText("Date: " + currentDate);

                // get total price of all items
                totalPrice = AppDatabase.getDatabase(getContext()).recordDao().getTotalPriceByDate(startEndDate[0], startEndDate[1]);

                // get the monthly limit
                Date currentTime = Calendar.getInstance().getTime();
                String currentMonthYear = (String) android.text.format.DateFormat.format("MM-yyyy", currentTime);
                //limitAmount = AppDatabase.getDatabase(getContext()).monthLimitDao().getLimitByDate(currentMonthYear);

                // set view
                limit = getContext().getSharedPreferences("Data", MODE_PRIVATE).getFloat("LIMIT", 0);

                priceView.setText("Total Calories: " + totalPrice + "kcal" + "/" + limit +"kcal");
                recordCountView.setText("Num. of Items: " + itemCount);
                progressBar.setMax(100);
                int currentProgress = (int)((totalPrice/limit)*100);
                progressBar.setProgress(currentProgress);
                if((int) (totalPrice/limit)*100 > 80) {
                    progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                } else {
                    progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                }

                return recordList;
            }

            @Override
            protected void onPostExecute(List<Record> records) {
                super.onPostExecute(records);
                if(records != null) {
                    recyclerView.setAdapter(new MyRecordRecyclerViewAdapter(records));
                    notification();
                }
            }
        }
        new getRecordTask().execute();
    }



    private void notification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_fastfood_24)
                .setContentTitle("Total Calories: ")
                .setContentText(totalPrice + "kcal" + " / " + limit +"kcal")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(100, (int)((totalPrice/limit)*100),false);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        notificationManager.notify(1000, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notification";
            String description = "notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}