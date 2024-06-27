package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddNotes extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private Button btnSimpan;
    private EditText edtNoteTitle, edtNoteBody;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btn_back);
        btnSimpan = findViewById(R.id.btn_Simpan);
        edtNoteTitle = findViewById(R.id.edt_NoteTitle);
        edtNoteBody = findViewById(R.id.edt_NoteBody);

        btnBack.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        if(id == R.id.btn_back){
            Intent back = new Intent(AddNotes.this, Notes.class);
            startActivity(back);
        } else if(id == R.id.btn_Simpan){
            String noteTitle = edtNoteTitle.getText().toString().trim();
            String noteBody = edtNoteBody.getText().toString().trim();

            if (!noteTitle.isEmpty() && !noteBody.isEmpty()) {
                String currentUserID = mAuth.getCurrentUser().getUid();

                Note note = new Note(noteTitle, noteBody, currentUserID);

                db.collection("notes")
                        .add(note)
                        .addOnSuccessListener(documentReference -> {
                            String newNoteId = documentReference.getId();

                            note.setNoteId(newNoteId);

                            db.collection("notes").document(newNoteId)
                                    .set(note)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(AddNotes.this, "Catatan berhasil disimpan", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(AddNotes.this, Notes.class);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AddNotes.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddNotes.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(AddNotes.this, "Judul dan Isi Catatan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }
        }
    }
}