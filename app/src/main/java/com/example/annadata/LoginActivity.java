package com.example.annadata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
{
    //Initialize Firebase authentication service.
    FirebaseAuth mAuth;
    EditText etSignInEmail, etSignInPassword;
    ProgressBar progressBar;
    Button btnAuthenticate;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch remember_me_switch;
    TextView tvSignUp;
    boolean remember_me = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mAuth = FirebaseAuth.getInstance();

        etSignInEmail = (EditText)findViewById(R.id.etSignInEmail);
        etSignInPassword = (EditText)findViewById(R.id.etSignInPassword);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        remember_me_switch = (Switch)findViewById(R.id.remember_me_switch);
        btnAuthenticate = findViewById(R.id.btnAuthenticate);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : Username and Password.
                 * Utility : If all the requirements are satisfied, attempt Log In.
                 * Output : Reroute to further activity on successful transaction, else display error message.
                 */
                //If Fields are empty, display error message.
                if (etSignInEmail.getText().toString().isEmpty() || etSignInPassword.getText().toString().isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Please enter All Fields !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    //Call Firebase authentication request. Post username and password.
                    mAuth.signInWithEmailAndPassword(etSignInEmail.getText().toString(), etSignInPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    progressBar.setVisibility(View.GONE);
                                    //If authentication was successful, proceed to user profile.
                                    if (task.isSuccessful())
                                    {
                                        //Proceed to User Profile.
                                        Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                                        intent.putExtra("remember_me", remember_me);
                                        startActivity(intent);
                                        //Terminate current activity.
                                        LoginActivity.this.finish();

                                    }
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this, "Invalid Credentials !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Launch Sign Up activity.
                 * Output : Clear all fields and switch to sign Up Activity.
                 */
                Intent intent = new Intent(LoginActivity.this, com.example.annadata.RegistrationActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        remember_me_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /*
                 * Input : Event of state change of switch.
                 * utility : Alter value of remember me field.
                 * Output : None.
                 */
                if (isChecked)
                {
                    remember_me = true;
                }
                else
                {
                    remember_me = false;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        /*
         * Input : None
         * Utility: On pressing back button, launch main activity and clear field contents.
         * Output : Launch main activity.
         */
        AlertDialog.Builder alert_dialog = new AlertDialog.Builder(LoginActivity.this);
        View dialog_view = getLayoutInflater().inflate(R.layout.exit_dialog, null);
        Button btnExit = (Button)dialog_view.findViewById(R.id.btnExit);
        Button btnCancel = (Button)dialog_view.findViewById(R.id.btnCancel);

        alert_dialog.setView(dialog_view);
        AlertDialog alertDialog = alert_dialog.create();
        alert_dialog.setCancelable(false);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Clear text field.
                etSignInEmail.setText("");
                etSignInPassword.setText("");
                //Intent launch.
                LoginActivity.this.finish();
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


    @Override
    protected void onStart() {
        super.onStart();
        //If Current user is signed in, navigate to user profile.
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            Intent intent = new Intent(getApplicationContext(), UserProfile.class);
            intent.putExtra("remember_me", true);
            startActivity(intent);
            //Destroy login activity.
            LoginActivity.this.finish();
        }
    }
}