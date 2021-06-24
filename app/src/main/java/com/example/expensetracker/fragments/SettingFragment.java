package com.example.expensetracker.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.expensetracker.R;
import com.example.expensetracker.activity.CalculatorActivity;
import com.example.expensetracker.data.AppDatabase;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Integer.parseInt;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Variable
    private float inputLimit = 0;

    // View
    private EditText limitEditTextView;
    private ImageButton limitSubmitButton;
    private Button tdeeButton;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);

        limitEditTextView = view.findViewById(R.id.monthLimit);
        float limit = getContext().getSharedPreferences("Data", MODE_PRIVATE).getFloat("LIMIT", 0);
        limitEditTextView.setText(String.valueOf(limit));

        limitSubmitButton = view.findViewById(R.id.submitButton);

        limitSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!limitEditTextView.getText().toString().equals("")) {
                    SharedPreferences pref = getContext().getSharedPreferences("Data", MODE_PRIVATE);
                    pref.edit().putFloat("LIMIT", Float.parseFloat(limitEditTextView.getText().toString())).apply();
                    Toast toast = Toast.makeText(getContext(),"Saved",Toast.LENGTH_SHORT);
                    toast.show();
//                    inputLimit = Float.parseFloat(limitEditTextView.getText().toString());
//                    addLimitToDB();
                }
            }
        });

        tdeeButton = view.findViewById(R.id.calculate_button);
        tdeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CalculatorActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void addLimitToDB() {

        class saveLimitTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                Date currentTime = Calendar.getInstance().getTime();
                String currentMonthYear = (String) android.text.format.DateFormat.format("MM-yyyy", currentTime);

                Log.d("current date", currentMonthYear);
//
//                MonthLimit currentMonthLimit = new MonthLimit();
//
//                currentMonthLimit.setDate(currentMonthYear);
//                currentMonthLimit.setMonthlyLimit(inputLimit);
//
//                AppDatabase.getDatabase(getContext()).monthLimitDao().insertLimit(currentMonthLimit);
//
//                float limitAmount = AppDatabase.getDatabase(getContext()).monthLimitDao().getLimitByDate(currentMonthYear);
//                Log.d("limit count", limitAmount + "");


                return null;
            }
        }
        new saveLimitTask().execute();
    }
}