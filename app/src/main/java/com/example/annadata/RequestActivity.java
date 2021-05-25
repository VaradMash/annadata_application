package com.example.annadata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestActivity extends AppCompatActivity {
    private CollectionReference requestCollection;
    private Activity context;
    private ProgressBar pbRequests;
    private FloatingActionButton btnNewRequest;
    private ListView requestListView;
    private boolean remember_me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        Intent intent = getIntent();
        remember_me = intent.getBooleanExtra("remember_me", true);

        //Initialize widgets
        pbRequests = (ProgressBar)findViewById(R.id.pbRequests);
        btnNewRequest = (FloatingActionButton)findViewById(R.id.btnNewRequest);
        requestListView = (ListView)findViewById(R.id.requestScrollView);

        //Set behaviour for new request button.
        btnNewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Input : None
                 * Utility : Launch new donation activity.
                 * Output : New Donation activity launch.
                 */
                //Initialize intent
                Intent intent = new Intent(getApplicationContext(), NewRequestActivity.class);
                intent.putExtra("remember_me", remember_me);
                startActivity(intent);
                //Destroy current activity.
                RequestActivity.this.finish();
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
        RequestActivity.this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
         * Input : None
         * Utility : Get Donations made by user and render on activity screen with card view.
         * Output : None
         */
        //Display progress bar
        pbRequests.setVisibility(View.VISIBLE);
        //Initialize collection reference.
        requestCollection = FirebaseFirestore.getInstance().collection("requests");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        context = RequestActivity.this;
        List<Map<String, Object>> requestList = new ArrayList<Map<String, Object>>();
        //Get relevant documents.
        requestCollection
                .whereEqualTo("request_person_id", uid)
                .whereEqualTo("is_active", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot data : task.getResult())
                            {
                                requestList.add(data.getData());
                            }
                            if(requestList.isEmpty())
                            {
                                Toast.makeText(context, "No active requests found !", Toast.LENGTH_SHORT).show();
                            }
                            RequestList adapter = new RequestList(context, requestList, remember_me);
                            requestListView.setAdapter(adapter);
                            pbRequests.setVisibility(View.GONE);
                        }
                        else {
                            pbRequests.setVisibility(View.GONE);
                            //Display error message.
                            Toast.makeText(context, "An error occurred !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}