package com.example.yomixteam_myproducts.DB_Helpers;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.yomixteam_myproducts.ProductListActivity;
import com.example.yomixteam_myproducts.R;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {
    private PropertiesDataBaseHelper propertiesDB;
    private ProductsDataBaseHelper productsDB;
    private NotificationChannel channel;
    private NotificationManager notificationManager;

    private static int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "yomixteam_myproducts";
    private static final String CHANNEL_NAME = "YomixTeam_MyProducts";


    @Override
    public void onCreate() {
        super.onCreate();
        propertiesDB = new PropertiesDataBaseHelper(getApplicationContext());
        productsDB = new ProductsDataBaseHelper(getApplicationContext());
        channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int warnForDays = getWarnForDaysFromProperties();
        updateServiceAlarms(this);
        String source = "Activity";
        try {
            source = intent.getStringExtra("source");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (source == null)
            checkProductExpiryDates(warnForDays, getRemoveFromProperties());

        return START_STICKY;
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
        int remove = 5;

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

    private void checkProductExpiryDates(int warnForDays, int removeFromDB) {
        Cursor cursor = productsDB.readProducts();

        new Thread(() -> {
            for (int i = 0; cursor.moveToNext(); i++) {
                if (i == 0)
                    cursor.moveToFirst();
                int id = cursor.getInt(0);
                String productName = cursor.getString(1);
                String expiredDate = cursor.getString(2);
                int days_exp = getExpiryApproachingDays(expiredDate, warnForDays);
                if (days_exp <= warnForDays){
                    if (days_exp <= 0) {
                        String title = "Истечение срока годности";
                        String description = "Срок годности продукта " + productName.toUpperCase() + " истёк";
                        showNotification(title, description);
                        if (days_exp < removeFromDB) {
                            productsDB.deleteSingleProduct(String.valueOf(id));
                        }
                    } else if (days_exp == 1) {
                        String title = "Предупреждение об истечении срока годности";
                        String description = "Срок годности продукта " + productName.toUpperCase() + " истекает сегодня";
                        showNotification(title, description);
                    } else {
                        String title = "Предупреждение об истечении срока годности";
                        String description = "Срок годности продукта " + productName.toUpperCase() + " истекает через " + days_exp + " " + getDayWord(String.valueOf(days_exp));
                        showNotification(title, description);
                    }
                NOTIFICATION_ID++;
            }

            }

            if (cursor != null) {
                cursor.close();
            }
        }).start();
    }



    private int getExpiryApproachingDays(String expiryDate, int warnForDays) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date currentDate = new Date();
        try {
            Date expiryDateDate = sdf.parse(expiryDate);
            int diffInDays = 0;
            if (expiryDate != null)
                diffInDays = (int) (TimeUnit.MILLISECONDS.toDays(expiryDateDate.getTime() - currentDate.getTime()) + 1);
            return diffInDays;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Integer.MAX_VALUE;
    }

    private void showNotification(String title, String description) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.round_icon)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent());

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void setNextServiceAlarm(Context context, long triggerTimeMillis) {
        cancelAllServiceAlarms(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra("source", "Notification");
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
        }
    }

    private void cancelAllServiceAlarms(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }


    public void updateServiceAlarms(Context context) {
        String timeVerify = getTimeVerifyFromProperties() + ":00:000";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
        try {
            Calendar calendar = Calendar.getInstance();
            Date currentTime = sdf.parse(sdf.format(calendar.getTime()));
            Date verifyTime = sdf.parse(timeVerify);

            calendar.setTime(verifyTime);
            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

            if (currentTime.after(verifyTime)) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            long triggerTimeMillis = calendar.getTimeInMillis();
            setNextServiceAlarm(context, triggerTimeMillis);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
