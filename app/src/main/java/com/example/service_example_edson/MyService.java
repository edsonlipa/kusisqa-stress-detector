package com.example.service_example_edson;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    CountDownTimer countDownTimer = null;
    private String TAG = "timerService";
    public static final String COUNTDOWN_BR = "com.example.service_example_edson";
    Intent intent = new Intent(COUNTDOWN_BR);
    public MyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "MyService: staring...");

        countDownTimer= new CountDownTimer( 30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG,"om tick seconds remaining: "+millisUntilFinished);
                intent.putExtra("countdown",millisUntilFinished);
                sendBroadcast(intent);
            }

            @Override
            public void onFinish() {

            }

        };

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Toast.makeText(this, "Se ha iniciado el servicio", Toast.LENGTH_SHORT).show();

        countDownTimer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        Toast.makeText(this, "El servicio se ha detenido", Toast.LENGTH_SHORT).show();
        countDownTimer.cancel();
        super.onDestroy();

    }
}