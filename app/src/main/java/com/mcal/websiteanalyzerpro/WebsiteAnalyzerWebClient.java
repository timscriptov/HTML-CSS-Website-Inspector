package com.mcal.websiteanalyzerpro;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class WebsiteAnalyzerWebClient extends WebViewClient {
    private Context context;

    public WebsiteAnalyzerWebClient(Context context) {
        this.context = context;
    }

    public void onPageFinished(WebView view, String url) {
        ((MainActivity) context).hideProgressBar();
        MainActivity.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
        if (!url.equalsIgnoreCase("about:blank")) {
            MainActivity.setWebsiteAddress(url);
        }
        super.onPageFinished(view, url);
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.toLowerCase().contains("youtube")) {
            MainActivity.showYoutubeViolationToast(context);
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            view.loadUrl(url);
            ((MainActivity) context).setEditText(url);
        } else {
            view.loadUrl("http://" + url);
            ((MainActivity) context).setEditText("http://" + url);
        }
        return true;
    }

    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        ((MainActivity) context).setProgressBar(0);
        ((MainActivity) context).showProgressBar();
        super.onPageStarted(view, url, favicon);
    }
}