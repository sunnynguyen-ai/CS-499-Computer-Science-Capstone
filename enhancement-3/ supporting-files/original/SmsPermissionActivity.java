package com.example.projectthree_sunnynguyen;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SmsPermissionActivity extends AppCompatActivity {
    private static final int REQ_SMS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_permission);

        findViewById(R.id.buttonAllowSMS).setOnClickListener(v ->
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQ_SMS));

        findViewById(R.id.buttonDenySMS).setOnClickListener(v -> finish());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] perms, @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, perms, results);
        if (requestCode == REQ_SMS) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}