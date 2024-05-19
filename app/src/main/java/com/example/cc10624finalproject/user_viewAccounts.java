package com.example.cc10624finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class user_viewAccounts extends AppCompatActivity {

    private ListView listView;
    private Button backButton;
    private DBHandler dbHandler;
    private Button addAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_accounts);

        listView = findViewById(R.id.lv_viewAccounts);
        backButton = findViewById(R.id.btn_ViewAccountBack);
        dbHandler = new DBHandler(this);

        addAccount = findViewById(R.id.btn_AddAccount);

        List<String> userNames = dbHandler.getAllUserNames();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
        listView.setAdapter(adapter);

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(user_viewAccounts.this, user_mainActivity.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUserName = userNames.get(position);
            showConfirmUserDialog(selectedUserName);
        });

        addAccount.setOnClickListener(view -> {
            Intent intent = new Intent(user_viewAccounts.this, user_View.class);
            startActivity(intent);
        });
    }

    private void showConfirmUserDialog(String userName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirm_user, null);
        builder.setView(dialogView);

        String[] parts = userName.split(":");
        String actualUserName = parts.length > 1 ? parts[1].trim() : userName.trim();

        EditText editTextUserName = dialogView.findViewById(R.id.editText_SelectUsername);
        EditText editTextPassword = dialogView.findViewById(R.id.editText_SelectPassword);
        Button buttonConfirm = dialogView.findViewById(R.id.btn_ConfirmPlayerSwitch);
        Button buttonBack = dialogView.findViewById(R.id.btn_ConfirmBack);

        editTextUserName.setText(actualUserName);
        editTextUserName.setEnabled(false);

        AlertDialog dialog = builder.create();

        buttonConfirm.setOnClickListener(v -> {
            String password = editTextPassword.getText().toString().trim();
            if (password.isEmpty()) {
                Toast.makeText(user_viewAccounts.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                int userId = dbHandler.getUserIdByPlayerNameAndPassword(actualUserName, password);

                if (userId != -1) {
                    dbHandler.setCurrentUser(user_viewAccounts.this, userId);
                    Intent intent = new Intent(user_viewAccounts.this, user_mainActivity.class);

                    startActivity(intent);

                    dialog.dismiss();

                } else {
                    Toast.makeText(user_viewAccounts.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonBack.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}
