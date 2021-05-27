package com.example.annadata;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ViewDonationDetails extends AppCompatActivity {
    private boolean remember_me;
    private String donation_id, request_id;
    //Donation card fields.
    private TextView tvDate, tvTime, tvVeg, tvNonVeg, tvContent;
    //User card fields.
    private TextView tvName, tvSuccessfulOrders;
    private Button btnCall, btnEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donation_details);

        //Get variables from Intent
        Intent intent = getIntent();
        remember_me = intent.getBooleanExtra("remember_me", remember_me);
        donation_id = intent.getStringExtra("donation_id");
        request_id = intent.getStringExtra("request_id");

        //Initialize widgets.
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvVeg = (TextView) findViewById(R.id.tvVeg);
        tvNonVeg = (TextView) findViewById(R.id.tvNonVeg);
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvName = (TextView) findViewById(R.id.tvName);
        tvSuccessfulOrders = (TextView) findViewById(R.id.tvSuccessfulOrders);
        btnCall = (Button)findViewById(R.id.btnCallUser);
        btnEmail = (Button)findViewById(R.id.btnEmailUser);

    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
         * Input : None
         * Utility : Get contents of user details and donation details and update text field values.
         * Output : None.
         */
        DocumentReference donation_document = FirebaseFirestore.getInstance().collection("donations").document(donation_id);
        donation_document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String donor_id = value.getString("donor_id");
                tvDate.setText(value.getString("donation_date"));
                tvTime.setText(value.getString("donation_time"));
                tvContent.setText(value.getString("content"));
                String update_veg = "Veg : ";
                String update_non_veg = "Non Veg : ";
                if(value.getBoolean("veg_content"))
                {
                    update_veg = update_veg + "Yes";
                }
                else {
                    update_veg = update_veg + "No";
                }
                if(value.getBoolean("non_veg_content"))
                {
                    update_non_veg = update_non_veg + "Yes";
                }
                else {
                    update_non_veg = update_non_veg + "No";
                }
                tvVeg.setText(update_veg);
                tvNonVeg.setText(update_non_veg);
                //Get Donor document and update details
                DocumentReference donor_document = FirebaseFirestore.getInstance().collection("users").document(donor_id);
                donor_document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value2, @Nullable FirebaseFirestoreException error) {
                        tvName.setText(value2.getString("username"));
                        String successful_orders = "Successful donations : " + String.valueOf(value2.getLong("successful_orders"));
                        tvSuccessfulOrders.setText(successful_orders);
                        String contact_number = value2.getString("contact_number");
                        String email = value2.getString("email");

                        btnCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*
                                 * Input : None
                                 * Utility : Call user using contact number.
                                 * Output : Launch implicit intent.
                                 */
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                String uri_phone = "tel: " + contact_number;
                                intent.setData(Uri.parse(uri_phone));
                                startActivity(intent);
                            }
                        });

                        btnEmail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*
                                 * Input : None
                                 * Utility : Call user using contact number.
                                 * Output : Launch implicit intent.
                                 */
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                String uri_email = "mailto: " + email;
                                intent.setData(Uri.parse(uri_email));
                                startActivity(intent);
                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
         * Input : None
         * Utility : Navigate back to ViewDonations activity.
         * Output : Launch ViewDonations Activity.
         */
        //Initialize intent.
        Intent intent = new Intent(getApplicationContext(), ViewDonations.class);
        intent.putExtra("remember_me", remember_me);
        intent.putExtra("request_id", request_id);
        startActivity(intent);
        //Destroy current activity.
        this.finish();
    }
}