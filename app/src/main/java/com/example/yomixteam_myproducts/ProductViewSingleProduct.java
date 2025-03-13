package com.example.yomixteam_myproducts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yomixteam_myproducts.DB_Helpers.ProductsDataBaseHelper;

public class ProductViewSingleProduct extends AppCompatActivity {
    Button btn_edit;
    ImageView btn_back, recycle_bin;
    TextView name, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProductsDataBaseHelper productsDataBaseHelper = new ProductsDataBaseHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view_single_product);
        btn_edit = findViewById(R.id.edit);
        btn_back = findViewById(R.id.home);
        recycle_bin = findViewById(R.id.delete);
        name = findViewById(R.id.name);
        date = findViewById(R.id.date);


        int id = getIntent().getIntExtra("id", -1);
        String name_data = getIntent().getStringExtra("name");
        String date_data = getIntent().getStringExtra("date");
        Intent intent_list = new Intent(this, ProductListActivity.class);
        Intent intent_edit = new Intent(this, ProductAddActivity.class);
        name.setText(name_data);
        date.setText(date_data);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent_list);
                finish();
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_edit.putExtra("name", name_data);
                intent_edit.putExtra("date", date_data);
                intent_edit.putExtra("id", id);
                intent_edit.putExtra("type_request", "edit");
                startActivity(intent_edit);
                finish();
            }
        });
        recycle_bin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productsDataBaseHelper.deleteSingleProduct(String.valueOf(id));
                    Toast.makeText(getApplicationContext(), "Успешно удалено", Toast.LENGTH_SHORT).show();
                startActivity(intent_list);
                finish();
            }
        });

    }
}