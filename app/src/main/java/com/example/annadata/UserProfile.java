package com.example.annadata;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;

public class UserProfile extends AppCompatActivity {

    boolean remember_me;
    Button btnSignOut, btnUpdateProfile, btnRequest, btnDonate;
    ProgressBar signOutProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Capture remember me value from previous intent.
        Intent intent = getIntent();
        remember_me = intent.getBooleanExtra("remember_me", false);

        //Toast.makeText(UserProfile.this, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();

        //Initialize buttons.
        btnSignOut = (Button)findViewById(R.id.btnSignOut);
        btnUpdateProfile = (Button)findViewById(R.id.btnUpdateProfile);
        btnRequest = (Button)findViewById(R.id.btnRequest);
        btnDonate = (Button)findViewById(R.id.btnDonate);
        signOutProgressBar = (ProgressBar)findViewById(R.id.signOutProgressBar);


        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Sign Out current user and navigate to login activity.
                 * Output : Launch Login Activity.
                 */
                signOutProgressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                signOutProgressBar.setVisibility(View.GONE);
                //Destroy current activity.
                UserProfile.this.finish();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Launch Update profile activity.
                 * Output : None
                 */
                //Initialize Intent
                Intent intent = new Intent(getApplicationContext(), UpdateProfile.class);
                startActivity(intent);
                //Destroy current profile.
                UserProfile.this.finish();
            }
        });

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Launch donations page.
                 * Output : Donation Activity launch.
                 */
                //Initialize intent.
                Intent intent = new Intent(getApplicationContext(), DonationActivity.class);
                intent.putExtra("remember_me", remember_me);
                startActivity(intent);
                //Destroy current intent.
                UserProfile.this.finish();
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Launch request page.
                 * Output : Request Activity launch.
                 */
                //Initialize intent.
                Intent intent = new Intent(getApplicationContext(), RequestActivity.class);
                intent.putExtra("remember_me", remember_me);
                startActivity(intent);
                //Destroy current activity
                UserProfile.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        /*
         * Input : None
         * Utility: On pressing back button, sign out user if requested.
         * Output : Launch relevant activity.
         */
        AlertDialog.Builder alert_dialog = new AlertDialog.Builder(UserProfile.this);
        View dialog_view = getLayoutInflater().inflate(R.layout.exit_dialog, null);
        Button btnExit = (Button)dialog_view.findViewById(R.id.btnExit);
        Button btnCancel = (Button)dialog_view.findViewById(R.id.btnCancel);

        alert_dialog.setView(dialog_view);
        AlertDialog alertDialog = alert_dialog.create();
        alert_dialog.setCancelable(false);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (!remember_me)
                {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    UserProfile.this.finish();
                }
                else
                {
                    UserProfile.this.finish();
                    return;
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}