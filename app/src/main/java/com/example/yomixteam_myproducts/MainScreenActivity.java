package com.example.yomixteam_myproducts;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yomixteam_myproducts.DB_Helpers.NotificationService;

public class MainScreenActivity extends AppCompatActivity {

    ImageView btn_list, btn_add, btn_settings;
    private static final int REQUEST_CODE_IGNORE_BATTERY_OPTIMIZATIONS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && currentApiVersion >= 33) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        String packageName = getPackageName();
        if (powerManager != null && powerManager.isIgnoringBatteryOptimizations(packageName))
            requestIgnoreBatteryOptimizations();
        Intent intent_list = new Intent(this, ProductListActivity.class);
        Intent intent_add = new Intent(this, ProductAddActivity.class);
        Intent intent_settings = new Intent(this, SettingsActivity.class);

        startNotificationService();
        btn_list = findViewById(R.id.list);
        btn_add = findViewById(R.id.add);
        btn_settings = findViewById(R.id.settings);

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent_list);
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_add.putExtra("type_request", "add");
                startActivity(intent_add);
            }
        });
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent_settings);
            }
        });


    }

    public void startNotificationService() {
        Intent serviceIntent = new Intent(this, NotificationService.class);
        serviceIntent.putExtra("source", "Activity");
        startService(serviceIntent);
    }
    private ActivityResultLauncher<Intent> ignoreBatteryOptimizationsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    String packageName = getPackageName();
                    if (powerManager != null && powerManager.isIgnoringBatteryOptimizations(packageName)) {
                        Toast.makeText(this, "Спасибо! Приятного Использования", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Разрешение важно для работы приложения! Дайте его", Toast.LENGTH_SHORT).show();
                        requestIgnoreBatteryOptimizations();
                    }
                }
            }
    );


    private void requestIgnoreBatteryOptimizations() {
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            ignoreBatteryOptimizationsLauncher.launch(intent);
            Toast.makeText(this, "Разрешите для корретной работы уведомлений!", Toast.LENGTH_LONG).show();
        }
    }



}