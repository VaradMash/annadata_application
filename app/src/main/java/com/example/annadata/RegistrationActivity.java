  package com.example.annadata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


  public class RegistrationActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText etSignUpUsername, etSignUpEmail, etSignUpPassword, etContactNumber, etConfirmPassword;
    ProgressBar SignUpProgressBar;
    Button btnRegister;
    TextView tvSignIn;
    CollectionReference userCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Initialize Firebase authorization service,
        mAuth = FirebaseAuth.getInstance();

        //Initialize Firebase database reference to update value.
        etSignUpUsername = (EditText)findViewById(R.id.etSignUpUsername);
        etSignUpEmail = (EditText)findViewById(R.id.etSignUpEmail);
        etSignUpPassword = (EditText)findViewById(R.id.etSignUpPassword);
        etContactNumber = (EditText)findViewById(R.id.etContactNumber);
        etConfirmPassword = (EditText)findViewById(R.id.etConfirmPassword);
        SignUpProgressBar = (ProgressBar)findViewById(R.id.SignUpProgressBar);
        SignUpProgressBar.setVisibility(View.GONE);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                 * Input : EditText data.
                 * Utility : Sign Up user to Firebase.
                 * Output : Launch User Profile Activity.
                 */
                //Capture all variables.
                String username = etSignUpUsername.getText().toString();
                String email = etSignUpEmail.getText().toString();
                String password = etSignUpPassword.getText().toString();
                String contact_number = etContactNumber.getText().toString();
                String confirm_password = etConfirmPassword.getText().toString();
                //If any variable is empty, display respective error message.
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || contact_number.isEmpty() || confirm_password.isEmpty())
                {
                    if (username.isEmpty())
                    {
                        etSignUpUsername.setError("Username cannot be empty !");
                        etSignUpUsername.requestFocus();
                    }
                    if (email.isEmpty())
                    {
                        etSignUpEmail.setError("Email cannot be empty !");
                        etSignUpEmail.requestFocus();
                    }
                    if (password.isEmpty())
                    {
                        etSignUpPassword.setError("Password cannot be empty !");
                        etSignUpPassword.requestFocus();
                    }
                    if (contact_number.isEmpty())
                    {
                        etContactNumber.setError("Contact Number cannot be empty !");
                        etContactNumber.requestFocus();
                    }
                    if (confirm_password.isEmpty())
                    {
                        etConfirmPassword.setError("Password cannot be empty !");
                        etConfirmPassword.requestFocus();
                    }
                }
                //validate email address.
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    etSignUpEmail.setError("Enter valid EMail Address");
                    etSignUpEmail.requestFocus();
                }
                //Confirm password and password do not match.
                else if (!password.equals(confirm_password))
                {
                    etSignUpPassword.setError("Passwords do no match !");
                    etConfirmPassword.setError("Passwords do no match !");
                    etSignUpPassword.requestFocus();
                    etConfirmPassword.requestFocus();
                }
                else if (password.length() < 6)
                {
                    etSignUpPassword.setError("Password too short !");
                    etConfirmPassword.setError("Password too short !");
                    etSignUpPassword.requestFocus();
                    etConfirmPassword.requestFocus();
                }
                else
                {
                    SignUpProgressBar.setVisibility(View.VISIBLE);
                    userCollection = FirebaseFirestore.getInstance().collection("users");
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest updateRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username)
                                        .build();
                                user.updateProfile(updateRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful())
                                        {
                                            Toast.makeText(RegistrationActivity.this, "Username update failed !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                //Create Hash Map for user for post request.
                                Map<String, Object> current_user = new HashMap<>();
                                current_user.put("username", username);
                                current_user.put("contact_number", contact_number);
                                current_user.put("email", email);
                                current_user.put("successful_orders", 0);
                                //Create document in user collection.
                                userCollection.document(user.getUid()).set(current_user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    //Navigate to user profile.
                                                    Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                                                    intent.putExtra("remember_me", true);
                                                    startActivity(intent);
                                                    RegistrationActivity.this.finish();
                                                }
                                                else
                                                {
                                                    //Display error message
                                                    Toast.makeText(getApplicationContext(), "An error occurred !", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                            else {
                                Toast.makeText(RegistrationActivity.this, "An error occurred !", Toast.LENGTH_SHORT).show();
                            }
                            SignUpProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Switch to Sign In activity.
                 * Output: Close current instance, clear all fields and redirect to sign in activity.
                 */
                //Clear field contents.
                etSignUpUsername.setText("");
                etSignUpEmail.setText("");
                etSignUpPassword.setText("");
                etContactNumber.setText("");
                //Initialize and launch intent.
                Intent intent = new Intent(RegistrationActivity.this, com.example.annadata.LoginActivity.class);
                startActivity(intent);
                RegistrationActivity.this.finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Sign out of all accounts.
        mAuth.signOut();
    }

    @Override
    public void onBackPressed() {
        /*
         * Input : None
         * Utility: On pressing back button, launch main activity and clear field contents.
         * Output : Launch main activity.
         */
        AlertDialog.Builder alert_dialog = new AlertDialog.Builder(RegistrationActivity.this);
        View dialog_view = getLayoutInflater().inflate(R.layout.exit_dialog, null);
        Button btnExit = (Button)dialog_view.findViewById(R.id.btnExit);
        Button btnCancel = (Button)dialog_view.findViewById(R.id.btnCancel);

        alert_dialog.setView(dialog_view);
        AlertDialog alertDialog = alert_dialog.create();
        alert_dialog.setCancelable(false);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Clear text fields.
                etSignUpUsername.setText("");
                etSignUpEmail.setText("");
                etSignUpPassword.setText("");
                etContactNumber.setText("");
                etConfirmPassword.setText("");
                //Intent destruction.
                RegistrationActivity.this.finish();
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