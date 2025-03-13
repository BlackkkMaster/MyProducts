package com.example.yomixteam_myproducts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.yomixteam_myproducts.DB_Helpers.NotificationService;

public class BootBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service_intent = new Intent(context, NotificationService.class);
        service_intent.putExtra("source", "Activity");
        context.startService(service_intent);
    }
}
