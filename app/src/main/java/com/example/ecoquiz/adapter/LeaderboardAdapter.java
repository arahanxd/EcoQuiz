package com.example.ecoquiz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoquiz.R;
import com.example.ecoquiz.model.LeaderboardEntry;

import java.util.List;

/**
 * LeaderboardAdapter for presenting top 10 users ranked by highest score descending.
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final Context context;
    private final List<LeaderboardEntry> list;

    public LeaderboardAdapter(Context context, List<LeaderboardEntry> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardEntry entry = list.get(position);

        holder.tvRank.setText(String.valueOf(entry.getRank()));
        holder.tvUsername.setText("@" + entry.getUsername());
        holder.tvScore.setText(entry.getHighestScore() + " pts");
        holder.tvDate.setText("Best Attempt: " + (entry.getLatestDate() != null ? entry.getLatestDate() : "N/A"));

        // Highlight top 3 ranks
        if (entry.getRank() == 1) {
            holder.tvRank.setBackgroundResource(R.drawable.bg_option_badge);
            holder.tvRank.setTextColor(ContextCompat.getColor(context, R.color.accent_gold_dark));
            holder.tvScore.setTextColor(ContextCompat.getColor(context, R.color.accent_gold_dark));
        } else if (entry.getRank() == 2) {
            holder.tvRank.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            holder.tvScore.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        } else if (entry.getRank() == 3) {
            holder.tvRank.setTextColor(ContextCompat.getColor(context, R.color.difficulty_medium));
            holder.tvScore.setTextColor(ContextCompat.getColor(context, R.color.difficulty_medium));
        } else {
            holder.tvRank.setTextColor(ContextCompat.getColor(context, R.color.text_muted));
            holder.tvScore.setTextColor(ContextCompat.getColor(context, R.color.primary_forest_green));
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvUsername, tvScore, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvLeaderRank);
            tvUsername = itemView.findViewById(R.id.tvLeaderUsername);
            tvScore = itemView.findViewById(R.id.tvLeaderScore);
            tvDate = itemView.findViewById(R.id.tvLeaderDate);
        }
    }
}
