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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonationList extends ArrayAdapter<Map<String, Object>> {
    private Activity context;
    private List<Map<String, Object>> donationList;

    public DonationList(Activity context, List<Map<String, Object>> donationList)
    {
        /*
         * Constructor for single element in assignment list
         */
        super(context, R.layout.donation_list_layout, donationList);
        this.context = context;
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        /*
         * Input : Position of HashMap in List.
         * Utility : Define behaviour of donation card.
         * Output : View
         */
        LayoutInflater layoutInflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder")
        View donationListView = layoutInflater.inflate(R.layout.donation_list_layout, null, true);
        TextView tvPostDate = donationListView.findViewById(R.id.tvPostDate);
        TextView tvPostTime = donationListView.findViewById(R.id.tvPostTime);
        Map<String, Object> map = donationList.get(position);
        String date = tvPostDate.getText().toString() + " " + map.get("donation_date");
        String time = tvPostTime.getText().toString() + " " + map.get("donation_time");
        tvPostTime.setText(time);
        tvPostDate.setText(date);
        return donationListView;
    };
}
