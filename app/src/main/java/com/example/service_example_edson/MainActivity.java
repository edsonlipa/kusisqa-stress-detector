package com.example.service_example_edson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button mIniciar, mParar;
    String TAG = "Main Activity";
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIniciar=findViewById(R.id.iniciarId);
        mParar=findViewById(R.id.pararId);
        txt=findViewById(R.id.textId);

        mIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, MyService.class);
                startService(intent);

            }
        });
        mParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, MyService.class);
                stopService(intent);
            }
        });
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(MyService.COUNTDOWN_BR));
        Log.i(TAG,"on remuse: Registered broadcast reciver");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        Log.i(TAG,"Unregistered broadcast receiver");
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(broadcastReceiver);

        }catch (Exception e){
            //receiver was probably already
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,MyService.class));
        Log.i(TAG,"Stoppped service");
        super.onDestroy();
    }
    private void updateGUI(Intent intent){
        if (intent.getExtras()!=null){
            long milliUntilFinished= intent.getLongExtra("countdown",30000);
            Log.i(TAG,"countdown second remaining"+ milliUntilFinished/1000);
            txt.setText(Long.toString(milliUntilFinished/1000));
            //txt.setText("actualizando...");
        }
    }
}