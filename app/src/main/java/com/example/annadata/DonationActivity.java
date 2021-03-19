package com.example.annadata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DonationActivity extends AppCompatActivity {

    private FloatingActionButton btnNewDonation;
    private boolean remember_me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        //Capture remember_me value from previous intent.
        Intent intent = getIntent();
        this.remember_me = intent.getBooleanExtra("remember_me", false);

        //Initialize widgets.
        btnNewDonation = (FloatingActionButton)findViewById(R.id.btnNewDonation);

        //Set on click listeners.
        btnNewDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Launch new donation page.
                 * Output : New Donation activity launch.
                 */
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