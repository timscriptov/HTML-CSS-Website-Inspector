package com.mcal.websiteanalyzerpro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppRater {
    private static final String APP_PNAME = "web.dassem.websiteanalyzerpro";
    private static final String APP_TITLE = "HTML Website Inspector Pro";
    private static final int DAYS_UNTIL_PROMPT = 1;
    private static final int LAUNCHES_UNTIL_PROMPT = 5;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (!prefs.getBoolean("dontshowagain", false)) {
            Editor editor = prefs.edit();
            long launch_count = prefs.getLong("launch_count", 0) + 1;
            editor.putLong("launch_count", launch_count);
            Long date_firstLaunch = Long.valueOf(prefs.getLong("date_firstlaunch", 0));
            if (date_firstLaunch.longValue() == 0) {
                date_firstLaunch = Long.valueOf(System.currentTimeMillis());
                editor.putLong("date_firstlaunch", date_firstLaunch.longValue());
            }
            if (launch_count >= 5 && System.currentTimeMillis() >= date_firstLaunch.longValue() + 86400000) {
                showRateDialog(mContext, editor);
            }
            editor.commit();
        }
    }

    public static void showRateDialog(final Context mContext, final Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Rate HTML Website Inspector Pro");
        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(DAYS_UNTIL_PROMPT);
        TextView tv = new TextView(mContext);
        tv.setText("If you enjoy using HTML Website Inspector Pro, please take a moment to rate it. Thanks for your support!");
        tv.setWidth(240);
        tv.setPadding(4, 0, 4, 10);
        ll.addView(tv);
        Button b1 = new Button(mContext);
        b1.setText("Rate HTML Website Inspector Pro");
        b1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=web.dassem.websiteanalyzerpro")));
                dialog.dismiss();
            }
        });
        ll.addView(b1);
        Button b2 = new Button(mContext);
        b2.setText("Remind me later");
        b2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ll.addView(b2);
        Button b3 = new Button(mContext);
        b3.setText("No, thanks");
        b3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        ll.addView(b3);
        dialog.setContentView(ll);
        dialog.show();
    }
}
