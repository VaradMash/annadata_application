package com.example.annadata;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class EditRequest extends AppCompatActivity {
    private EditText etEditRequestNumberOfPeople, etEditRequestRegion;
    private Switch switchEditRequestVeg, switchEditRequestNonVeg;
    private Button btnEditRequest;
    private ProgressBar pbEditRequest;
    private boolean remember_me;
    private String request_id;
    private DocumentReference request_document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_request);
        //Initialize intent variables.
        Intent intent = getIntent();
        request_id = intent.getStringExtra("request_id");
        remember_me = intent.getBooleanExtra("remember_me", true);
        request_document = FirebaseFirestore.getInstance().collection("requests").document(request_id);

        //Initialize widgets.
        etEditRequestRegion = (EditText)findViewById(R.id.etEditRequestRegion);
        etEditRequestNumberOfPeople = (EditText)findViewById(R.id.etEditRequestNumberOfPeople);
        switchEditRequestVeg = (Switch)findViewById(R.id.switchEditRequestVeg);
        switchEditRequestNonVeg = (Switch)findViewById(R.id.switchEditRequestNonVeg);
        pbEditRequest = (ProgressBar)findViewById(R.id.pbEditRequest);
        btnEditRequest = (Button)findViewById(R.id.btnUpdateRequest);

        btnEditRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : Field contents.
                 * Utility : Validate field contents and update donation document.
                 * Output : None.
                 */
                String number_of_people = etEditRequestNumberOfPeople.getText().toString();
                String region = etEditRequestRegion.getText().toString();
                boolean veg = switchEditRequestVeg.isChecked();
                boolean nonVeg = switchEditRequestNonVeg.isChecked();
                //Validate entries in form.
                if (number_of_people.isEmpty() || region.isEmpty() || number_of_people.equals("0") || (!veg && !nonVeg))
                {
                    if (number_of_people.isEmpty())
                    {
                        etEditRequestNumberOfPeople.setError("Field cannot be empty !");
                        etEditRequestNumberOfPeople.requestFocus();
                    }
                    if (region.isEmpty())
                    {
                        etEditRequestRegion.setError("Region cannot be empty !");
                        etEditRequestRegion.requestFocus();
                    }
                    if (number_of_people.equals("0"))
                    {
                        etEditRequestNumberOfPeople.setError("Enter valid number !");
                        etEditRequestNumberOfPeople.requestFocus();
                    }
                    if (!veg && !nonVeg)
                    {
                        Toast.makeText(getApplicationContext(), "Please select appropriate food category(veg or non veg).", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    //Show progress bar
                    pbEditRequest.setVisibility(View.VISIBLE);
                    //Update entries in document.
                    request_document.update("number_of_people", Integer.parseInt(number_of_people));
                    request_document.update("region", region);
                    request_document.update("veg_content", veg);
                    request_document.update("non_veg_content", nonVeg);
                    //Hide progress bar
                    pbEditRequest.setVisibility(View.GONE);
                    //Display completion message.
                    Toast.makeText(EditRequest.this, "Request successfully updated !", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        pbEditRequest.setVisibility(View.VISIBLE);
        request_document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                /*
                 * Input : None
                 * Utility : Update text field values depending on document details.
                 * Output : None
                 */
                etEditRequestNumberOfPeople.setText(String.valueOf(value.get("number_of_people")));
                etEditRequestRegion.setText(value.getString("region"));
                switchEditRequestVeg.setChecked(value.getBoolean("veg_content"));
                switchEditRequestNonVeg.setChecked(value.getBoolean("non_veg_content"));
            }
        });
        pbEditRequest.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
         * Input : None
         * Utility : Navigate back to Request activity.
         * Output : Activity launch.
         */
        //Initialize intent.
        Intent intent = new Intent(getApplicationContext(), RequestActivity.class);
        intent.putExtra("remember_me", this.remember_me);
        startActivity(intent);
        //Destroy current intent.
        this.finish();
    }
}