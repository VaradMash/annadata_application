package com.example.annadata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewDonationActivity extends AppCompatActivity {

    private boolean remember_me;
    private Button btnPostDonation;
    private EditText etNumberOfPeople, etContent, etRegion;
    private ProgressBar pbNewDonation;
    private CollectionReference orderCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_donation);

        //Initialize widgets.
        btnPostDonation = (Button)findViewById(R.id.btnPostDonation);
        etNumberOfPeople = (EditText)findViewById(R.id.etNumberOfPeople);
        etContent = (EditText)findViewById(R.id.etContent);
        etRegion = (EditText)findViewById(R.id.etRegion);
        pbNewDonation = (ProgressBar)findViewById(R.id.pbNewDonation);
        //Initialize orders end point.
        orderCollection = FirebaseFirestore.getInstance().collection("orders");

        //Capture remember_me value from previous activity.
        Intent intent = getIntent();
        remember_me = intent.getBooleanExtra("remember_me", true);

        btnPostDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : Order details.
                 * Utility : Post order details on database.
                 * Output : None.
                 */
                //Acquire field values.
                String number_of_people = etNumberOfPeople.getText().toString();
                String content = etContent.getText().toString();
                String region = etRegion.getText().toString();
                //Validate entries in form.
                if (number_of_people.isEmpty() || content.isEmpty() || region.isEmpty() || number_of_people.equals("0"))
                {
                    if (number_of_people.isEmpty())
                    {
                        etNumberOfPeople.setError("Field cannot be empty !");
                        etNumberOfPeople.requestFocus();
                    }
                    if (content.isEmpty())
                    {
                        etContent.setError("Content cannot be empty !");
                        etContent.requestFocus();
                    }
                    if (region.isEmpty())
                    {
                        etRegion.setError("Region cannot be empty !");
                        etRegion.requestFocus();
                    }
                    if (number_of_people.equals("0"))
                    {
                        etNumberOfPeople.setError("Enter valid number !");
                        etNumberOfPeople.requestFocus();
                    }
                }
                else
                {
                    pbNewDonation.setVisibility(View.VISIBLE);
                    //Get User ID for foreign key in order collection.
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss", Locale.US);
                    String donation_time = sdf.format(new Date());
                    //Initialize new Map object for posting data.
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("number_of_people", Integer.parseInt(number_of_people));
                    dataMap.put("content", content);
                    dataMap.put("region", region);
                    dataMap.put("donor_id", uid);
                    orderCollection.document(donation_time + "_" + uid).set(dataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getApplicationContext(), "Donation posted !", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Error occurred !", Toast.LENGTH_SHORT).show();
                                    }
                                    pbNewDonation.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
         * Input : Back button press
         * Utility : Launch donations page.
         * Output : Donation Activity Launch.
         */
        //Initialize Intent
        Intent intent = new Intent(getApplicationContext(), DonationActivity.class);
        intent.putExtra("remember_me", remember_me);
        startActivity(intent);
        //Destroy current activity.
        NewDonationActivity.this.finish();
    }
}