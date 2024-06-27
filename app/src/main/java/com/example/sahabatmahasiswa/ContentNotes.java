package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ContentNotes extends AppCompatActivity {

    private EditText edtNoteTitle;
    private EditText edtNoteBody;
    private String noteId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_notes);

        db = FirebaseFirestore.getInstance();

        String title = getIntent().getStringExtra("Notes_TITLE");
        String body = getIntent().getStringExtra("Notes_DETAIL");
        noteId = getIntent().getStringExtra("Note_ID");

        Log.d("ContentNotes", "Received Note ID: " + noteId);

        edtNoteTitle = findViewById(R.id.edt_NoteTitle);
        edtNoteBody = findViewById(R.id.edt_NoteBody);
        edtNoteTitle.setText(title);
        edtNoteBody.setText(body);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(ContentNotes.this, Notes.class);
                startActivity(back);
                finish();
            }
        });

        Button btnSimpan = findViewById(R.id.btn_Simpan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = edtNoteTitle.getText().toString().trim();
                String newBody = edtNoteBody.getText().toString().trim();

                if (!newTitle.isEmpty() && !newBody.isEmpty()) {
                    DocumentReference noteRef = db.collection("notes").document(noteId);
                    noteRef.update("title", newTitle, "body", newBody)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ContentNotes.this, "Catatan berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ContentNotes.this, Notes.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ContentNotes.this, "Gagal memperbarui catatan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(ContentNotes.this, "Judul dan isi catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}