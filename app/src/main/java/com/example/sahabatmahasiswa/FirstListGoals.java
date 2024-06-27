package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class FirstListGoals extends AppCompatActivity {

    private TextView tvTujuanTitle;
    private RecyclerView recyclerView;
    private ImageButton btnBack;
    private Button btnSimpan;
    private FirebaseFirestore db;
    private GoalsAdapter2 adapter;
    private GoalModel goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_list_goals);

        tvTujuanTitle = findViewById(R.id.tv_Tujuantitle);
        recyclerView = findViewById(R.id.recycler_view);
        btnBack = findViewById(R.id.btn_back);
        btnSimpan = findViewById(R.id.btn_Simpan);
        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCheckboxStatus();
            }
        });

        goal = (GoalModel) getIntent().getSerializableExtra("goal");

        if (goal != null) {
            tvTujuanTitle.setText(goal.getCapaian());
            Log.d("TODO_ITEM", "IDNYA: " + goal.getGoalsId());
            List<Map<String, Object>> todoList = goal.getTodoList();
            Log.d("TODO_ITEM", "TodoList size: " + todoList.size());

            if (todoList != null) {
                adapter = new GoalsAdapter2(this, todoList);
                recyclerView.setAdapter(adapter);
            } else {
            }
        } else {
        }
    }

    private void saveCheckboxStatus() {
        if (goal != null && adapter != null) {
            List<Map<String, Object>> todoList = goal.getTodoList();
            for (int i = 0; i < adapter.getItemCount(); i++) {
                GoalsAdapter2.TodoViewHolder viewHolder = (GoalsAdapter2.TodoViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                if (viewHolder != null) {
                    CheckBox checkBox = viewHolder.todoCheckbox;
                    boolean isChecked = checkBox.isChecked();
                    Map<String, Object> todoItem = todoList.get(i);
                    todoItem.put("status", isChecked);
                }
            }
            db.collection("goals").document(goal.getGoalsId()).set(goal)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(FirstListGoals.this, "Capaian berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(FirstListGoals.this, Goals.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FirstListGoals.this, "Gagal memperbarui Capaian: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
