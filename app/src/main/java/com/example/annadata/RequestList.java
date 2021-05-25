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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
        View requestListView = layoutInflater.inflate(R.layout.request_list_layout, null, true);
        TextView tvPostDate = requestListView.findViewById(R.id.tvRequestPostDate);
        TextView tvPostTime = requestListView.findViewById(R.id.tvRequestPostTime);
        Button btnMarkAsComplete = requestListView.findViewById(R.id.btnMarkAsComplete);
        Button btnEdit = requestListView.findViewById(R.id.btnEdit);
        Button btnViewDonations = requestListView.findViewById(R.id.btnViewDonations);

        Map<String, Object> map = requestList.get(position);
        //Set behaviour for buttons.
        btnMarkAsComplete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : mark request as completed by setting isActive value to false
                 * Output : Reload context activity.
                 */
                AlertDialog.Builder alert_dialog = new AlertDialog.Builder(context);
                @SuppressLint("ViewHolder") View dialog_view = context.getLayoutInflater().inflate(R.layout.exit_dialog, null);
                Button btnYes = (Button)dialog_view.findViewById(R.id.btnExit);
                Button btnNo = (Button)dialog_view.findViewById(R.id.btnCancel);
                TextView tvMessage = (TextView)dialog_view.findViewById(R.id.tvExitMessage);
                tvMessage.setText("Are you sure?");

                alert_dialog.setView(dialog_view);
                AlertDialog alertDialog = alert_dialog.create();
                alert_dialog.setCancelable(true);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                         * Increment count of successful orders.
                         */
                        context.findViewById(R.id.pbRequests).setVisibility(View.VISIBLE);
                        //Get reference to order document.
                        DocumentReference donation_document = FirebaseFirestore.getInstance().collection("requests").document(String.valueOf(map.get("request_id")));
                        //Update isActive value of donation entry.
                        donation_document.update("is_active", false);
                        //Recreate context activity.
                        context.recreate();
                        alertDialog.dismiss();
                        context.findViewById(R.id.pbRequests).setVisibility(View.GONE);
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

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Launch edit donation activity for current donation field.
                 * Output : Activity Launch
                 */
                //Initialize intent.
                Intent intent = new Intent(context, EditRequest.class);
                intent.putExtra("remember_me", remember_me);
                intent.putExtra("request_id", map.get("request_id").toString());
                context.startActivity(intent);
                //Destroy current activity.
                context.finish();
            }
        });

        btnViewDonations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Navigate to View Donations activity.
                 * Output : View Donations activity launch.
                 */
                //Initialize intent.
                Intent intent = new Intent(context, ViewDonations.class);
                intent.putExtra("remember_me", remember_me);
                intent.putExtra("request_id", map.get("request_id").toString());
                context.startActivity(intent);
                //Destroy current activity.
                context.finish();
            }
        });

        //Update value of text fields.
        String date = tvPostDate.getText().toString() + " " + map.get("request_date");
        String time = tvPostTime.getText().toString() + " " + map.get("request_time");
        tvPostTime.setText(time);
        tvPostDate.setText(date);
        //Return view to parent list view.
        return requestListView;
    }
}
