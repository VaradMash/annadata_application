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
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.model.Document;

import java.util.EventListenerProxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonationList extends ArrayAdapter<Map<String, Object>> {
    private Activity context;
    private List<Map<String, Object>> donationList;
    private boolean remember_me;

    public DonationList(Activity context, List<Map<String, Object>> donationList, boolean remember_me)
    {
        /*
         * Constructor for single element in donation list.
         */
        super(context, R.layout.donation_list_layout, donationList);
        this.context = context;
        this.donationList = donationList;
        this.remember_me = remember_me;
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
        Button btnEdit = donationListView.findViewById(R.id.btnEdit);
        Button btnMarkAsComplete = donationListView.findViewById(R.id.btnMarkAsComplete);
        //Get map object from list.
        Map<String, Object> map = donationList.get(position);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Launch edit donation activity for current donation field.
                 * Output : Activity Launch
                 */
                //Initialize intent.
                Intent intent = new Intent(context, EditDonation.class);
                intent.putExtra("donation_id", map.get("donation_id").toString());
                intent.putExtra("remember_me", remember_me);
                context.startActivity(intent);
                //Destroy current activity.
                context.finish();
            }
        });

        btnMarkAsComplete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : mark donation as completed by setting isActive value to false. Update value of successful orders if transaction was completed.
                 * Output : Reload context activity.
                 */
                //Change details of order and user
                AlertDialog.Builder alert_dialog = new AlertDialog.Builder(context);
                @SuppressLint("ViewHolder") View dialog_view = context.getLayoutInflater().inflate(R.layout.exit_dialog, null);
                Button btnYes = (Button)dialog_view.findViewById(R.id.btnExit);
                Button btnNo = (Button)dialog_view.findViewById(R.id.btnCancel);
                TextView tvMessage = (TextView)dialog_view.findViewById(R.id.tvExitMessage);
                tvMessage.setText("Was The Order successfully completed ?");

                alert_dialog.setView(dialog_view);
                AlertDialog alertDialog = alert_dialog.create();
                alert_dialog.setCancelable(false);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                         * Increment count of successful orders.
                         */
                        context.findViewById(R.id.pbDonations).setVisibility(View.VISIBLE);

                        DocumentReference user_document = FirebaseFirestore.getInstance().collection("users").document(String.valueOf(map.get("donor_id")));
                        //Increment successful order count.
                        user_document.update("successful_orders", FieldValue.increment(1));
                        //Get reference to order document.
                        DocumentReference donation_document = FirebaseFirestore.getInstance().collection("donations").document(String.valueOf(map.get("donation_id")));
                        //Update isActive value of donation entry.
                        donation_document.update("is_active", false);
                        //Recreate context activity.
                        context.recreate();
                        alertDialog.dismiss();
                        context.findViewById(R.id.pbDonations).setVisibility(View.GONE);
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                //Set order to inactive.

            }
        });
        //Update value of text fields.
        String date = tvPostDate.getText().toString() + " " + map.get("donation_date");
        String time = tvPostTime.getText().toString() + " " + map.get("donation_time");
        tvPostTime.setText(time);
        tvPostDate.setText(date);
        //Return view to parent list view.
        return donationListView;
    };
}
