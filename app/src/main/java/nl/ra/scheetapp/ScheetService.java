package nl.ra.scheetapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

/**
 * Draait op de achtergrond en laat een scheet horen wanneer:
 *  - het scherm aangaat (telefoon oppakken / aanzetten), of
 *  - de telefoon stil lag en daarna opgepakt wordt (bewegingssensor).
 */
public class ScheetService extends Service implements SensorEventListener {

    private static final String KANAAL = "scheet_kanaal";
    private static final long PAUZE_MS = 4000;   // minimaal 4 sec tussen scheten
    private static final long STIL_MS = 2000;    // zo lang stil = "ligt neer"
    private static final float DREMPEL = 1.8f;   // beweging in m/s² boven zwaartekracht

    private SensorManager sensorManager;
    private long laatsteScheet = 0;
    private long stilSinds = 0;
    private boolean lagStil = false;

    private final BroadcastReceiver schermReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) speel();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        maakKanaal();

        Intent open = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, open, PendingIntent.FLAG_IMMUTABLE);
        Notification n = new Notification.Builder(this, KANAAL)
                .setContentTitle("Scheet App is actief 💨")
                .setContentText("Pak je telefoon op...")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pi)
                .setOngoing(true)
                .build();

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(1, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(1, n);
        }

        registerReceiver(schermReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (acc != null) {
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
        }
        stilSinds = SystemClock.elapsedRealtime();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        float x = e.values[0], y = e.values[1], z = e.values[2];
        float beweging = Math.abs((float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH);
        long nu = SystemClock.elapsedRealtime();

        if (beweging < 0.4f) {
            if (nu - stilSinds > STIL_MS) lagStil = true;
        } else {
            stilSinds = nu;
            if (lagStil && beweging > DREMPEL) {
                lagStil = false;
                speel();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    private void speel() {
        long nu = SystemClock.elapsedRealtime();
        if (nu - laatsteScheet < PAUZE_MS) return;
        laatsteScheet = nu;
        MediaPlayer mp = MediaPlayer.create(this, R.raw.scheet);
        if (mp == null) return;
        mp.setOnCompletionListener(MediaPlayer::release);
        mp.start();
    }

    private void maakKanaal() {
        NotificationChannel c = new NotificationChannel(KANAAL, "Scheet App", NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(c);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(schermReceiver);
        if (sensorManager != null) sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
