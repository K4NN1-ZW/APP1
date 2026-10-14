package nl.ra.scheetapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Button aanUit;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(48, 48, 48, 48);

        TextView titel = new TextView(this);
        titel.setText("💨 Scheet App");
        titel.setTextSize(32);
        titel.setGravity(Gravity.CENTER);

        status = new TextView(this);
        status.setTextSize(18);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 32, 0, 32);

        aanUit = new Button(this);
        aanUit.setTextSize(20);
        aanUit.setOnClickListener(v -> wissel());

        Button test = new Button(this);
        test.setText("Test het geluid");
        test.setOnClickListener(v -> {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.scheet);
            mp.setOnCompletionListener(MediaPlayer::release);
            mp.start();
        });

        layout.addView(titel);
        layout.addView(status);
        layout.addView(aanUit);
        layout.addView(test);
        setContentView(layout);

        if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        if (isAan(this)) startService();
        updateUi();
    }

    static boolean isAan(android.content.Context c) {
        return c.getSharedPreferences("scheet", MODE_PRIVATE).getBoolean("aan", false);
    }

    private void wissel() {
        boolean nieuw = !isAan(this);
        SharedPreferences.Editor e = getSharedPreferences("scheet", MODE_PRIVATE).edit();
        e.putBoolean("aan", nieuw).apply();
        if (nieuw) startService();
        else stopService(new Intent(this, ScheetService.class));
        updateUi();
    }

    private void startService() {
        startForegroundService(new Intent(this, ScheetService.class));
    }

    private void updateUi() {
        boolean aan = isAan(this);
        status.setText(aan
                ? "Actief! Pak je telefoon op of zet het scherm aan... 💨"
                : "Uitgeschakeld");
        aanUit.setText(aan ? "Uitzetten" : "Aanzetten");
    }
}
