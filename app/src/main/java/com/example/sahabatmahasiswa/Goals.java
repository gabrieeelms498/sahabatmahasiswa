package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Goals extends AppCompatActivity implements View.OnClickListener, GoalsAdapter.OnItemClickListener {

    private ImageButton btnBack;
    private Button btnAddGoals;
    private RecyclerView recyclerView;
    private GoalsAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnAddGoals = findViewById(R.id.btn_AddGoals);
        btnAddGoals.setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GoalsAdapter(this, this);
        recyclerView.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        retrieveGoalsFromFirestore();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            Intent back = new Intent(Goals.this, Home.class);
            startActivity(back);
        } else if (id == R.id.btn_AddGoals) {
            Intent addGoals = new Intent(Goals.this, AddGoals.class);
            startActivity(addGoals);
        }
    }

    private void retrieveGoalsFromFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            CollectionReference goalsRef = db.collection("goals");
            goalsRef.whereEqualTo("userId", currentUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<GoalModel> goalsList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    GoalModel goal = document.toObject(GoalModel.class);
                                    goal.setGoalsId(document.getId());
                                    goalsList.add(goal);
                                }
                                adapter.setGoals(goalsList);
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e("Goals", "Error getting goals: ", task.getException());
                            }
                        }
                    });
        }
    }

    @Override
    public void onDeleteClick(String goalId) {
        db.collection("goals").document(goalId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                retrieveGoalsFromFirestore();
            } else {
                Log.e("Goals", "Error deleting goal: ", task.getException());
            }
        });
    }
}
