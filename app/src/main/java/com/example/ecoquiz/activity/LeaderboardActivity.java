package com.example.ecoquiz.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoquiz.R;
import com.example.ecoquiz.adapter.LeaderboardAdapter;
import com.example.ecoquiz.database.DatabaseHelper;
import com.example.ecoquiz.model.LeaderboardEntry;

import java.util.List;

/**
 * LeaderboardActivity: Screen 8
 * Features:
 * - Local device leaderboard displaying Top 10 users based on highest score descending
 * - Tie-breaker: earliest attempt date / username
 * - Clean visual hierarchy highlighting Gold, Silver, Bronze ranks
 * - Explicit notice indicating it is a local offline device leaderboard
 */
public class LeaderboardActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RecyclerView rvLeaderboard;
    private TextView tvEmptyLeaderboard;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        databaseHelper = new DatabaseHelper(this);

        ivBack = findViewById(R.id.ivLeaderBack);
        rvLeaderboard = findViewById(R.id.rvLeaderboard);
        tvEmptyLeaderboard = findViewById(R.id.tvEmptyLeaderboard);

        ivBack.setOnClickListener(v -> finish());

        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        List<LeaderboardEntry> list = databaseHelper.getTopLeaderboard();
        if (list == null || list.isEmpty()) {
            tvEmptyLeaderboard.setVisibility(View.VISIBLE);
            rvLeaderboard.setVisibility(View.GONE);
        } else {
            tvEmptyLeaderboard.setVisibility(View.GONE);
            rvLeaderboard.setVisibility(View.VISIBLE);
            LeaderboardAdapter adapter = new LeaderboardAdapter(this, list);
            rvLeaderboard.setAdapter(adapter);
        }
    }
}
