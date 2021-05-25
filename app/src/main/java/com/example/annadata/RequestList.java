package com.example.annadata;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class RequestList extends ArrayAdapter<Map<String, Object>> {
    private Activity context;
    private List<Map<String, Object>> requestList;
    private boolean remember_me;

    public RequestList(Activity context, List<Map<String, Object>> requestList, boolean remember_me)
    {
        /*
         * Constructor for single element in donation list.
         */
        super(context, R.layout.request_list_layout, requestList);
        this.context = context;
        this.requestList = requestList;
        this.remember_me = remember_me;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder")
        View donationListView = layoutInflater.inflate(R.layout.request_list_layout, null, true);
        TextView tvPostDate = donationListView.findViewById(R.id.tvRequestPostDate);
        TextView tvPostTime = donationListView.findViewById(R.id.tvRequestPostTime);
        Map<String, Object> map = requestList.get(position);
        //Update value of text fields.
        String date = tvPostDate.getText().toString() + " " + map.get("request_date");
        String time = tvPostTime.getText().toString() + " " + map.get("request_time");
        tvPostTime.setText(time);
        tvPostDate.setText(date);
        //Return view to parent list view.
        return donationListView;
    }
}
