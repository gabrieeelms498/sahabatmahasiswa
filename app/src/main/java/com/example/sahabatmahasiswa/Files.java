package com.example.sahabatmahasiswa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Files extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FilesActivity";
    private ImageButton btnBack;
    private AppCompatButton btnAddFiles;
    private RecyclerView recyclerView;
    private FilesAdapter filesAdapter;
    private List<StorageReference> fileList;

    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private BroadcastReceiver filesUpdateReceiver;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        // Inisialisasi View
        btnBack = findViewById(R.id.btn_back);
        btnAddFiles = findViewById(R.id.btn_AddFiles);
        recyclerView = findViewById(R.id.recyclerView);

        // Set OnClickListener untuk tombol
        btnBack.setOnClickListener(this);
        btnAddFiles.setOnClickListener(this);

        // Inisialisasi Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Setup RecyclerView
        fileList = new ArrayList<>();
        filesAdapter = new FilesAdapter(fileList, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(filesAdapter);

        // Mengambil daftar file dari Firebase
        fetchFiles();

        // Set OnClickListener untuk item di RecyclerView (delete)
        filesAdapter.setOnItemClickListener(new FilesAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position, FilesAdapter.FileViewHolder holder) {
                // Panggil method deleteFileFromFirestoreAndStorage untuk menghapus file dari Firestore dan Storage
                deleteFileFromFirestoreAndStorage(position, holder);
            }
        });

        // Register BroadcastReceiver untuk refresh data
        filesUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.example.tesfirebaseregisterlogin.REFRESH_FILES")) {
                    fetchFiles();
                }
            }
        };
        registerReceiver(filesUpdateReceiver, new IntentFilter("com.example.tesfirebaseregisterlogin.REFRESH_FILES"));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            Intent back = new Intent(Files.this, Home.class);
            startActivity(back);
        } else if (id == R.id.btn_AddFiles) {
            Intent addfiles = new Intent(Files.this, AddFiles.class);
            startActivity(addfiles);
        }
    }

    private void deleteFileFromFirestoreAndStorage(int position, FilesAdapter.FileViewHolder holder) {
        if (position != RecyclerView.NO_POSITION && position < fileList.size()) {
            StorageReference fileRef = holder.fileRef;
            String fileName = fileRef.getName(); // Dapatkan nama file
            String currentUserId = mAuth.getCurrentUser().getUid();

            // 1. Hapus Dokumen dari Firestore
            db.collection("files")
                    .whereEqualTo("userId", currentUserId)
                    .whereEqualTo("fileName", fileName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("files").document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "Dokumen Firestore berhasil dihapus!");

                                            // 2. Cek keberadaan file di Storage sebelum menghapus
                                            fileRef.getMetadata()
                                                    .addOnSuccessListener(storageMetadata -> {
                                                        // File ada, hapus dari Storage
                                                        fileRef.delete()
                                                                .addOnSuccessListener(aVoid1 -> {
                                                                    Toast.makeText(Files.this, "File berhasil dihapus", Toast.LENGTH_SHORT).show();

                                                                    // Update data di RecyclerView
                                                                    fileList.remove(position);
                                                                    filesAdapter.notifyItemRemoved(position);
                                                                    filesAdapter.notifyItemRangeChanged(position, fileList.size());
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Log.e(TAG, "Gagal menghapus file dari Storage", e);
                                                                    Toast.makeText(Files.this, "Gagal menghapus file dari Storage", Toast.LENGTH_SHORT).show();
                                                                });
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // File tidak ada, mungkin sudah dihapus
                                                        Log.w(TAG, "File tidak ditemukan di Storage", e);
                                                        // Tetap update RecyclerView karena dokumen Firestore sudah dihapus
                                                        Toast.makeText(Files.this, "File berhasil dihapus", Toast.LENGTH_SHORT).show();
                                                        fileList.remove(position);
                                                        filesAdapter.notifyItemRemoved(position);
                                                        filesAdapter.notifyItemRangeChanged(position, fileList.size());
                                                    });

                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w(TAG, "Error deleting document", e);
                                            Toast.makeText(Files.this, "Gagal menghapus dokumen", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(Files.this, "Gagal mendapatkan dokumen", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Method untuk mengambil daftar file dari Firebase
    private void fetchFiles() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            db.collection("files")
                    .whereEqualTo("userId", currentUserId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        fileList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String fileName = documentSnapshot.getString("fileName");
                            if (fileName != null) {
                                StorageReference fileRef = storageRef.child("uploads/" + currentUserId + "/" + fileName);
                                fileList.add(fileRef);
                            } else {
                                Log.w(TAG, "File name is null for document: " + documentSnapshot.getId());
                            }
                        }
                        filesAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching files", e);
                        Toast.makeText(Files.this, "Failed to fetch files", Toast.LENGTH_SHORT).show();
                    });
        }
    }




    public void onFileNameClick(View view) {
        int position = recyclerView.getChildLayoutPosition((View) view.getParent());
        if (position != RecyclerView.NO_POSITION) {
            StorageReference fileRef = fileList.get(position);
            openFile(fileRef,Files.this);
        }
    }

    public void openFile(StorageReference fileRef, Context context) {
        String fileName = fileRef.getName();
        String[] splitFileName = fileName.split("\\.");
        String fileExtension = splitFileName[splitFileName.length - 1].toLowerCase(); // Get the file extension
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mimeType);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent chooser = Intent.createChooser(intent, "Open File");
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(chooser);
            } else {
                Toast.makeText(context, "No application found to open this file fILES", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(exception -> {
            Toast.makeText(context, "Failed to get file URL", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to get file URL", exception);
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(filesUpdateReceiver);
    }
}