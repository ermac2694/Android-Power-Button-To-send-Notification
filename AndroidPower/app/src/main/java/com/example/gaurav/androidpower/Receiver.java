package com.example.gaurav.androidpower;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import java.util.Timer;

import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.ACTION_SCREEN_ON;

public class Receiver extends BroadcastReceiver {

    private boolean screenOff;
    private int count = 0;
    private Long timer;
    Timer tim = new Timer();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("jhfrhfrhfhrjh", action);
        if (action.equals(ACTION_SCREEN_OFF)) {
            screenOff = true;
        } else if (action.equals(ACTION_SCREEN_ON)) {
            //screenOff = false;
            count++;
        }
        if(count >= 2) {
            Intent j = new Intent(context, SendPanicData.class);
            j.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            j.putExtra("openfrom", "ButtonPress");
            context.startActivity(j);
        }
    }
}
