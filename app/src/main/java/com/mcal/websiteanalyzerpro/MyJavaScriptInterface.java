package com.mcal.websiteanalyzerpro;

import android.content.Context;
import android.webkit.JavascriptInterface;

class MyJavaScriptInterface {
    private Context context;

    MyJavaScriptInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void processHTML(String html) {
        MainActivity.setHTMLSourceCode(html);
    }

    @JavascriptInterface
    public void getHTMLElement(String element) {
        ((MainActivity) context).setTextViewTextOuter(element);
    }
}