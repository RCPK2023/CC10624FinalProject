package com.example.cc10624finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ViewScore extends AppCompatActivity {

    private Button btn_back;
    private List<Score> scores;
    private ListView listViewNormal;
    DBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_score);

        btn_back = (Button)findViewById(R.id.btn_back);

        listViewNormal = findViewById(R.id.lv_normalScore);
        dbHandler = new DBHandler(this);

        List<UserScore> scores = dbHandler.getTopScores();

        List<String> normalScoreStrings = new ArrayList<>();
        for (UserScore score : scores) {
            normalScoreStrings.add(score.toString());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, normalScoreStrings);

        listViewNormal.setAdapter(adapter);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewScore.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}