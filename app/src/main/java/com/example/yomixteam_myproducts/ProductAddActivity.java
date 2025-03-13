package com.example.yomixteam_myproducts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yomixteam_myproducts.DB_Helpers.ProductsDataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductAddActivity extends AppCompatActivity {
    ImageView btn_home;
    Button btn_save;
    EditText nameProduct;
    TextView dateProduct;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    int id = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProductsDataBaseHelper productsDataBaseHelper = new ProductsDataBaseHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);
        btn_home = findViewById(R.id.home);
        btn_save = findViewById(R.id.save);
        nameProduct = findViewById(R.id.name);
        dateProduct = findViewById(R.id.date);
        dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String name = "";
        String date = "";
        Intent intent_home = new Intent(this, MainScreenActivity.class);
        Intent intent_list = new Intent(this, ProductListActivity.class);
        String type_request = getIntent().getExtras().getString("type_request");
        if (type_request.equals("edit")) {
            name = getIntent().getStringExtra("name");
            date = getIntent().getStringExtra("date");
            id = getIntent().getIntExtra("id", -1);
        }
        nameProduct.setText(name);
        dateProduct.setText(date);
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateProduct.setText(dateFormatter.format(newDate.getTime()));
            }
        };
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent_home);
                finish();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pattern regexPattern = Pattern.compile("\\d{2}[.]\\d{2}[.]\\d{4}");
                Matcher matcher = regexPattern.matcher(dateProduct.getText().toString());
                if (nameProduct.getText().toString().equals("")){
                    nameProduct.setError("Заполните поле");
                    return;
                }
                if (dateProduct.getText().toString().equals("")){
                    dateProduct.setError("Заполните поле");
                    return;
                }
                if (type_request.equals("add"))
                    productsDataBaseHelper.addProduct(nameProduct.getText().toString(), dateProduct.getText().toString());
                else if (type_request.equals("edit") && id != -1)
                    productsDataBaseHelper.editProduct(nameProduct.getText().toString(), dateProduct.getText().toString(), String.valueOf(id));
                startActivity(intent_list);
                finish();
            }
        });
        dateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentDate = Calendar.getInstance();
                int year = currentDate.get(Calendar.YEAR);
                int month = currentDate.get(Calendar.MONTH);
                int day = currentDate.get(Calendar.DAY_OF_MONTH);

                if (!(dateProduct.getText() != null)) {
                    String[] date_from_EditText = dateProduct.getText().toString().split("\\.");
                    year = Integer.parseInt(date_from_EditText[0]);
                    month = Integer.parseInt(date_from_EditText[1]);
                    day = Integer.parseInt(date_from_EditText[2]);
                }


                DatePickerDialog datePickerDialog = new DatePickerDialog(ProductAddActivity.this, dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });
    }
}


