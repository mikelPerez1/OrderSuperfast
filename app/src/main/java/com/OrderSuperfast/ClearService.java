package com.OrderSuperfast;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class ClearService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //Code here
        System.out.println("APP KILLED");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        SharedPreferences sharedPreferences = getSharedPreferences("logPedido", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("pedido");
        editor.commit();

        SharedPreferences sharedTakeAway=getSharedPreferences("takeAway",Context.MODE_PRIVATE);
        SharedPreferences.Editor editorTakeAway=sharedTakeAway.edit();
        editorTakeAway.remove("takeAwayActivado");
        editorTakeAway.apply();

        SharedPreferences sharedDevices= getSharedPreferences("devices",Context.MODE_PRIVATE);
        SharedPreferences.Editor deviceEditor=sharedDevices.edit();
        deviceEditor.remove("listaDispositivos");
        deviceEditor.apply();

        stopSelf();
    }
}
