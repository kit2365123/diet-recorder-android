package com.example.expensetracker.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.expensetracker.R;

public class CalculatorActivity extends AppCompatActivity {

    // View
    EditText ageView, heightView, weightView;
    TextView textView;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Spinner spinner;
    Button button;

    int spinnerOption = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        ageView = findViewById(R.id.age_view);
        heightView = findViewById(R.id.height_view);
        weightView = findViewById(R.id.weight_view);
        radioGroup = findViewById(R.id.gender_group);
        spinner = findViewById(R.id.active_level);
        button = findViewById(R.id.calculate_button);
        textView = findViewById(R.id.result_view);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.active_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(spnOnItemSelected);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int age = Integer.parseInt(String.valueOf(ageView.getText()));
                float height = Float.parseFloat(String.valueOf(heightView.getText()));
                float weight = Float.parseFloat(String.valueOf(weightView.getText()));
                float activityLv = (float) 1.2;
                switch (spinnerOption) {
                    case 0:
                        activityLv = (float) 1.2;
                        break;
                    case 1:
                        activityLv = (float) 1.375;
                        break;
                    case 2:
                        activityLv = (float) 1.55;
                        break;
                    case 3:
                        activityLv = (float) 1.725;
                        break;
                    case 4:
                        activityLv = (float) 1.9;
                        break;
                }
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(selectedId);

                float gender;
                if ("Male".equals(String.valueOf(radioButton.getText()))) {
                    gender = 5;
                } else {
                    gender = -161;
                }

                float bmr = (float) ((10*weight) + (6.25*height) - (5*age) + gender);
                float tdee = bmr * activityLv;
                textView.setText(tdee+"");
            }
        });

    }

    private final AdapterView.OnItemSelectedListener spnOnItemSelected = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            spinnerOption = pos;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

}