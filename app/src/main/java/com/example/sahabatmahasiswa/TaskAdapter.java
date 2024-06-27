package com.example.sahabatmahasiswa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<TaskModel> taskList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(String taskId);
        void onStatusChange(int position, String taskId, boolean isChecked);
    }

    public TaskAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setTask(List<TaskModel> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tugas, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (taskList != null && position < taskList.size()) {
            TaskModel task = taskList.get(position);
            holder.taskTextView.setText(task.getTugas());
            holder.deadline.setText(task.getDay() + " | " + task.getTime());
            holder.deskripsi.setText(task.getDeskripsi());

            holder.cb.setOnCheckedChangeListener(null);
            holder.cb.setChecked(task.isStatus());
            holder.cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onStatusChange(holder.getAdapterPosition(), task.getTaskId(), isChecked);
                updateBackground(holder, isChecked);
            });

            updateBackground(holder, task.isStatus());


            holder.deleteButton.setOnClickListener(v -> {
                listener.onDeleteClick(task.getTaskId());
                Toast.makeText(context, "Tugas berhasil dihapus", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateBackground(TaskViewHolder holder, boolean isChecked) {
        if (isChecked) {
            holder.relativeLayout.setBackgroundResource(R.drawable.task_tv);
        } else {
            holder.relativeLayout.setBackgroundResource(R.drawable.task_tvy);
        }
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTextView, deskripsi, deadline;
        ImageButton deleteButton;
        CheckBox cb;
        RelativeLayout relativeLayout;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTextView = itemView.findViewById(R.id.tv_Tugas);
            deskripsi = itemView.findViewById(R.id.tv_Desc);
            deadline = itemView.findViewById(R.id.tv_dl);
            cb = itemView.findViewById(R.id.cb);
            deleteButton = itemView.findViewById(R.id.btn_Delete1);
            relativeLayout = itemView.findViewById(R.id.relative_layout1);
        }
    }
}