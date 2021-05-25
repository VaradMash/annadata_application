package com.example.annadata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DonationActivity extends AppCompatActivity {

    private FloatingActionButton btnNewDonation;
    private boolean remember_me;
    private CollectionReference donationCollection;
    private ProgressBar pbDonations;
    private ListView donationScrollView;
    private Activity context;

    public boolean getRememberMe()
    {
        /*
         * Getter method for remember_me field.
         */
        return remember_me;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        //Disable dark mode.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Capture remember_me value from previous intent.
        Intent intent = getIntent();
        this.remember_me = intent.getBooleanExtra("remember_me", false);

        //Initialize widgets.
        btnNewDonation = (FloatingActionButton)findViewById(R.id.btnNewDonation);
        pbDonations = (ProgressBar)findViewById(R.id.pbDonations);
        donationScrollView = (ListView)findViewById(R.id.donationScrollView);

        //Set on click listeners.
        btnNewDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Launch new donation page.
                 * Output : New Donation activity launch.
                 */
                //Initialize intent
                Intent intent = new Intent(getApplicationContext(), NewDonationActivity.class);
                intent.putExtra("remember_me", remember_me);
                startActivity(intent);
                //Destroy current activity.
                DonationActivity.this.finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
         * Input : None
         * Utility : Get all donations made by current user and render to activity.
         * Output : None
         */
        pbDonations.setVisibility(View.VISIBLE);
        context = this;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        donationCollection = FirebaseFirestore.getInstance().collection("donations");
        List<Map<String, Object>> donationList = new ArrayList<Map<String, Object>>();
        //Get Donations
        donationCollection
                .whereEqualTo("donor_id", uid)
                .whereEqualTo("is_active", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult())
                            {
                                donationList.add(documentSnapshot.getData());
                            }
                            if(donationList.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "No donations found !", Toast.LENGTH_SHORT).show();
                            }
                            DonationList adapter = new DonationList(context  , donationList, getRememberMe());
                            donationScrollView.setAdapter(adapter);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), " An error occurred ", Toast.LENGTH_SHORT).show();
                        }
                        pbDonations.setVisibility(View.GONE);

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
         * Input : None.
         * Utility : Launch User profile
         * Output : Launch user profile activity.
         */
        //Initialize intent.
        Intent intent = new Intent(getApplicationContext(), UserProfile.class);
        intent.putExtra("remember_me", remember_me);
        startActivity(intent);
        //Destroy current activity.
        DonationActivity.this.finish();
    }
}