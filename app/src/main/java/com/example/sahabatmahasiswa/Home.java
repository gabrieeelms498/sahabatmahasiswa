package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Home extends AppCompatActivity implements View.OnClickListener {
    private static final String VIDEO_ID = "MHd7fvR8fnU";
    private ImageButton btnMatkul;
    private ImageButton btnCatatan;
    private ImageButton btnTugas;
    private ImageButton btnDokumen;
    private ImageButton btnCapaian;
    private RelativeLayout btnEduSpend1;
    private ImageButton btnEduSpend2;
    private TextView btnEduSpend3;
    private RelativeLayout btnProfile1;
    private ImageButton btnProfile2;
    private TextView btnProfile3;
    private TextView tvGreet;
    private ImageButton btnSemua;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        btnSemua = findViewById(R.id.btn_semua);
        btnSemua.setOnClickListener(this);
        btnEduSpend1 = findViewById(R.id.rl_EduSpend);
        btnEduSpend1.setOnClickListener(this);
        btnEduSpend2 = findViewById(R.id.btn_EduSpend);
        btnEduSpend2.setOnClickListener(this);
        btnEduSpend3 = findViewById(R.id.tv_EduSpend);
        btnEduSpend3.setOnClickListener(this);
        btnProfile1 = findViewById(R.id.rl_Profile);
        btnProfile1.setOnClickListener(this);
        btnProfile2 = findViewById(R.id.btn_Profile);
        btnProfile2.setOnClickListener(this);
        btnProfile3 = findViewById(R.id.tv_Profile);
        btnProfile3.setOnClickListener(this);

        tvGreet = findViewById(R.id.tv_greet);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String userName = document.getString("name");
                                tvGreet.setText("Selamat datang, " + userName + "!");
                            } else {
                                Toast.makeText(Home.this, "Document not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Home.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        ImageView imageView1 = findViewById(R.id.iv_video);


        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.youtube.com/watch?v=Pk5S1hz5nh0&t=1s";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        ImageView imageView2 = findViewById(R.id.iv_artikel);


        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.kompasiana.com/ernaerviana8446/655f61e912d50f5750490362/pentingnya-pendidikan-dalam-membentuk-masa-depan";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        if(id == R.id.btn_catatan) {
            Intent catatan = new Intent(Home.this, Notes.class);
            startActivity(catatan);
        } else if(id == R.id.btn_tugas){
            Intent tugas = new Intent(Home.this, Task.class);
            startActivity(tugas);
        } else if(id == R.id.btn_dokumen){
            Intent dokumen = new Intent(Home.this, Files.class);
            startActivity(dokumen);
        } else if(id == R.id.btn_capaian){
            Intent capaian = new Intent(Home.this, Goals.class);
            startActivity(capaian);
        } else if(id == R.id.btn_semua){
            Intent semua = new Intent(Home.this, AllMenu.class);
            startActivity(semua);
        } else if(id == R.id.btn_matakuliah){
            Intent matakuliah = new Intent(Home.this, Matkul.class);
            startActivity(matakuliah);
        } else if(id == R.id.rl_EduSpend){
            Intent eduspend = new Intent(Home.this, EduSpend.class);
            startActivity(eduspend);
        } else if(id == R.id.btn_EduSpend){
            Intent eduspend = new Intent(Home.this, EduSpend.class);
            startActivity(eduspend);
        } else if(id == R.id.tv_EduSpend){
            Intent eduspend = new Intent(Home.this, EduSpend.class);
            startActivity(eduspend);
        } else if(id == R.id.rl_Profile){
            Intent profile = new Intent(Home.this, Profile.class);
            startActivity(profile);
        } else if(id == R.id.btn_Profile){
            Intent profile = new Intent(Home.this, Profile.class);
            startActivity(profile);
        } else if(id == R.id.tv_Profile){
            Intent profile = new Intent(Home.this, Profile.class);
            startActivity(profile);
        }
    }
}