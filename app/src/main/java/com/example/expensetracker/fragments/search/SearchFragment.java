package com.example.expensetracker.fragments.search;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.AppDatabase;
import com.example.expensetracker.data.Record;
import com.example.expensetracker.data.RecordDao;
import com.example.expensetracker.fragments.home.MyRecordRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class SearchFragment extends Fragment {

    // View
    private RecyclerView searchRecyclerView;
    private EditText searchBox;
    private ImageView searchButton;
    private static List<Integer> recordIdList = new ArrayList<>();
    private static List<Record> recordList = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        View view =  inflater.inflate(R.layout.fragment_search, container, false);

        searchBox = view.findViewById(R.id.search_box);
        searchButton = view.findViewById(R.id.search_button);
        searchRecyclerView = view.findViewById(R.id.search_record_recyclerview);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        searchButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(!searchBox.getText().toString().equals("")) {
                    searchRecord(searchBox.getText().toString());
                }

            }

        });

        return view;
    }

    private void searchRecord(final String searchText) {
        class getRecordTask extends AsyncTask<Void, Void, List<Record>> {

            @Override
            protected List<Record> doInBackground(Void... voids) {

                recordIdList.clear();
                recordList.clear();
                recordIdList = AppDatabase.getDatabase(getContext()).keywordDao().getRecordIdByWord(searchText);

                RecordDao recordDao = AppDatabase.getDatabase(getContext()).recordDao();
                for(Integer i : recordIdList) {
                    recordList.add(recordDao.getRecordById(i));
                    Log.d("Record Id", String.valueOf(i));
                }
                return recordList;
            }

            @Override
            protected void onPostExecute(List<Record> records) {
                super.onPostExecute(records);
                if(records != null) {
                    searchRecyclerView.setAdapter(new MyRecordRecyclerViewAdapter(records));
                }
            }
        }
        new getRecordTask().execute();
    }
}