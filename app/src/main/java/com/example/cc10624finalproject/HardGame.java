package com.example.cc10624finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HardGame extends AppCompatActivity {

    private Button cancelHardGame;
    private TextView txt_Timer;
    private Handler handler;
    private long startTimeMillis;
    private ImageButton[] imageButtons;
    private List<Integer> pairs;
    private ImageButton firstClicked;
    private ImageButton secondClicked;

    private int matchedPairsCount = 0;
    private int totalPairs;
    private boolean isTimerRunning;

    private int mistakes = 0;

    //Calculates score
    private int calculateScore(long elapsedTimeSeconds, int mistakes) {
        int timeScore;
        // Score based on time
        if (elapsedTimeSeconds <= 40) {
            timeScore = 10;
        } else if (elapsedTimeSeconds <= 70) {
            timeScore = 5;
        } else {
            timeScore = 1;
        }

        int mistakesScore;
        // Score based on mistakes
        if (mistakes <= 30) {
            mistakesScore = 15;
        } else if (mistakes <= 70) {
            mistakesScore = 10;
        } else {
            mistakesScore = 5;
        }

        // Combine time and mistakes scores
        return timeScore + mistakesScore;
    }

    private void checkForGameCompletion() {

        if (matchedPairsCount == totalPairs) {


            //Stops the timer then gets the time
            stopTimer();
            long elapsedTimeMillis = SystemClock.elapsedRealtime() - startTimeMillis;
            long elapsedTimeSeconds = elapsedTimeMillis / 1000;

            String difficulty = "Hard";
            int hardScore = calculateScore(elapsedTimeSeconds, mistakes);
            int time = (int) elapsedTimeSeconds;

            Score score = new Score(time, difficulty, hardScore);
            DBHandler dbHandler = new DBHandler(this);
            int userId = dbHandler.getCurrentUser(this);
            dbHandler.addScore(userId, score);

            //Says congrats to the congrats to the player
            showAlertDialog("Your time was: " + elapsedTimeSeconds + " Seconds");
        }
    }

    private void showAlertDialog(String message) {
        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);

        // Find the TextView in the custom layout
        TextView textViewMessage = dialogView.findViewById(R.id.txt_TimeFinished);
        textViewMessage.setText(message);

        // Build the AlertDialog with the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Finds the button
        Button resetGame = dialogView.findViewById(R.id.btn_PlayAgain);
        resetGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartGame();
                dialog.dismiss();
            }
        });

        Button quitGame = dialogView.findViewById(R.id.btn_QuitGame);
        quitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                Intent intent = new Intent(HardGame.this, Menu.class);
                startActivity(intent);

            }
        });
    }

    // Check if all pairs are matched
    private void onMatchFound() {

        matchedPairsCount++;
        checkForGameCompletion();
    }

    private void startTimer() {
        if (!isTimerRunning) {
            startTimeMillis = SystemClock.elapsedRealtime();
            handler = new Handler();
            handler.postDelayed(timerRunnable, 1000);
            isTimerRunning = true;
        }
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTimeMillis = SystemClock.elapsedRealtime();
            long elapsedTimeMillis = currentTimeMillis - startTimeMillis;

            // Convert milliseconds to minutes and seconds
            long minutes = (elapsedTimeMillis / 1000) / 60;
            long seconds = (elapsedTimeMillis / 1000) % 60;

            // Format the time and set it to the TextView
            String timerText = String.format("%02d:%02d", minutes, seconds);
            txt_Timer.setText(timerText);

            // Continue updating the timer
            handler.postDelayed(this, 1000);
        }
    };

    private void stopTimer() {
        if (isTimerRunning) {
            handler.removeCallbacks(timerRunnable);
            isTimerRunning = false;
        }
    }

    //Resets the game
    private void restartGame() {
        resetTimer();
        resetCards();
    }

    private void resetTimer() {
        stopTimer();
        txt_Timer.setText("00:00");
    }

    private void resetCards() {
        Collections.shuffle(pairs);


        for (int i = 0; i < imageButtons.length; i++) {
            final int index = i;
            imageButtons[i].setImageResource(R.drawable.ic_launcher_foreground);
            imageButtons[i].setEnabled(true);
            imageButtons[i].setAlpha(1.0f);
        }

        // Reset firstClicked and secondClicked
        firstClicked = null;
        secondClicked = null;

        //Resets scores
        matchedPairsCount = 0;
        totalPairs = pairs.size() / 2;
    }

    // Lets user check if they wanna cancel the game or not
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to quit?")
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        Intent intent = new Intent(HardGame.this, Menu.class);
                        startActivity(intent);

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_game);

        cancelHardGame = (Button)findViewById(R.id.btn_HardBack);
        //Initialize timer id

        txt_Timer = findViewById(R.id.txt_Timer);
        isTimerRunning = false;

        // Initialize imageButtons array
        imageButtons = new ImageButton[]{
                findViewById(R.id.imgbtn_n_1), findViewById(R.id.imgbtn_n_2), findViewById(R.id.imgbtn_n_3),
                findViewById(R.id.imgbtn_n_4), findViewById(R.id.imgbtn_n_5), findViewById(R.id.imgbtn_n_6),
                findViewById(R.id.imgbtn_n_7), findViewById(R.id.imgbtn_n_8), findViewById(R.id.imgbtn_n_9),
                findViewById(R.id.imgbtn_n_10), findViewById(R.id.imgbtn_n_11), findViewById(R.id.imgbtn_n_12),
                findViewById(R.id.imgbtn_n_13), findViewById(R.id.imgbtn_n_14), findViewById(R.id.imgbtn_n_15),
                findViewById(R.id.imgbtn_n_16), findViewById(R.id.imgbtn_n_17), findViewById(R.id.imgbtn_n_18),
                findViewById(R.id.imgbtn_n_19), findViewById(R.id.imgbtn_n_20), findViewById(R.id.imgbtn_n_21),
                findViewById(R.id.imgbtn_n_22), findViewById(R.id.imgbtn_n_23), findViewById(R.id.imgbtn_n_24),
                findViewById(R.id.imgbtn_n_25), findViewById(R.id.imgbtn_n_26), findViewById(R.id.imgbtn_n_27),
                findViewById(R.id.imgbtn_n_28)
        };

        // Create pairs of images
        pairs = new ArrayList<>(Arrays.asList(
                R.drawable.ic_anchor, R.drawable.ic_api, R.drawable.ic_brush, R.drawable.ic_bunny,
                R.drawable.ic_castle, R.drawable.ic_flower, R.drawable.ic_puzzle, R.drawable.ic_basketball,
                R.drawable.ic_beer, R.drawable.ic_coffee, R.drawable.ic_football, R.drawable.ic_rocket,
                R.drawable.ic_star, R.drawable.ic_volleyball
        ));
        pairs.addAll(pairs);
        Collections.shuffle(pairs);

        totalPairs = pairs.size() / 2;

        // Set OnClickListener for each ImageButton
        for (int i = 0; i < imageButtons.length; i++) {
            final int index = i;
            imageButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton clickedButton = imageButtons[index];
                    if (firstClicked == null) {
                        //Start timer

                        startTimer();

                        // First card clicked
                        firstClicked = clickedButton;
                        firstClicked.setImageResource(pairs.get(index));
                    } else if (secondClicked == null && firstClicked != clickedButton) {
                        // Second card clicked
                        secondClicked = clickedButton;
                        secondClicked.setImageResource(pairs.get(index));

                        // Compare images
                        if (pairs.get(Arrays.asList(imageButtons).indexOf(firstClicked))
                                .equals(pairs.get(Arrays.asList(imageButtons).indexOf(secondClicked)))) {
                            // Match found
                            firstClicked.setEnabled(false);
                            secondClicked.setEnabled(false);
                            firstClicked.setAlpha(0.3f);
                            secondClicked.setAlpha(0.3f);
                            firstClicked = null;
                            secondClicked = null;
                            onMatchFound();
                        } else {
                            // Mismatch
                            mistakes++;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    firstClicked.setImageResource(R.drawable.ic_launcher_foreground);
                                    secondClicked.setImageResource(R.drawable.ic_launcher_foreground);
                                    firstClicked = null;
                                    secondClicked = null;
                                }
                            }, 250); // Delay to show mismatched cards before flipping back
                        }
                    }
                }
            });
        }

        cancelHardGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();

            }
        });
    }
}