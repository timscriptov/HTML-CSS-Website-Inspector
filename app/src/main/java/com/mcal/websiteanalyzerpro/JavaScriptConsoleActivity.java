package com.mcal.websiteanalyzerpro;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class JavaScriptConsoleActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private static JavaScriptConsoleActivity activity;
    private String consoleContent = ""/*BuildConfig.FLAVOR*/;
    private EditText consoleInput;
    private TextView consoleText;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String finish = intent.getStringExtra("finish");
            if (finish == null) {
                String message = "ffff";
                message = intent.getStringExtra("message");
                if (message != null) {
                    consoleContent = message + "\n" + consoleContent;
                }
                consoleText.setText(consoleContent);
                try {
                    if (message == null) {
                        return;
                    }
                    if (message.equals("true")) {
                        setColor(consoleText, consoleContent, "true", Color.parseColor("#339933"));
                    } else {
                        setColor(consoleText, consoleContent, message, Color.parseColor("#cc0000"));
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            } else if (finish.equals("finish_activity")) {
                finish();
            }
        }
    };

    public static void finishActivity() {
        if (activity != null) {
            activity.finish();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_javascript_layout);
        consoleText = findViewById(R.id.consoleText);
        setSupportActionBar(findViewById(R.id.toolbar));
        activity = this;
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("sendJavascript"));
        consoleInput = findViewById(R.id.searchfield);
        consoleInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != 4 || consoleInput.getText() == null) {
                return false;
            }
            MainActivity.loadJavascript(consoleInput.getText().toString());
            return true;
        });
    }

    private void setColor(TextView view, String fulltext, String subtext, int color) {
        view.setText(fulltext, BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fulltext.indexOf(subtext);
        str.setSpan(new ForegroundColorSpan(color), i, subtext.length() + i, 33);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.console_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewInBrowser:
                MainActivity.loadJavascript(consoleInput.getText().toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        activity = null;
        super.onDestroy();
    }
}