package com.example.yomixteam_myproducts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.yomixteam_myproducts.DB_Helpers.ProductsDataBaseHelper;
import com.example.yomixteam_myproducts.DB_Helpers.PropertiesDataBaseHelper;

import java.util.Calendar;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    EditText daysForWarn, removeDays;
    TextView timeVerify;
    ImageView btn_home;
    Button deleteAllProducts, btn_save;
    PropertiesDataBaseHelper propertiesDB;
    ProductsDataBaseHelper productsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        propertiesDB = new PropertiesDataBaseHelper(this);
        productsDB = new ProductsDataBaseHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        daysForWarn = findViewById(R.id.warn);
        removeDays = findViewById(R.id.remove);
        timeVerify = findViewById(R.id.time);
        deleteAllProducts = findViewById(R.id.deleteAll);
        btn_save = findViewById(R.id.save);
        btn_home = findViewById(R.id.home);
        Intent intent = new Intent(this, MainScreenActivity.class);
        String warnDaysProperties = String.valueOf(getWarnForDaysFromProperties());
        String timeVerifyProperties = getTimeVerifyFromProperties();
        String removeDaysProperties = String.valueOf(getRemoveFromProperties());

        daysForWarn.setText(warnDaysProperties);
        timeVerify.setText(timeVerifyProperties);
        removeDays.setText(removeDaysProperties);


        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                finish();
            }
        });
        deleteAllProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productsDB.deleteAllProducts();
                Toast.makeText(getApplicationContext(), "Все продукты удалены", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }

        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String warnDaysFromEditText = daysForWarn.getText().toString();
                String timeVerifyFromEditText = timeVerify.getText().toString();
                String removeDaysFromEditText = removeDays.getText().toString();
                if (warnDaysFromEditText == null || warnDaysFromEditText.equals("")) {
                    daysForWarn.setError("Заполните поле");
                    return;
                }
                if (timeVerifyFromEditText == null || timeVerifyFromEditText.equals("")) {
                    timeVerify.setError("Заполните поле");
                    return;
                }
                if (removeDaysFromEditText == null || removeDaysFromEditText.equals("")) {
                    removeDays.setError("Заполните поле");
                    return;
                }
                if (!warnDaysProperties.equals(warnDaysFromEditText))
                    propertiesDB.editProperties("warnForDays", warnDaysFromEditText);
                if (!timeVerifyProperties.equals(timeVerifyFromEditText))
                    propertiesDB.editProperties("timeVerify", timeVerifyFromEditText);
                if (!removeDaysProperties.equals(removeDaysFromEditText))
                    propertiesDB.editProperties("removeAfter", removeDaysFromEditText);
                startActivity(intent);
                finish();
            }
        });
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                timeVerify.setText(time);
            }
        };
        timeVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] time = getTimeVerifyFromProperties().split(":");
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);

                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this, timeSetListener, hour, minute, true);
                timePickerDialog.show();
            }
        });


    }
    private int getWarnForDaysFromProperties() {
        Cursor cursor = propertiesDB.readProperties();
        int warnForDays = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String key = cursor.getString(0);
                String value = cursor.getString(1);

                if (key.equals("warnForDays")) {
                    warnForDays = Integer.parseInt(value);
                    break;
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return warnForDays;
    }
    private int getRemoveFromProperties() {
        Cursor cursor = propertiesDB.readProperties();
        int remove = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String key = cursor.getString(0);
                String value = cursor.getString(1);

                if (key.equals("removeAfter")) {
                    remove = Integer.parseInt(value);
                    break;
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return remove;
    }

    private String getTimeVerifyFromProperties() {
        Cursor cursor = propertiesDB.readProperties();
        String timeVerify = "";

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String key = cursor.getString(0);
                String value = cursor.getString(1);

                if (key.equals("timeVerify")) {
                    timeVerify = value;
                    break;
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return timeVerify;
    }
}