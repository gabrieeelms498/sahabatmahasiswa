package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Task extends AppCompatActivity implements View.OnClickListener, TaskAdapter.OnItemClickListener {

    private ImageButton btnBack;
    private RelativeLayout rlList1;
    private TextView tvList1;
    private Button btnAddTask;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TaskAdapter adapter;
    private RecyclerView recyclerView;
    private List<TaskModel> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnAddTask = findViewById(R.id.btn_AddTask);
        btnAddTask.setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(this, this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        retrieveGoalsFromFirestore();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            Intent back = new Intent(Task.this, Home.class);
            startActivity(back);
        } else if (id == R.id.btn_AddTask) {
            Intent addTask = new Intent(Task.this, AddTask.class);
            startActivity(addTask);
        }
    }

    @Override
    public void onDeleteClick(String taskId) {
        db.collection("task").document(taskId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Task", "Task successfully deleted!");
                    retrieveGoalsFromFirestore();
                })
                .addOnFailureListener(e -> Log.w("Task", "Error deleting task", e));
    }

    @Override
    public void onStatusChange(int position, String taskId, boolean isChecked) {
        updateTaskStatus(position, taskId, isChecked);
    }

    private void updateTaskStatus(int position, String taskId, boolean isChecked) {
        DocumentReference noteRef = db.collection("task").document(taskId);
        noteRef.update("status", isChecked)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Task", "Task status updated successfully!");
                    taskList.get(position).setStatus(isChecked);
                    adapter.notifyItemChanged(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Task.this, "Gagal memperbarui Tugas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void retrieveGoalsFromFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            CollectionReference goalsRef = db.collection("task");
            goalsRef.whereEqualTo("userId", currentUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<TaskModel> tugasList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    TaskModel Tugas = document.toObject(TaskModel.class);
                                    Tugas.setTaskId(document.getId());
                                    tugasList.add(Tugas);
                                }
                                taskList = tugasList;
                                adapter.setTask(tugasList);
                            } else {
                                Log.e("Task", "Error getting Task: ", task.getException());
                            }
                        }
                    });
        }
    }
}