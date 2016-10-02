package com.example.gaurav.androidpower;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UpdateService extends Service {

    int mStartMode;
    private Receiver mReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        // register receiver that handles screen on and screen off logic
        Log.e(">>>>>>", "HardwareTriggerService CREATED.");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new Receiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e(">>>>>>", "HardwareTriggerService DESTROYED.");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        unregisterReceiver(mReceiver);
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags,int startId) {
//        boolean screenOn = intent.getBooleanExtra("screen_state", false);
//        if (!screenOn) {
//            Log.e("screenON", "Called");
//            Toast.makeText(getApplicationContext(), "Awake", Toast.LENGTH_LONG)
//                    .show();
//        } else {
//            Log.e("screenOFF", "Called");
//            // Toast.makeText(getApplicationContext(), "Sleep",
//            // Toast.LENGTH_LONG)
//            // .show();
//        }
//        return mStartMode;
//    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        // register receiver that handles screen on and screen off logic
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        BroadcastReceiver mReceiver = new Receiver();
//        registerReceiver(mReceiver, filter);
//    }
//
//    @Override
//    public void onStart(Intent intent, int startId) {
//        boolean screenOn = intent.getBooleanExtra("screen_state", false);
//        if (!screenOn) {
//            // your code
//            Toast.makeText(UpdateService.this, "Screen off", Toast.LENGTH_LONG).show();
//        } else {
//            // your code
//            Toast.makeText(UpdateService.this, "Screen On", Toast.LENGTH_LONG).show();
//        }
//    }
}
