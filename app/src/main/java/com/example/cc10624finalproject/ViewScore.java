package com.example.cc10624finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class ViewScore extends AppCompatActivity {

    private Button btn_back;

    private RecyclerView recyclerView;
    private myAdapter adapter;
    private List<Score> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_score);

        btn_back = (Button)findViewById(R.id.btn_back);

        //Recycler View for Normal Difficulty
        RecyclerView recyclerView = findViewById(R.id.recycleview_ScoreNormal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DBHandler dbHandler = new DBHandler(this);
        scores = dbHandler.getAllScores();

        adapter = new myAdapter(this, scores);
        recyclerView.setAdapter(adapter);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewScore.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}