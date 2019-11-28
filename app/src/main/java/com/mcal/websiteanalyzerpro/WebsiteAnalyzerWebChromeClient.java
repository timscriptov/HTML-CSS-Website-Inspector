package com.mcal.websiteanalyzerpro;

import android.content.Context;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

class WebsiteAnalyzerWebChromeClient extends WebChromeClient {
    private Context context;

    WebsiteAnalyzerWebChromeClient(Context context) {
        this.context = context;
    }

    public void onProgressChanged(WebView view, int newProgress) {
        ((MainActivity) context).setProgressBar(newProgress);
        super.onProgressChanged(view, newProgress);
    }

    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        super.onConsoleMessage(consoleMessage);
        MainActivity.setConsoleText(consoleMessage.message(), context);
        if (consoleMessage != null && consoleMessage.message().equals("true")) {
            JavaScriptConsoleActivity.finishActivity();
        }
        return true;
    }
}
