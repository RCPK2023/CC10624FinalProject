package com.example.cc10624finalproject;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView timeTextView;
    TextView difficultyTextView;
    TextView scoreTextView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        timeTextView = itemView.findViewById(R.id.txt_time);
        difficultyTextView = itemView.findViewById(R.id.txt_difficulty);
        scoreTextView = itemView.findViewById(R.id.txt_score);
    }
}
