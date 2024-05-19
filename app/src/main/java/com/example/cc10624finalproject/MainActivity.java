package com.example.cc10624finalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean isExistingPlayer = false;
    private Button playGame;
    private Button viewScore;
    private Button quitGame;
    private Button viewAccount;
    private TextView playerName;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DBHandler(this);

        playGame = findViewById(R.id.btn_PlayGame);
        viewScore = findViewById(R.id.btn_Score);
        quitGame = findViewById(R.id.btn_ExitGame);
        viewAccount = findViewById(R.id.btn_ViewAccount);
        playerName = findViewById(R.id.txt_userName);

        playGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExistingPlayer) {
                    Intent intent = new Intent(MainActivity.this, Menu.class);
                    startActivity(intent);
                } else {
                    showCreateAccountDialog();
                }
            }
        });

        viewScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExistingPlayer) {
                    Intent intent = new Intent(MainActivity.this, ViewScore.class);
                    startActivity(intent);
                } else {
                    showCreateAccountDialog();
                }
            }
        });

        quitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

        viewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExistingPlayer) {
                    Intent intent = new Intent(MainActivity.this, user_mainActivity.class);
                    startActivity(intent);
                } else {
                    showCreateAccountDialog();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkExistingPlayer();
    }

    private void checkExistingPlayer() {
        isExistingPlayer = dbHandler.hasExistingPlayer();
        if (isExistingPlayer) {
            int mostRecentUserId = dbHandler.getMostRecentUserId();
            dbHandler.setCurrentUser(this, mostRecentUserId);
            User currentUser = dbHandler.getUserById(mostRecentUserId);
            if (currentUser != null) {
                playerName.setText(currentUser.getName());
            }
            viewAccount.setVisibility(View.VISIBLE);
        } else {
            viewAccount.setVisibility(View.GONE);

            playerName.setText("");
            showCreateAccountDialog();
        }
    }


    private void showCreateAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please create an account first to view your score.")
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(MainActivity.this, user_View.class));
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
