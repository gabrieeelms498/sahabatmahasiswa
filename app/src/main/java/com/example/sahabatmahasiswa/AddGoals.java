package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGoals extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private FirebaseAuth mAuth;
    private Button btnSimpan;
    private LinearLayout containerTodoList;
    private Button btnAddTodoList;
    private EditText edtCapaian;
    private int todoListCount = 1;

    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goals);
        mAuth = FirebaseAuth.getInstance();
        btnBack = (ImageButton)findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnSimpan = (Button)findViewById(R.id.btn_Simpan);
        btnSimpan.setOnClickListener(this);
        edtCapaian = findViewById(R.id.edt_Capaian);
        btnAddTodoList = findViewById(R.id.btn_Addtodolist);
        containerTodoList = findViewById(R.id.container_todo_list);
        db = FirebaseFirestore.getInstance();
        if (containerTodoList == null) {
            Log.e("AddGoals", "containerTodoList is null!");
        } else {
            Log.d("AddGoals", "containerTodoList initialized successfully.");
        }

        if (btnAddTodoList == null) {
            Log.e("AddGoals", "btnAddTodoList is null!");
        } else {
            Log.d("AddGoals", "btnAddTodoList initialized successfully.");
        }
        btnAddTodoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTodoEditText();
            }
        });
    }
    private void saveDataToFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(AddGoals.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference newDocRef = db.collection("goals").document();
        String userId = currentUser.getUid();
        String capaian = edtCapaian.getText().toString().trim();
        String goalsId = newDocRef.getId();
        List<TodoItem> todoList = new ArrayList<>();

        if (containerTodoList == null) {
            Log.e("AddGoals", "containerTodoList is null!");
            return;
        }

        for (int i = 0; i < containerTodoList.getChildCount(); i++) {
            View view = containerTodoList.getChildAt(i);
            if (view instanceof EditText) {
                String todoText = ((EditText) view).getText().toString().trim();
                if (!todoText.isEmpty()) {
                    todoList.add(new TodoItem(todoText, false));
                }
            }
        }

        if (!capaian.isEmpty() && !todoList.isEmpty()) {
            Map<String, Object> goal = new HashMap<>();
            goal.put("capaian", capaian);
            List<Map<String, Object>> todoListMap = new ArrayList<>();

            for (TodoItem todoItem : todoList) {
                Map<String, Object> todoItemMap = new HashMap<>();
                todoItemMap.put("todoText", todoItem.getText());
                todoItemMap.put("status", todoItem.isStatus());
                todoListMap.add(todoItemMap);
            }

            goal.put("todoList", todoListMap);
            goal.put("userId", userId);
            goal.put("goalsId", goalsId);

            db.collection("goals").document(goalsId)
                    .set(goal)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddGoals.this, "Capaian berhasil disimpan", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AddGoals.this, Goals.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddGoals.this, "Gagal menyimpan capaian", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        if(id == R.id.btn_back){
            Intent back = new Intent(AddGoals.this, Goals.class);
            startActivity(back);
        } else if(id == R.id.btn_Simpan){
            saveDataToFirebase();
        }
    }
    private void addNewTodoEditText() {
        LinearLayout containerTodoList = (LinearLayout)findViewById(R.id.container_todo_list);
        EditText newTodo = new EditText(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        newTodo.setHint("Tulis disini...");
        newTodo.setLayoutParams(p);
        newTodo.setBackgroundResource(R.color.edt);
        newTodo.setId(todoListCount + 1);
        float scale = getResources().getDisplayMetrics().density;
        int left = (int) (15*scale + 0.5f);
        int rest = (int) (5*scale + 0.5f);
        newTodo.setPadding(left, rest, rest, rest);
        p.setMargins(0,16,0,16);
        containerTodoList.addView(newTodo);
        todoListCount++;
    }
    public class TodoItem {
        private String text;
        private boolean status;

        public TodoItem(String text, boolean status) {
            this.text = text;
            this.status = status;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }


    }

}