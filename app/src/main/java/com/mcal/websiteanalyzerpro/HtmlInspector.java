package com.mcal.websiteanalyzerpro;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class HtmlInspector implements OnTouchListener {
    private float webViewHeight;
    private float webViewWidth;

    HtmlInspector(float width, float height) {
        webViewWidth = width;
        webViewHeight = height;
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == 0) {
            float x = event.getX();
            MainActivity.loadUrl("javascript:HTMLOUT.getHTMLElement(document.elementFromPoint(" + x + "* (window.innerWidth/" + webViewWidth + ")," + event.getY() + "*(window.innerHeight/" + webViewHeight + ")).outerHTML);");
        }
        return true;
    }
}
