package com.example.projectthree_sunnynguyen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
            boolean ok = db.createUser(etUser.getText().toString(), etPass.getText().toString());
            Toast.makeText(this, ok ? "Account created" : "User exists", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            if (db.validateUser(etUser.getText().toString(), etPass.getText().toString())) {
                startActivity(new Intent(this, EventsGridActivity.class));
            } else {
                Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show();
            }
        });
    }
}