package com.faah.sound;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private SoundPool pool;
    private int sound, stream;
    private boolean ready;
    private int dp(float value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color); t.setGravity(Gravity.CENTER);
        return t;
    }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(20,20,20));
        getWindow().setNavigationBarColor(Color.rgb(20,20,20));
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER);
        root.setPadding(dp(20),dp(20),dp(20),dp(20)); root.setBackgroundColor(0xff141414);
        TextView title = text("FAAH!", 36, Color.WHITE); title.setTypeface(null, Typeface.BOLD); root.addView(title);
        root.addView(text("Sound Button", 16, 0xffaaaaaa));
        TextView button = text("FAAH!", 36, Color.WHITE); button.setTypeface(null, Typeface.BOLD);
        GradientDrawable bg = new GradientDrawable(); bg.setShape(GradientDrawable.OVAL); bg.setColor(0xffdf2020); bg.setStroke(dp(3),0xffff5555);
        button.setBackground(bg); button.setElevation(dp(10)); button.setClickable(true); button.setFocusable(true);
        int diameter = Math.min(dp(230), getResources().getDisplayMetrics().widthPixels - dp(48));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(diameter,diameter); params.setMargins(0,dp(42),0,dp(28)); root.addView(button,params);
        TextView status = text("Memuat suara...", 14, 0xffaaaaaa); root.addView(status);
        setContentView(root);
        pool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).build();
        pool.setOnLoadCompleteListener((p,id,result) -> runOnUiThread(() -> { ready = result == 0; status.setText(ready ? "TAP TO PLAY" : "Gagal memuat suara"); }));
        sound = pool.load(this,R.raw.faah,1);
        button.setOnClickListener(v -> {
            if (!ready || pool == null) return;
            if (stream != 0) pool.stop(stream);
            stream = pool.play(sound,1f,1f,1,0,1f);
            v.animate().cancel(); v.setScaleX(.92f); v.setScaleY(.92f); v.animate().scaleX(1f).scaleY(1f).setDuration(130).start();
        });
    }
    @Override protected void onPause() { super.onPause(); if(pool != null && stream != 0) pool.stop(stream); }
    @Override protected void onDestroy() { if(pool != null) { pool.release(); pool = null; } super.onDestroy(); }
}
