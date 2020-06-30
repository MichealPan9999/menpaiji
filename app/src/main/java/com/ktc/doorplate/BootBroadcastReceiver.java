package com.ktc.doorplate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent intentMain = new Intent(context, MainActivity.class);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentMain);
        }
    }
}
