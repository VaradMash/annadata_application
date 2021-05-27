package com.example.annadata;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class UpdateProfile extends AppCompatActivity {

    TextView tvUpdateEmail;
    EditText etUpdateContactNumber, etUpdateUsername;
    DocumentReference userDocument;
    Button btnUpdateDetails;
    ProgressBar pbUpdateProfile;
    boolean remember_me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        //Deactivate night mode.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Get "remember_me" value.
        remember_me = getIntent().getBooleanExtra("remember_me", true);
        //Get current user UID.
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Initialize widgets.
        tvUpdateEmail = (TextView)findViewById(R.id.tvUpdateEmail);
        etUpdateUsername = (EditText)findViewById(R.id.etUpdateUsername);
        etUpdateContactNumber = (EditText)findViewById(R.id.etUpdateContactNumber);
        userDocument = FirebaseFirestore.getInstance().collection("users").document(uid);
        btnUpdateDetails = (Button)findViewById(R.id.btnUpdateDetails);
        pbUpdateProfile = (ProgressBar)findViewById(R.id.pbUpdateProfile);
        //Set on click listener update profile.
        btnUpdateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUpdateUsername.getText().toString();
                String contact_number = etUpdateContactNumber.getText().toString();
                String email = tvUpdateEmail.getText().toString();
                if (username.isEmpty() || contact_number.isEmpty())
                {
                    if (username.isEmpty())
                    {
                        etUpdateUsername.setError("Username cannot be empty !");
                        etUpdateUsername.requestFocus();
                    }
                    if (contact_number.isEmpty())
                    {
                        etUpdateContactNumber.setError("Contact Number cannot be empty !");
                        etUpdateContactNumber.requestFocus();
                    }
                }
                else
                {
                    pbUpdateProfile.setVisibility(View.VISIBLE);
                    userDocument.update("username", username);
                    userDocument.update("contact_number", contact_number);
                    Toast.makeText(getApplicationContext(), "User details updated !", Toast.LENGTH_SHORT).show();
                    pbUpdateProfile.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        userDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                /*
                 * Input : None
                 * Utility : Set value fields to respective values.
                 * Output : None.
                 */
                tvUpdateEmail.setText(value.getString("email"));
                etUpdateContactNumber.setText(value.getString("contact_number"));
                etUpdateUsername.setText(value.getString("username"));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
         * Input : None
         * Utility : Navigate back to user profile.
         * Output : Launch user profile activity.
         */
        //Initialize intent
        Intent intent = new Intent(getApplicationContext(), UserProfile.class);
        intent.putExtra("remember_me", remember_me);
        startActivity(intent);
        //Destroy current activity
        UpdateProfile.this.finish();
    }
}