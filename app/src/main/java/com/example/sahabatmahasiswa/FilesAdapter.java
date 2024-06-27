package com.example.sahabatmahasiswa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {
    private List<StorageReference> fileList;
    private Context context;
    public static final String TAG = "FileViewHolder";

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position, FileViewHolder holder);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FilesAdapter(List<StorageReference> fileList, Context context) {
        this.fileList = fileList;
        this.context = context;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_files, parent, false);
        return new FileViewHolder(view, fileList, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        StorageReference fileRef = fileList.get(position);

        // Mengambil nama file dari StorageReference
        String fileName = fileRef.getName();
        holder.textViewFileName.setText(fileName);
        holder.fileRef = fileRef;

        // Set up OnClickListener untuk tombol delete
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position, holder);
                    }
                }
            }
        });

        // Set up OnClickListener untuk item file (untuk mengakses file)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.openFile(holder.fileRef, view.getContext());
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewFileName;
        public ImageButton btnDelete;
        public StorageReference fileRef;
        private List<StorageReference> fileList;
        private FilesAdapter adapter;

        public FileViewHolder(View itemView, List<StorageReference> fileList, FilesAdapter adapter) {
            super(itemView);
            textViewFileName = itemView.findViewById(R.id.textViewFileName);
            btnDelete = itemView.findViewById(R.id.btn_Delete1);
            this.fileList = fileList;
            this.adapter = adapter;
        }

        // Fungsi untuk membuka file (di dalam FileViewHolder)
        public void openFile(StorageReference fileRef, Context context) {
            // Mendapatkan nama file
            String fileName = fileRef.getName();

            // Membuat referensi ke path yang tepat di storage Firebase
            StorageReference storageRef = fileRef.getRoot().child("files/uploads/" + fileName);

            // Mendapatkan URL unduhan
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Log keberhasilan dan buka file menggunakan Intent
                Log.d(TAG, "Download URL obtained successfully: " + uri.toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }).addOnFailureListener(e -> {
                // Log kegagalan dan tampilkan pesan toast
                Log.e(TAG, "Failed to get file URL", e);
                Toast.makeText(context, "Failed to open file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}