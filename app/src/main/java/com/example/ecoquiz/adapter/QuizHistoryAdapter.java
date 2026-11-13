package com.example.ecoquiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoquiz.R;
import com.example.ecoquiz.model.QuizAttempt;

import java.util.List;

/**
 * Adapter for displaying previous Quiz Attempts in PerformanceActivity.
 */
public class QuizHistoryAdapter extends RecyclerView.Adapter<QuizHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<QuizAttempt> attemptList;

    public QuizHistoryAdapter(Context context, List<QuizAttempt> attemptList) {
        this.context = context;
        this.attemptList = attemptList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizAttempt attempt = attemptList.get(position);

        String diff = attempt.getDifficulty();
        holder.tvDiffBadge.setText(diff.toUpperCase());

        int color;
        if ("Easy".equalsIgnoreCase(diff)) {
            color = ContextCompat.getColor(context, R.color.difficulty_easy);
        } else if ("Medium".equalsIgnoreCase(diff)) {
            color = ContextCompat.getColor(context, R.color.difficulty_medium);
        } else {
            color = ContextCompat.getColor(context, R.color.difficulty_hard);
        }
        holder.tvDiffBadge.setTextColor(color);

        holder.tvScore.setText(attempt.getTotalScore() + " / 100");
        holder.tvDate.setText(attempt.getAttemptDate() != null ? attempt.getAttemptDate() : "");
        holder.tvStats.setText(attempt.getCorrectAnswers() + " Correct • " +
                attempt.getIncorrectAnswers() + " Wrong • " +
                attempt.getUnanswered() + " Missed");
    }

    @Override
    public int getItemCount() {
        return attemptList != null ? attemptList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDiffBadge, tvScore, tvDate, tvStats;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiffBadge = itemView.findViewById(R.id.tvHistoryDiffBadge);
            tvScore = itemView.findViewById(R.id.tvHistoryScore);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvStats = itemView.findViewById(R.id.tvHistoryStats);
        }
    }
}
