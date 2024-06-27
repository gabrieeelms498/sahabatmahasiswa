package com.example.sahabatmahasiswa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class GoalsAdapter2 extends RecyclerView.Adapter<GoalsAdapter2.TodoViewHolder> {

    private Context context;
    private List<Map<String, Object>> todoList;

    public GoalsAdapter2(Context context, List<Map<String, Object>> todoList) {
        this.context = context;
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkbox, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Map<String, Object> todoItem = todoList.get(position);
        String todoText = (String) todoItem.get("todoText");
        Boolean status = (Boolean) todoItem.get("status");

        holder.todoCheckbox.setText(todoText);
        holder.todoCheckbox.setChecked(status);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {

        CheckBox todoCheckbox;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            todoCheckbox = itemView.findViewById(R.id.cbox);
        }
    }
}


