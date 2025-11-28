package com.example.projectthree_sunnynguyen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity handles user authentication (login/registration).
 * 
 * NEW: After successful login, the user is taken to EventsGridActivity
 * where cloud sync is automatically triggered.
 */
public class MainActivity extends AppCompatActivity {
    private EditText etUser, etPass;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        etUser = findViewById(R.id.editTextUsername);
        etPass = findViewById(R.id.editTextPassword);

        findViewById(R.id.buttonCreateAccount).setOnClickListener(v -> {
            String username = etUser.getText().toString().trim();
            String password = etPass.getText().toString().trim();
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            boolean ok = db.createUser(username, password);
            Toast.makeText(this, ok ? "Account created" : "User exists", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            String username = etUser.getText().toString().trim();
            String password = etPass.getText().toString().trim();
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (db.validateUser(username, password)) {
                // NEW: On successful login, navigate to EventsGridActivity
                // Cloud sync will be triggered automatically in EventsGridActivity.onCreate()
                Intent intent = new Intent(this, EventsGridActivity.class);
                startActivity(intent);
                finish(); // Prevent going back to login screen
            } else {
                Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
