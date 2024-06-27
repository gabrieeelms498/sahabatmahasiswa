package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Notes extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private Button btnAddNotes;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserID = mAuth.getCurrentUser().getUid();
        } else {
            Intent intent = new Intent(Notes.this, Login.class);
            startActivity(intent);
            finish();
        }

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnAddNotes = findViewById(R.id.btn_AddNotes);
        btnAddNotes.setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db.collection("notes")
                .whereEqualTo("userId", currentUserID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Note> notesList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Note note = document.toObject(Note.class);
                                note.setNoteId(document.getId());
                                notesList.add(note);
                            }
                            NotesAdapter adapter = new NotesAdapter(Notes.this, notesList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(Notes.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            Intent back = new Intent(Notes.this, Home.class);
            startActivity(back);
        } else if (id == R.id.btn_AddNotes) {
            Intent addNotes = new Intent(Notes.this, AddNotes.class);
            startActivity(addNotes);
        }
    }
}