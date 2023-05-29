package com.example.evarkadasibulma;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<User> userList, filterList;
    private EditText duration, distance;
    private Button filter, iptal;
    private ImageView logout,settings;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        logout = findViewById(R.id.logout);
        settings = findViewById(R.id.settings);
        recyclerView = findViewById(R.id.rv);
        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        filterList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        duration = findViewById(R.id.durationEditText);
        distance = findViewById(R.id.distanceEditText);
        filter = findViewById(R.id.filterButton);
        iptal = findViewById(R.id.cancelButton);
        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                System.out.println( "Fetching FCM registration token failed");
                                return;
                            }

                            String token = task.getResult();
                            Log.d("TOKEN BU",token);

                        }
                    });


        }catch (Exception e){
            Log.d("exc",e.getMessage());


        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(user!=null){
                        Intent intent = new Intent(getApplicationContext(), ProfileSettings.class);
                        intent.putExtra("user",user);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }

            }
        });


        filter.setOnClickListener(view -> {
            String duration = this.duration.getText().toString().trim();
            String distance = this.distance.getText().toString().trim();
            filterList.clear();

            for (User user : userList) {
                if ((!duration.isEmpty() && (user.getDuration() == null || !user.getDuration().equals(duration)))||!distance.isEmpty() && (user.getDistance() == null || !user.getDistance().equals(distance))) {
                //not addable
                }
                else{
                    filterList.add(user);
                }
            }
            Adapter adapter = new Adapter(filterList);
            recyclerView.setAdapter(adapter);
            filter.setVisibility(View.GONE);
            iptal.setVisibility(View.VISIBLE);
        });

        iptal.setOnClickListener(view -> {
            filterList.clear();
            filterList.addAll(userList);
            Adapter adapter = new Adapter(filterList);
            recyclerView.setAdapter(adapter);
            this.duration.getText().clear();
            this.distance.getText().clear();
            iptal.setVisibility(View.GONE);
            filter.setVisibility(View.VISIBLE);
        });

        db.collection("User")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);

                            if(user!=null && currentUser!=null && !user.getUid().equals(currentUser.getUid())){
                                userList.add(user);
                            }
                            else if(user.getUid().equals(currentUser.getUid())){
                                this.user = user;
                            }
                        }
                        Adapter adapter = new Adapter(userList);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

}