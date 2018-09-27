package com.example.datpt.spacex;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ActivityPlay.NOTIFY_PREVIOUS)) {
            Toast.makeText(context, "NOTIFY_PREVIOUS", Toast.LENGTH_LONG).show();
        } else if (intent.getAction().equals(ActivityPlay.NOTIFY_PAUSE)) {
            Toast.makeText(context, "NOTIFY_PLAY", Toast.LENGTH_LONG).show();
        } else if (intent.getAction().equals(ActivityPlay.NOTIFY_NEXT)) {
            Toast.makeText(context, "NOTIFY_NEXT", Toast.LENGTH_LONG).show();
        }
    }
}
