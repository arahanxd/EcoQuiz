package com.example.ecoquiz.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoquiz.R;
import com.example.ecoquiz.model.DifficultyItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * DifficultyPagerAdapter binds difficulty level cards into ViewPager2.
 */
public class DifficultyPagerAdapter extends RecyclerView.Adapter<DifficultyPagerAdapter.ViewHolder> {

    public interface OnDifficultyClickListener {
        void onPlayClicked(DifficultyItem item);
    }

    private final Context context;
    private final List<DifficultyItem> items;
    private final OnDifficultyClickListener listener;

    public DifficultyPagerAdapter(Context context, List<DifficultyItem> items, OnDifficultyClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_difficulty_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DifficultyItem item = items.get(position);

        holder.tvDiffBadge.setText(item.getTitle().toUpperCase());
        int color = ContextCompat.getColor(context, item.getColorResId());
        holder.tvDiffBadge.setTextColor(color);

        holder.tvDiffTitle.setText(item.getTitle() + " Level");
        holder.tvDiffSubtitle.setText(item.getSubtitle());
        holder.tvDiffTimer.setText(item.getTimeSeconds() + "s");
        holder.tvDiffQuestions.setText(String.valueOf(item.getQuestionsCount()));
        holder.tvDiffMaxScore.setText(item.getMaxScore() + " pts");

        holder.ivDiffIcon.setImageResource(item.getIconResId());
        holder.ivDiffIcon.setImageTintList(ColorStateList.valueOf(color));

        holder.btnPlayDifficulty.setBackgroundTintList(ColorStateList.valueOf(color));
        holder.btnPlayDifficulty.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDiffBadge, tvDiffTitle, tvDiffSubtitle;
        TextView tvDiffTimer, tvDiffQuestions, tvDiffMaxScore;
        ImageView ivDiffIcon;
        MaterialButton btnPlayDifficulty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiffBadge = itemView.findViewById(R.id.tvDiffBadge);
            tvDiffTitle = itemView.findViewById(R.id.tvDiffTitle);
            tvDiffSubtitle = itemView.findViewById(R.id.tvDiffSubtitle);
            tvDiffTimer = itemView.findViewById(R.id.tvDiffTimer);
            tvDiffQuestions = itemView.findViewById(R.id.tvDiffQuestions);
            tvDiffMaxScore = itemView.findViewById(R.id.tvDiffMaxScore);
            ivDiffIcon = itemView.findViewById(R.id.ivDiffIcon);
            btnPlayDifficulty = itemView.findViewById(R.id.btnPlayDifficulty);
        }
    }
}
