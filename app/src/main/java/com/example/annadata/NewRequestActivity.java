package com.example.annadata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class NewRequestActivity extends AppCompatActivity {
    private boolean remember_me;
    private Button btnPostRequest;
    private EditText etRequestNumberOfPeople, etRequestRegion;
    private ProgressBar pbNewRequest;
    private CollectionReference requestCollection;
    private Switch switchRequestVeg, switchRequestNonVeg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);

        Intent intent = getIntent();
        remember_me = intent.getBooleanExtra("remember_me", true);

        //Initialize widgets.
        btnPostRequest = (Button)findViewById(R.id.btnPostRequest);
        etRequestNumberOfPeople = (EditText)findViewById(R.id.etRequestNumberOfPeople);
        etRequestRegion = (EditText)findViewById(R.id.etRequestRegion);
        pbNewRequest = (ProgressBar)findViewById(R.id.pbNewRequest);
        switchRequestVeg = (Switch)findViewById(R.id.switchRequestVeg);
        switchRequestNonVeg = (Switch)findViewById(R.id.switchRequestNonVeg);
        //Initialize orders end point.
        requestCollection = FirebaseFirestore.getInstance().collection("requests");

        //Set behaviour for post request button.
        btnPostRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : Request details.
                 * Utility : Post request details on database.
                 * Output :None.
                 */
                //Acquire field values.
                String number_of_people = etRequestNumberOfPeople.getText().toString();
                String region = etRequestRegion.getText().toString();
                boolean veg = switchRequestVeg.isChecked();
                boolean nonVeg = switchRequestNonVeg.isChecked();
                //Validate entries in form.
                if (number_of_people.isEmpty()  || region.isEmpty() || number_of_people.equals("0") || (!veg && !nonVeg))
                {
                    if (number_of_people.isEmpty())
                    {
                        etRequestNumberOfPeople.setError("Field cannot be empty !");
                        etRequestNumberOfPeople.requestFocus();
                    }
                    if (region.isEmpty())
                    {
                        etRequestRegion.setError("Region cannot be empty !");
                        etRequestRegion.requestFocus();
                    }
                    if (number_of_people.equals("0"))
                    {
                        etRequestNumberOfPeople.setError("Enter valid number !");
                        etRequestNumberOfPeople.requestFocus();
                    }
                    if (!veg && !nonVeg)
                    {
                        Toast.makeText(getApplicationContext(), "Please select appropriate food category(veg or non veg).", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    pbNewRequest.setVisibility(View.VISIBLE);
                    //Get User ID for foreign key in order collection.
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    LocalDateTime time = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy.HH:mm:ss");
                    String request_time = dateTimeFormatter.format(time);
                    //Initialize new Map object for posting data.
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("number_of_people", Integer.parseInt(number_of_people));
                    dataMap.put("region", region);
                    dataMap.put("request_person_id", uid);
                    dataMap.put("veg_content", veg);
                    dataMap.put("non_veg_content", nonVeg);
                    dataMap.put("request_date", request_time.substring(0, 10));
                    dataMap.put("request_time", request_time.substring(11));
                    dataMap.put("is_active", true);
                    dataMap.put("request_id", request_time + "_" + uid);
                    requestCollection.document(request_time + "_" + uid).set(dataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Donation posted !", Toast.LENGTH_SHORT).show();
                                        //Initialize Intent
                                        Intent intent = new Intent(getApplicationContext(), RequestActivity.class);
                                        intent.putExtra("remember_me", remember_me);
                                        startActivity(intent);
                                        //Destroy current activity.
                                        NewRequestActivity.this.finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error occurred !", Toast.LENGTH_SHORT).show();
                                    }
                                    pbNewRequest.setVisibility(View.GONE);
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
         * Input : None
         * Utility : Launch requests activity on back button pressed.
         * Output : Launch request activity.
         */
        //Initialize intent
        Intent intent = new Intent(getApplicationContext(), RequestActivity.class);
        intent.putExtra("remember_me", remember_me);
        startActivity(intent);
        //Destroy current activity
        NewRequestActivity.this.finish();
    }
}