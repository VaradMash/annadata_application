package com.example.annadata;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;
import java.util.Map;

public class RelevantDonationList extends ArrayAdapter<Map<String, Object>> {

    private Activity context;
    private List<Map<String, Object>> relevantDonationList;
    private boolean remember_me;
    private String donor_name, request_id;

    public RelevantDonationList(Activity context, List<Map<String, Object>> relevantDonationList, boolean remember_me, String request_id)
    {
        /*
         * Constructor for single element in donation list.
         */
        super(context, R.layout.donation_list_layout, relevantDonationList);
        this.context = context;
        this.relevantDonationList = relevantDonationList;
        this.remember_me = remember_me;
        this.request_id = request_id;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder")
        View relevantDonationListView = layoutInflater.inflate(R.layout.relevant_donation_list, null, true);
        Map<String, Object> map = relevantDonationList.get(position);
        TextView tvDonor = (TextView)relevantDonationListView.findViewById(R.id.tvDonorName);
        TextView tvDonationDate = (TextView)relevantDonationListView.findViewById(R.id.tvDonationDate);
        DocumentReference user_document = FirebaseFirestore.getInstance().collection("users").document(map.get("donor_id").toString());
        user_document.addSnapshotListener(new EventListener<DocumentSnapshot>(){
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                /*
                 * Get donor name by accessing donor document.
                 */
                donor_name = value.getString("username");
                Log.d("Donor_name", donor_name);
                tvDonor.setText(donor_name);
                tvDonationDate.setText(map.get("donation_date").toString());
            }
        });
        Button btnView = (Button)relevantDonationListView.findViewById(R.id.btnView);
        //set behaviour for viewing donation details.
        btnView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Start view donation activity.
                 * Output : Launch View donation activity.
                 */
                //Initialize intent.
                Intent intent = new Intent(context, ViewDonationDetails.class);
                intent.putExtra("remember_me", remember_me);
                intent.putExtra("donation_id", map.get("donation_id").toString());
                intent.putExtra("request_id", request_id);
                context.startActivity(intent);
                //destroy current activity.
                context.finish();
            }
        });
        return relevantDonationListView;
    }
}
