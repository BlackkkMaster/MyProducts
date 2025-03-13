package com.example.yomixteam_myproducts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yomixteam_myproducts.DB_Helpers.ProductsDataBaseHelper;
import com.example.yomixteam_myproducts.DB_Helpers.PropertiesDataBaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ProductListActivity extends AppCompatActivity {
    ImageView btn_home, btn_add;
    ListView list_products;
    int time_for_warn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        ProductsDataBaseHelper productsDataBaseHelper = new ProductsDataBaseHelper(this);
        PropertiesDataBaseHelper propertiesDataBaseHelper = new PropertiesDataBaseHelper(this);
        Intent intent_home = new Intent(this, MainScreenActivity.class);
        Intent intent_add = new Intent(this, ProductAddActivity.class);
        Intent intent_view = new Intent(this, ProductViewSingleProduct.class);
        btn_home = findViewById(R.id.home);
        btn_add = findViewById(R.id.add);
        list_products = findViewById(R.id.listProducts);

        Cursor cursor_properties = propertiesDataBaseHelper.readProperties();
        if (cursor_properties != null && cursor_properties.moveToFirst()){
            time_for_warn = Integer.parseInt(cursor_properties.getString(1));
        }
        if (cursor_properties != null)
            cursor_properties.close();
        Cursor cursor_products = productsDataBaseHelper.readProducts();
        int count = cursor_products.getCount();
        int[] ids = new int[count];
        String[] names = new String[count];
        String[] dates = new String[count];
        for (int i = 0; cursor_products.moveToNext(); i++) {
            if (i == 0)
                cursor_products.moveToFirst();
            ids[i] = cursor_products.getInt(0);
            names[i] = cursor_products.getString(1);
            dates[i] = cursor_products.getString(2);
        }
        if (cursor_products != null)
            cursor_products.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_view_custom, R.id.textViewTitle, names){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView titleTextView = view.findViewById(R.id.textViewTitle);
                TextView descriptionTextView = view.findViewById(R.id.textViewDescription);
                ImageView warn = view.findViewById(R.id.warn);
                titleTextView.setText(names[position]);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                Date currentDate = new Date();
                try {
                    Date expiryDate = sdf.parse(dates[position]);
                    long diffInDays = 0;
                    if (expiryDate != null)
                        diffInDays = TimeUnit.MILLISECONDS.toDays(expiryDate.getTime() - currentDate.getTime()) + 1;
                    if (diffInDays <= 0) {
                        descriptionTextView.setText("Срок годности истёк");
                        descriptionTextView.setTextColor(Color.rgb(153, 0, 0));
                        warn.setImageResource(R.drawable.dangerous);


                    } else if (diffInDays==1) {
                        descriptionTextView.setText("Срок годности истекает сегодня");
                        descriptionTextView.setTextColor(Color.rgb(230, 0, 51));
                        warn.setImageResource(R.drawable.warn);
                    } else if (diffInDays <= time_for_warn) {
                        descriptionTextView.setText("Срок годности истекает через " + diffInDays + " " + getDayWord(String.valueOf(diffInDays)));
                        descriptionTextView.setTextColor(Color.rgb(255, 102, 0));
                    } else {
                        descriptionTextView.setText("Срок годности истекает через " + diffInDays + " " + getDayWord(String.valueOf(diffInDays)));
                        descriptionTextView.setTextColor(Color.rgb(102, 204, 0));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return view;
            }
        };
        list_products.setAdapter(adapter);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent_home);
                finish();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_add.putExtra("type_request", "add");
                startActivity(intent_add);
                finish();
            }
        });
        list_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent_view.putExtra("name", names[position]);
                intent_view.putExtra("date", dates[position]);
                intent_view.putExtra("id", ids[position]);
                startActivity(intent_view);
            }
        });
    }
    public String getDayWord(String days) {
        int day = Character.getNumericValue(days.charAt(days.length() - 1));
        if (day == 1)
            return "день";
        else if (day > 1 && day < 5)
            return "дня";
        return "дней";
    }
}