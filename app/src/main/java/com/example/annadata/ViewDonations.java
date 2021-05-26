package com.example.annadata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewDonations extends AppCompatActivity {

    private boolean remember_me;
    private String request_id;
    private String request_person_id;
    private String region;
    private ListView viewDonationsListView;
    private ProgressBar pbViewDonations;
    private DocumentReference request_document;
    private CollectionReference donation_collection;
    private long number_of_people;
    private boolean veg_content, non_veg_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donations);

        //Capture variables from intent.
        Intent intent = getIntent();
        remember_me = intent.getBooleanExtra("remember_me", true);
        request_id = intent.getStringExtra("request_id");
        request_person_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Initialize widgets
        viewDonationsListView = (ListView)findViewById(R.id.viewDonationsListView);
        pbViewDonations = (ProgressBar)findViewById(R.id.pbViewDonations);
        pbViewDonations.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
         * Input : None
         * Utility : On pressing bck button, route to requests activity.
         * Output : Launch request activity.
         */
        //Initialize intent.
        Intent intent = new Intent(getApplicationContext(), RequestActivity.class);
        intent.putExtra("remember_me", remember_me);
        startActivity(intent);
        //Destroy current activity.
        ViewDonations.this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
         * Input : None
         * Utility : Get all documents relevant to current request with appropriate query processing.
         * Output : None
         */
        pbViewDonations.setVisibility(View.VISIBLE);
        //Locate document and collection references from FireStore database.
        request_document = FirebaseFirestore.getInstance().collection("requests").document(request_id);
        donation_collection = FirebaseFirestore.getInstance().collection("donations");
        //Get Request Document data.
        request_document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    number_of_people = document.getLong("number_of_people");
                    veg_content = document.getBoolean("veg_content");
                    non_veg_content = document.getBoolean("non_veg_content");
                    region = document.getString("region");
                    //Get Relevant donations and render to screen using custom card view adapters.
                    donation_collection
                            .whereEqualTo("veg_content", veg_content)
                            .whereEqualTo("non_veg_content", non_veg_content)
                            .whereGreaterThanOrEqualTo("number_of_people", number_of_people)
                            .whereEqualTo("region", region)
                            .whereEqualTo("is_active", true)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        List<Map<String, Object>> relevantDonationList = new ArrayList<>();
                                        for(QueryDocumentSnapshot document : task.getResult())
                                        {
                                            Map<String, Object> donation_document = document.getData();
                                            Log.d("Data Point :", String.valueOf(document.getData()));
                                            if(!donation_document.get("donor_id").equals(request_person_id)) {
                                                relevantDonationList.add(document.getData());
                                            }
                                        }
                                        //Toast.makeText(getApplicationContext(), "Completed : " + i, Toast.LENGTH_SHORT).show();
                                        if(relevantDonationList.isEmpty())
                                        {
                                            Toast.makeText(getApplicationContext(), " No relevant donations found ! ", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            RelevantDonationList adapter = new RelevantDonationList(ViewDonations.this,  relevantDonationList, remember_me, request_id);
                                            viewDonationsListView.setAdapter(adapter);
                                        }
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        pbViewDonations.setVisibility(View.GONE);
    }
}