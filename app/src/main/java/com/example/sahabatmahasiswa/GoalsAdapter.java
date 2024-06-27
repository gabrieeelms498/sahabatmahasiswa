package com.example.sahabatmahasiswa;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalsViewHolder> {

    private Context context;
    private List<GoalModel> goalsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(String goalId);
    }

    public GoalsAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setGoals(List<GoalModel> goalsList) {
        this.goalsList = goalsList;
    }

    @NonNull
    @Override
    public GoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goals, parent, false);
        return new GoalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalsViewHolder holder, int position) {
        if (goalsList != null && position < goalsList.size()) {
            GoalModel goal = goalsList.get(position);
            holder.goalTextView.setText(goal.getCapaian());
            holder.bind(goal.getGoalsId(), listener);
            boolean allDone = true;
            for (Map<String, Object> todo : goal.getTodoList()) {
                if (!(Boolean) todo.get("status")) {
                    allDone = false;
                    break;
                }
            }

            if (allDone) {
                holder.rlList.setBackgroundResource(R.drawable.goals_btnlistdone);
            } else {
                holder.rlList.setBackgroundResource(R.drawable.goals_btnlistundone);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FirstListGoals.class);
                    intent.putExtra("goal", goal);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return goalsList != null ? goalsList.size() : 0;
    }

    public static class GoalsViewHolder extends RecyclerView.ViewHolder {

        TextView goalTextView;
        ImageButton deleteButton;
        RelativeLayout rlList;

        public GoalsViewHolder(@NonNull View itemView) {
            super(itemView);
            goalTextView = itemView.findViewById(R.id.tv_List);
            deleteButton = itemView.findViewById(R.id.btn_Delete);
            rlList = itemView.findViewById(R.id.rl_List1);
        }

        public void bind(String goalId, OnItemClickListener listener) {
            deleteButton.setOnClickListener(v -> {
                listener.onDeleteClick(goalId);
                Toast.makeText(itemView.getContext(), "Capaian berhasil dihapus", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
