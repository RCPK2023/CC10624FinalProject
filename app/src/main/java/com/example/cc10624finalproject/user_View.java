package com.example.cc10624finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class user_View extends AppCompatActivity {

    private Button addPlayer;
    private EditText addPassword;
    private EditText editText_PlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        addPlayer = findViewById(R.id.btn_ConfirmPlayer);
        addPassword = findViewById(R.id.editText_Password);
        editText_PlayerName = findViewById(R.id.editText_PlayerName);

        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = editText_PlayerName.getText().toString().trim();
                String password = addPassword.getText().toString().trim();

                if (TextUtils.isEmpty(playerName) || TextUtils.isEmpty(password)) {
                    Toast.makeText(user_View.this, "Please add name and password", Toast.LENGTH_SHORT).show();
                } else {
                    DBHandler dbHandler = new DBHandler(user_View.this);

                    if (dbHandler.isExistingPlayer(playerName, password)) {
                        Toast.makeText(user_View.this, "Player already exists", Toast.LENGTH_SHORT).show();
                    } else {

                        User newUser = new User(playerName, password);
                        dbHandler.addUser(newUser);

                        Intent intent = new Intent(user_View.this, user_mainActivity.class);
                        startActivity(intent);

                    }
                }
            }
        });



    }


}
