package com.example.sahabatmahasiswa;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FileContentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_content);

        TextView textViewFileContent = findViewById(R.id.textViewFileContent);

        String fileContent = getIntent().getStringExtra("fileContent");
        textViewFileContent.setText(fileContent);
    }
}