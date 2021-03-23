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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_donation);

        //Initialize widgets.
        btnPostDonation = (Button)findViewById(R.id.btnPostDonation);
        etNumberOfPeople = (EditText)findViewById(R.id.etNumberOfPeople);
        etContent = (EditText)findViewById(R.id.etContent);
        etRegion = (EditText)findViewById(R.id.etRegion);
        pbNewDonation = (ProgressBar)findViewById(R.id.pbDonations);

        //Capture remember_me value from previous activity.
        Intent intent = getIntent();
        remember_me = intent.getBooleanExtra("remember_me", true);

        //Set on click listeners.
        btnPostDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Post donation on database after validations.
                 * Output : None.
                 */
                 int number_of_people = Integer.parseInt(etNumberOfPeople.getText().toString());
                 String content = etContent.getText().toString();
                 String region = etRegion.getText().toString();
                 //Validate input and variables.
                if (number_of_people == 0 || content.isEmpty() || region.isEmpty())
                {
                    if (number_of_people == 0)
                    {
                        etNumberOfPeople.setError("Enter valid number !");
                        etNumberOfPeople.requestFocus();
                    }
                    if (content.isEmpty())
                    {
                        etContent.setError("Content cannot be empty !");
                        etContent.requestFocus();
                    }
                    if (region.isEmpty())
                    {
                        etRegion.setError("Region cannot be empty");
                        etRegion.requestFocus();
                    }
                }
                else
                {
                    pbNewDonation.setVisibility(View.VISIBLE);
                    //Get User fields as foreign key for Orders.
                    String user_uid = FirebaseAuth.getInstance().getUid();
                    //Post data to FireStore.
                    Map<String, Object> map = new HashMap<>();
                    map.put("number_of_people", number_of_people);
                    map.put("region", region);
                    map.put("donor", user_uid);
                    map.put("content", content);
                    //Generate Order ID.
                    //String order_post_time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
                    /*
                    CollectionReference orderCollection = FirebaseFirestore.getInstance().collection("orders");
                    orderCollection.document(order_post_time + "_" + user_uid).set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "Donation posted !", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "An error occured !", Toast.LENGTH_SHORT).show();
                            }
                            pbNewDonation.setVisibility(View.GONE);
                        }
                    });*/
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