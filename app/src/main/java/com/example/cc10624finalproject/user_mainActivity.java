package com.example.cc10624finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class user_mainActivity extends AppCompatActivity {

    private TextView playerID;
    private EditText playerNameEditText;
    private EditText playerPasswordEditText;
    private ListView playerBestScoreListView;
    private Button backButton;
    private Button editPlayerButton;
    private Button deleteAccountButton;
    private Button switchAccountButton;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        playerNameEditText = findViewById(R.id.editText_EditPlayerName);
        playerPasswordEditText = findViewById(R.id.editText_EditPlayerPassword);
        playerID = findViewById(R.id.txt_UserID);

        playerBestScoreListView = findViewById(R.id.lv_PlayerBestScore);
        backButton = findViewById(R.id.btn_backPlayer);
        editPlayerButton = findViewById(R.id.btn_EditPlayer);
        deleteAccountButton = findViewById(R.id.btn_DeleteAccount);
        switchAccountButton = findViewById(R.id.btn_SwitchAccount);

        DBHandler dbHandler = new DBHandler(this);

        if (dbHandler.hasExistingPlayer()) {

            userId = dbHandler.getCurrentUser(this);
            User currentUser = dbHandler.getUserById(userId);

            if (currentUser != null) {

                playerID.setText(String.valueOf(currentUser.getUserId()));
                playerNameEditText.setText(currentUser.getName());
                playerPasswordEditText.setText(currentUser.getPassword());
            } else {

                List<User> allUsers = dbHandler.getAllUsers();
                if (!allUsers.isEmpty()) {

                    User firstUser = allUsers.get(0);
                    userId = firstUser.getUserId();

                    playerID.setText(String.valueOf(firstUser.getUserId()));
                    playerNameEditText.setText(firstUser.getName());
                    playerPasswordEditText.setText(firstUser.getPassword());
                } else {

                    Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } else {

            Toast.makeText(this, "No existing players", Toast.LENGTH_SHORT).show();
            finish();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(user_mainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        editPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = playerNameEditText.getText().toString().trim();
                String password = playerPasswordEditText.getText().toString().trim();

                if (!playerName.isEmpty() && !password.isEmpty()) {

                    User updatedUser = new User(userId, playerName, password);
                    dbHandler.updateUser(updatedUser);

                    Toast.makeText(user_mainActivity.this, "User updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(user_mainActivity.this, "Please add name and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(user_mainActivity.this);
                builder.setTitle("Delete Account");
                builder.setMessage("Are you sure you want to delete this account?");
                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHandler.deleteUser(userId);
                        finish();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        switchAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(user_mainActivity.this, user_viewAccounts.class);
                startActivity(intent);
            }
        });

        List<Score> playerBestScores = dbHandler.getScorePlayer(userId);



        List<String> scoreStrings = new ArrayList<>();
        for (Score score : playerBestScores) {

            scoreStrings.add(score.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scoreStrings);
        playerBestScoreListView.setAdapter(adapter);
    }
}
