package com.example.sahabatmahasiswa;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MatkulAdapter extends RecyclerView.Adapter<MatkulAdapter.MatkulViewHolder> {

    private Context context;
    private List<MatkulModel> matkulList;

    public MatkulAdapter(Context context, List<MatkulModel> matkulList) {
        this.context = context;
        this.matkulList = matkulList;
    }

    @NonNull
    @Override
    public MatkulViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matkul, parent, false);
        return new MatkulViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatkulViewHolder holder, int position) {
        MatkulModel matkul = matkulList.get(position);
        holder.tvMatkul.setText(matkul.getMatkul() + " (" + matkul.getSks() + " SKS)");
        holder.tvSmt.setText("Semester " + matkul.getSmt());
        holder.tvJadwal.setText(matkul.getRuangan() + " - " + matkul.getDay() + " " + matkul.getStart_time() + " - " + matkul.getEnd_time());
        holder.tvDosen.setText(matkul.getDosen());


        holder.btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, EditMatkul.class);
                intent.putExtra("matkulId", matkul.getmatkulId());
                intent.putExtra("matkul", matkul.getMatkul());
                intent.putExtra("sks", matkul.getSks());
                intent.putExtra("smt", matkul.getSmt());
                intent.putExtra("ruangan", matkul.getRuangan());
                intent.putExtra("dosen", matkul.getDosen());
                intent.putExtra("start_time", matkul.getStart_time());
                intent.putExtra("end_time", matkul.getEnd_time());
                intent.putExtra("day", matkul.getDay());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matkulList.size();
    }

    public static class MatkulViewHolder extends RecyclerView.ViewHolder {

        TextView tvMatkul, tvSmt, tvJadwal, tvDosen;
        ImageButton btnEdit;

        public MatkulViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMatkul = itemView.findViewById(R.id.tv_matkul);
            tvSmt = itemView.findViewById(R.id.tv_smt);
            tvJadwal = itemView.findViewById(R.id.tv_jadwal);
            tvDosen = itemView.findViewById(R.id.tv_dosen);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }
    }
}