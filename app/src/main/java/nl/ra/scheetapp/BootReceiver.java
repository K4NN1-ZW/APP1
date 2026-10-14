package nl.ra.scheetapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Start de scheet-service weer na het herstarten van de telefoon (als hij aan stond). */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) && MainActivity.isAan(context)) {
            context.startForegroundService(new Intent(context, ScheetService.class));
        }
    }
}
