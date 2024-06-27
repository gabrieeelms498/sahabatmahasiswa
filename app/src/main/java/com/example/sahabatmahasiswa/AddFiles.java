package com.example.sahabatmahasiswa;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AddFiles extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_FILE_REQUEST = 1;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private ImageButton btnBack;
    private ImageButton btnUpload;
    private TextView unggah;
    private Button btnSimpan;
    private Button btnCancel;
    private TextView fileName;

    private Uri fileUri; // Untuk menyimpan URI file yang dipilih
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_files);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnUpload = findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(this);
        btnSimpan = findViewById(R.id.btn_Simpan);
        btnSimpan.setOnClickListener(this);
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        fileName = findViewById(R.id.file_name);
        unggah = findViewById(R.id.tv_upload1);
        unggah.setOnClickListener(this); // Menambahkan onClick untuk TextView "Unggah Dokumen"

        // Inisialisasi Firebase Storage
        mStorageRef = FirebaseStorage.getInstance().getReference("files");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            Intent back = new Intent(AddFiles.this, Files.class);
            startActivity(back);
        } else if (id == R.id.btn_upload || id == R.id.tv_upload1) {
            chooseFile();
        } else if (id == R.id.btn_Simpan) {
            if (fileUri != null) {
                if (isFileSizeValid(fileUri) && isFileExtensionValid(fileUri)) {
                    uploadFile();
                } else {
                    Toast.makeText(this, "Invalid file. Please select a file with a valid size and format.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btn_cancel) {
            fileUri = null;
            fileName.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnUpload.setVisibility(View.VISIBLE);
            Toast.makeText(this, "File selection canceled", Toast.LENGTH_SHORT).show();
        }
    }

    // Fungsi untuk memilih file
    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            fileName.setText(getFileName(fileUri));
            fileName.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnUpload.setVisibility(View.GONE);
        }
    }

    // Fungsi untuk mengunggah file ke Firebase Storage
    private void uploadFile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(AddFiles.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fileUri != null) {
            String fileName = getFileName(fileUri);
            String currentUserId = currentUser.getUid();

            // 1. Buat referensi untuk file di Storage
            StorageReference fileRef = mStorageRef.child("uploads/" + fileName);

            // 2. Upload file ke Storage
            fileRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // 3. Setelah file berhasil diunggah, simpan data ke Firestore
                        Map<String, Object> fileData = new HashMap<>();
                        fileData.put("userId", currentUserId);
                        fileData.put("fileName", fileName);

                        db.collection("files").add(fileData)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                                    // 4. Navigasi ke Files.java setelah data Firestore disimpan
                                    Toast.makeText(AddFiles.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                                    Intent save = new Intent(AddFiles.this, Files.class);
                                    startActivity(save);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error adding document", e);
                                    Toast.makeText(AddFiles.this, "Failed to save file data", Toast.LENGTH_SHORT).show();
                                    // Anda mungkin ingin menghapus file dari Storage jika penyimpanan data Firestore gagal
                                    fileRef.delete()
                                            .addOnSuccessListener(aVoid -> Log.d(TAG, "File dihapus dari Storage setelah error Firestore"))
                                            .addOnFailureListener(e1 -> Log.e(TAG, "Gagal menghapus file dari Storage", e1));

                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to upload file", e);
                        Toast.makeText(AddFiles.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Fungsi untuk memeriksa ukuran file
    private boolean isFileSizeValid(Uri fileUri) {
        try {
            File file = new File(fileUri.getPath());
            return file.length() <= MAX_FILE_SIZE;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fungsi untuk memeriksa ekstensi file
    private boolean isFileExtensionValid(Uri fileUri) {
        String[] validExtensions = {"doc", "docx", "pdf", "xls", "xlsx", "csv"};
        String extension = getFileExtension(fileUri);
        for (String validExtension : validExtensions) {
            if (validExtension.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    // Fungsi untuk mendapatkan ekstensi file
    private String getFileExtension(Uri fileUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(fileUri));
    }

    // Fungsi untuk mendapatkan nama file dari URI
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}