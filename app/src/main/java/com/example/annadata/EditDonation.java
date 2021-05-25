package com.example.annadata;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class EditDonation extends AppCompatActivity {
    private EditText etEditContent, etEditNumberOfPeople, etEditRegion;
    private Switch switchEditVeg, switchEditNonVeg;
    private Button btnEditDonation;
    private ProgressBar pbEditDonations;
    private boolean remember_me;
    private String donation_id;
    private DocumentReference donation_document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donation);

        Intent intent = getIntent();
        //Capture values.
        remember_me = intent.getBooleanExtra("remember_me", true);
        donation_id = intent.getStringExtra("donation_id");
        donation_document = FirebaseFirestore.getInstance().collection("donations").document(donation_id);

        //Initializing widgets;
        etEditContent = (EditText)findViewById(R.id.etEditContent);
        etEditRegion = (EditText)findViewById(R.id.etEditRegion);
        etEditNumberOfPeople = (EditText)findViewById(R.id.etEditNumberOfPeople);
        switchEditVeg = (Switch)findViewById(R.id.switchEditVeg);
        switchEditNonVeg = (Switch)findViewById(R.id.switchEditNonVeg);
        pbEditDonations = (ProgressBar)findViewById(R.id.pbEditDonation);
        btnEditDonation = (Button)findViewById(R.id.btnEditDonation);

        //Set behaviour for donations.
        btnEditDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : Field contents.
                 * Utility : Validate field contents and update donation document.
                 * Output : None.
                 */
                String number_of_people = etEditNumberOfPeople.getText().toString();
                String content = etEditContent.getText().toString();
                String region = etEditRegion.getText().toString();
                boolean veg = switchEditVeg.isChecked();
                boolean nonVeg = switchEditNonVeg.isChecked();
                //Validate entries in form.
                if (number_of_people.isEmpty() || content.isEmpty() || region.isEmpty() || number_of_people.equals("0") || (!veg && !nonVeg))
                {
                    if (number_of_people.isEmpty())
                    {
                        etEditNumberOfPeople.setError("Field cannot be empty !");
                        etEditNumberOfPeople.requestFocus();
                    }
                    if (content.isEmpty())
                    {
                        etEditContent.setError("Content cannot be empty !");
                        etEditContent.requestFocus();
                    }
                    if (region.isEmpty())
                    {
                        etEditRegion.setError("Region cannot be empty !");
                        etEditRegion.requestFocus();
                    }
                    if (number_of_people.equals("0"))
                    {
                        etEditNumberOfPeople.setError("Enter valid number !");
                        etEditNumberOfPeople.requestFocus();
                    }
                    if (!veg && !nonVeg)
                    {
                        Toast.makeText(getApplicationContext(), "Please select appropriate food category(veg or non veg).", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    //Show progress bar
                    pbEditDonations.setVisibility(View.VISIBLE);
                    //Update entries in document.
                    donation_document.update("number_of_people", number_of_people);
                    donation_document.update("region", region);
                    donation_document.update("content", content);
                    donation_document.update("veg_content", veg);
                    donation_document.update("non_veg_content", nonVeg);
                    //Hide progress bar
                    pbEditDonations.setVisibility(View.GONE);
                    //Display completion message.
                    Toast.makeText(EditDonation.this, "Donation successfully updated !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        pbEditDonations.setVisibility(View.VISIBLE);
        donation_document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                /*
                 * Input : None
                 * Utility : Update text field values depending on document details.
                 * Output : None
                 */
                etEditContent.setText(value.getString("content"));
                etEditNumberOfPeople.setText(String.valueOf(value.get("number_of_people")));
                etEditRegion.setText(value.getString("region"));
                switchEditVeg.setChecked(value.getBoolean("veg_content"));
                switchEditNonVeg.setChecked(value.getBoolean("non_veg_content"));
            }
        });
        pbEditDonations.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
         * Input : None
         * Utility : Navigate back to Donation activity.
         * Output : Activity launch.
         */
        //Initialize intent.
        Intent intent = new Intent(getApplicationContext(), DonationActivity.class);
        intent.putExtra("remember_me", this.remember_me);
        startActivity(intent);
        //Destroy current intent.
        this.finish();
    }
}