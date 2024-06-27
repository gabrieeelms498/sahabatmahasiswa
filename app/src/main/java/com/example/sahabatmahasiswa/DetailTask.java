package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class DetailTask extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);

        btnBack = (ImageButton)findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        int id = v.getId();
        if(id == R.id.btn_back){
            Intent back = new Intent(DetailTask.this, Task.class);
            startActivity(back);
        }
    }
}