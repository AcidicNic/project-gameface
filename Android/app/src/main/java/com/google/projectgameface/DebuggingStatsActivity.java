package com.google.projectgameface;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class DebuggingStatsActivity extends AppCompatActivity {

    private static final String TAG = "DebuggingStats"; // TODO: remove unnecessary "Activity" from the end of the tags.

    private TextView wpmLatestAvgTxt;
    private TextView wpmAvgTxt;
    private TextView wordsPerPhraseAvgTxt;
    private TextView swipeDurationAvgTxt;
//    private TextView phraseLengthAvgTxt;

    private float wpmLatestAvg;
    private float wpmAvg;
    private float wordsPerPhraseAvg;
    private float swipeDurationAvg;
//    private float phraseLengthAvg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debugging_stats);
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        String profileName = ProfileManager.getCurrentProfile(this);
        SharedPreferences preferences = getSharedPreferences(profileName, Context.MODE_PRIVATE);

        // actionbar setup
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Debugging Stats");

        // textviews
        wpmLatestAvgTxt = findViewById(R.id.wpmLatestAvg);
        wpmAvgTxt = findViewById(R.id.wpmAvg);
        wordsPerPhraseAvgTxt = findViewById(R.id.wordsPerPhraseAvg);
        swipeDurationAvgTxt = findViewById(R.id.swipeDurationAvg);
//        phraseLengthAvgTxt = findViewById(R.id.phraseLengthAvg);

        // get stats from shared preferences
        wpmLatestAvg = preferences.getFloat("wpmLatestAvg", 0);
        wpmAvg = preferences.getFloat("wpmAvg", 0);
        wordsPerPhraseAvg = preferences.getFloat("wordsPerPhraseAvg", 0);
        swipeDurationAvg = preferences.getFloat("swipeDurationAvg", 0);
//        phraseLengthAvg = preferences.getFloat("phraseLengthAvg", 0);

        // set textviews
        wpmLatestAvgTxt.setText(String.format("Average words per minute of latest phrase: %.1f words/min", wpmLatestAvg));
        wpmAvgTxt.setText(String.format("Average words per minute:  %.1f words/min", wpmAvg));
        wordsPerPhraseAvgTxt.setText(String.format("Average words per phrase:  %.1f words/phrase", wordsPerPhraseAvg));
        swipeDurationAvgTxt.setText(String.format("Average swipe duration:  %.0f ms", swipeDurationAvg));
//        phraseLengthAvgTxt.setText("Average words per phrase: " + phraseLengthAvg);

        // btn onclicks
        findViewById(R.id.refreshBtn).setOnClickListener(v -> {
            refreshStats();
        });
        findViewById(R.id.resetBtn).setOnClickListener(v -> {
            resetStats();
        });
    }

    private void sendValueToService(String configName, float value) {
        String profileName = ProfileManager.getCurrentProfile(this);
        SharedPreferences preferences = getSharedPreferences(profileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(configName, value);
        editor.apply();
        Intent intent = new Intent("LOAD_SHARED_CONFIG_BASIC");
        intent.putExtra("configName", configName);
        sendBroadcast(intent);
    }

    private void resetStats() {
        sendValueToService("wpmLatestAvg", 0);
        sendValueToService("wpmAvg", 0);
        sendValueToService("wordsPerPhraseAvg", 0);
        sendValueToService("swipeDurationAvg", 0);
        sendBroadcast(new Intent("RESET_DEBUGGING_STATS"));
        refreshStats();
    }

    private void refreshStats() {
        String profileName = ProfileManager.getCurrentProfile(this);
        SharedPreferences preferences = getSharedPreferences(profileName, Context.MODE_PRIVATE);
        // get stats from shared preferences
        wpmLatestAvg = preferences.getFloat("wpmLatestAvg", 0);
        wpmAvg = preferences.getFloat("wpmAvg", 0);
        wordsPerPhraseAvg = preferences.getFloat("wordsPerPhraseAvg", 0);
        swipeDurationAvg = preferences.getFloat("swipeDurationAvg", 0);
//        phraseLengthAvg = preferences.getFloat("phraseLengthAvg", 0);

        // set textviews
        wpmLatestAvgTxt.setText(String.format("Average words per minute of latest phrase: %.1f words/min", wpmLatestAvg));
        wpmAvgTxt.setText(String.format("Average words per minute:  %.1f words/min", wpmAvg));
        wordsPerPhraseAvgTxt.setText(String.format("Average words per phrase:  %.1f words/phrase", wordsPerPhraseAvg));
        swipeDurationAvgTxt.setText(String.format("Average swipe duration:  %.1f ms", swipeDurationAvg));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Make back button work as back action in device's navigation.
     * @param item The menu item that was selected.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}