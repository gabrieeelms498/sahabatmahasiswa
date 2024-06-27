package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class AllMenu extends AppCompatActivity implements View.OnClickListener{

    private ImageButton btnBack;
    private ImageButton btnMatkul;
    private ImageButton btnCatatan;
    private ImageButton btnTugas;
    private ImageButton btnDokumen;
    private ImageButton btnCapaian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_menu);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnMatkul = findViewById(R.id.btn_matakuliah);
        btnMatkul.setOnClickListener(this);
        btnCatatan = findViewById(R.id.btn_catatan);
        btnCatatan.setOnClickListener(this);
        btnTugas = findViewById(R.id.btn_tugas);
        btnTugas.setOnClickListener(this);
        btnDokumen = findViewById(R.id.btn_dokumen);
        btnDokumen.setOnClickListener(this);
        btnCapaian = findViewById(R.id.btn_capaian);
        btnCapaian.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        int id = v.getId();
        if(id == R.id.btn_catatan) {
            Intent catatan = new Intent(AllMenu.this, Notes.class);
            startActivity(catatan);
        } else if(id == R.id.btn_tugas){
            Intent tugas = new Intent(AllMenu.this, Task.class);
            startActivity(tugas);
        } else if(id == R.id.btn_dokumen){
            Intent dokumen = new Intent(AllMenu.this, Files.class);
            startActivity(dokumen);
        } else if(id == R.id.btn_capaian){
            Intent capaian = new Intent(AllMenu.this, Goals.class);
            startActivity(capaian);
        } else if(id == R.id.btn_matakuliah){
            Intent matakuliah = new Intent(AllMenu.this, Matkul.class);
            startActivity(matakuliah);
        } else if(id == R.id.btn_back){
            Intent back = new Intent(AllMenu.this, Home.class);
            startActivity(back);
        }
    }
}