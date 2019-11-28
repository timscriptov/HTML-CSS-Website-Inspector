package com.mcal.websiteanalyzerpro;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static String HTMLSourceCode = null;
    private static int count = 0;
    private static CustomWebview myWebView;
    private static String url = null;
    private static String webPageAddress = null;
    private Dialog dialog;
    private RelativeLayout dimBackground;
    private boolean firstTime = false;
    private EditText getWebPageAddress;
    private boolean inspectorMode = false;
    private Menu menu;
    private String path;
    private String pickedElement = null;
    private ProgressBar progressBar;

    public static String getUrl() {
        return url;
    }

    public static void loadAlteredCode(String html, String weburl) {
        myWebView.loadDataWithBaseURL(weburl, html, "text/html", "utf-8", null);
    }

    public static void loadAlteredCode(String html) {
        myWebView.loadDataWithBaseURL(url, html, "text/html", "utf-8", null);
    }

    public static void loadJavascript(String code) {
        myWebView.loadUrl("javascript:(function() { try{" + code + ";console.log('true')}catch(err){console.log(err);}})()");
    }

    public static void setWebsiteAddress(String currentWebURL) {
        url = currentWebURL;
    }

    @SuppressLint("WrongConstant")
    public static void showYoutubeViolationToast(Context context) {
        Toast.makeText(context, "Google policy disallows viewing youtube videos.", 0).show();
    }

    private static String removeScriptTags(String message) {
        Pattern pattern2 = Pattern.compile("<\\s*script.*?(/\\s*>|<\\s*/\\s*script[^>]*>)");
        if (message == null) {
            return message;
        }
        Matcher matcher2 = pattern2.matcher(message);
        StringBuffer str = new StringBuffer(message.length());
        while (matcher2.find()) {
            matcher2.appendReplacement(str, Matcher.quoteReplacement(" "));
        }
        matcher2.appendTail(str);
        return str.toString();
    }

    public static String getHTMLSourceCode() {
        return HTMLSourceCode;
    }

    public static void setHTMLSourceCode(String HTMLSourceCode2) {
        HTMLSourceCode = HTMLSourceCode2;
    }

    public static void loadUrl(String url) {
        myWebView.loadUrl(url);
    }

    public static void setConsoleText(String javascriptMessage, Context context) {
        Intent intent = new Intent("sendJavascript");
        intent.putExtra("message", javascriptMessage);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        initialize();
        setOnActionListener();
    }

    protected void onPause() {
        Editor editor = getPreferences(0).edit();
        editor.putInt("count", count);
        editor.apply();
        super.onPause();
    }

    @SuppressLint({"WrongConstant", "SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initialize() {
        this.menu = findViewById(R.id.menu_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        getWebPageAddress = findViewById(R.id.searchfield);
        progressBar = findViewById(R.id.progressBar1);
        progressBar.setMax(100);
        progressBar.setVisibility(8);
        dimBackground = findViewById(R.id.bac_dim_layout);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/website.txt";
        WebsiteAnalyzerWebChromeClient webChromeClient = new WebsiteAnalyzerWebChromeClient(this);
        WebsiteAnalyzerWebClient webClient = new WebsiteAnalyzerWebClient(this);
        myWebView = findViewById(R.id.webview);
        myWebView.setWebViewClient(webClient);
        myWebView.setWebChromeClient(webChromeClient);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.addJavascriptInterface(new MyJavaScriptInterface(this), "HTMLOUT");
        myWebView.loadUrl("http://" + "google.com");
        count = getPreferences(0).getInt("count", 0);
        handleIntent();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnActionListener() {
        getWebPageAddress.setOnTouchListener((v, event) -> {
            showMenu(Boolean.FALSE);
            return false;
        });
        getWebPageAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != 2) {
                return false;
            }
            hideKeyboard();
            showMenu(Boolean.TRUE);
            getWebPageAddress.clearFocus();
            MainActivity.webPageAddress = getWebPageAddress.getText() + ""/*BuildConfig.FLAVOR*/;
            if (MainActivity.webPageAddress.startsWith("http://") || MainActivity.webPageAddress.startsWith("https://")) {
                MainActivity.myWebView.loadUrl(MainActivity.webPageAddress);
            } else {
                MainActivity.webPageAddress = "http://" + getWebPageAddress.getText();
                MainActivity.myWebView.loadUrl(MainActivity.webPageAddress);
            }
            progressBar.setProgress(0);
            return true;
        });
        getWebPageAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                showMenu(Boolean.TRUE);
                hideKeyboard();
                getWebPageAddress.clearFocus();
            }
        });
    }

    @SuppressLint("WrongConstant")
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setEditText(String url) {
        getWebPageAddress.setText(url);
    }

    @SuppressLint("WrongConstant")
    private void showMenu(Boolean hide) {
        if (menu != null) {
            menu.setGroupVisible(R.id.main_menu_group, hide);
            if (menu.hasVisibleItems()) {
                dimBackground.setVisibility(8);
                return;
            }
            dimBackground.bringToFront();
            dimBackground.setVisibility(0);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(((float) dp) * (getResources().getDisplayMetrics().xdpi / 160.0f));
    }

    public void setProgressBar(int i) {
        progressBar.setProgress(i);
    }

    @SuppressLint("WrongConstant")
    public void hideProgressBar() {
        progressBar.setVisibility(8);
    }

    @SuppressLint("WrongConstant")
    public void showProgressBar() {
        progressBar.setVisibility(0);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        final View activityRootView = findViewById(R.id.activity_root);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (activityRootView.getRootView().getHeight() - activityRootView.getHeight() > dpToPx(200)) {
                if (!firstTime) {
                    firstTime = true;
                }
            } else if (firstTime) {
                showMenu(Boolean.TRUE);
                firstTime = false;
            }
        });
        return true;
    }

    @SuppressLint({"ClickableViewAccessibility", "WrongConstant"})
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean z = true;
        int id = item.getItemId();
        if (id == R.id.inspectorMode) {
            if (inspectorMode) {
                disableInspectorMode();
            } else {
                menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_touch_app_amber_48dp));
                myWebView.setOnTouchListener(new HtmlInspector((float) myWebView.getWidth(), (float) myWebView.getHeight()));
                inspectorMode = true;
            }
        } else if (id == R.id.refresh) {
            if (webPageAddress == null || webPageAddress.equals(""/*BuildConfig.FLAVOR*/)) {
                myWebView.reload();
            } else {
                myWebView.loadUrl(webPageAddress);
            }
        } else if (id == R.id.savehtml) {
            shareFile(HTMLSourceCode);
        } else if (id == R.id.manage) {
            startActivity(new Intent(this, ManageSessionsActivity.class));
        } else if (id == R.id.viewelements) {
            pickHTMLElement();
        } else if (id == R.id.javascript_console_activity) {
            launchEditContentActivity();
        } else if (id == R.id.request_desktop) {
            if (item.isChecked()) {
                z = false;
            }
            item.setChecked(z);
            myWebView.setDesktopMode(item.isChecked());
        } else if (id == R.id.viewHTML) {
            if (HTMLSourceCode != null) {
                setTextviewText(HTMLSourceCode, null);
            } else {
                Toast.makeText(getBaseContext(), "Please load the page first.", 0).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    void launchEditContentActivity() {
        startActivity(new Intent(this, JavaScriptConsoleActivity.class));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void disableInspectorMode() {
        myWebView.setOnTouchListener(null);
        menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_touch_app_white_48dp));
        inspectorMode = false;
    }

    void pickHTMLElement() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.list_view_layout);
        final ListView lv = dialog.findViewById(R.id.lv);
        dialog.setCancelable(true);
        dialog.setTitle("HTML Tags");
        dialog.show();
        lv.setOnItemClickListener((adapterView, myView, myItemInt, mylng) -> {
            dialog.hide();
            pickedElement = (String) lv.getItemAtPosition(myItemInt);
            setTextviewText(MainActivity.HTMLSourceCode, pickedElement);
        });
    }

    public void onBackPressed() {
        if (menu != null) {
            showMenu(Boolean.FALSE);
            getWebPageAddress.clearFocus();
        }
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null && "text/plain".equals(intent.getType()) && "android.intent.action.SEND".equals(intent.getAction())) {
            String data = getIntent().getStringExtra("android.intent.extra.TEXT");
            if (data != null) {
                getWebPageAddress.setText(data);
                myWebView.loadUrl(data);
            }
        }
    }

    @SuppressLint("WrongConstant")
    public void shareFile(String content) {
        try {
            File htmlFile = new File(path);
            htmlFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(htmlFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(Jsoup.parse(content).toString());
            myOutWriter.close();
            fOut.close();
            Intent sharingIntent = new Intent("android.intent.action.SEND");
            sharingIntent.setType("text/*");
            sharingIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(htmlFile));
            startActivity(Intent.createChooser(sharingIntent, "Share page source:"));
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), 0).show();
        }
    }

    @SuppressLint("WrongConstant")
    void setTextviewText(String message, String element) {
        if (message == null && HTMLSourceCode == null) {
            Toast.makeText(getBaseContext(), "Please load the page first.", 0).show();
            return;
        }
        HTMLSourceCode = removeScriptTags(HTMLSourceCode);
        Intent switchIntent = new Intent(this, ViewHtmlActivity.class);
        if (element == null) {
            startActivity(switchIntent);
            return;
        }
        Intent elementsIntent = new Intent(this, SearchTagsActivity.class);
        List<Element> elements = new Source(message).getAllElements(element.replace("<", ""/*BuildConfig.FLAVOR*/).replace(">", ""/*BuildConfig.FLAVOR*/));
        ArrayList<String> tags = new ArrayList<>();
        for (Element tag : elements) {
            tags.add(tag.toString());
        }
        SearchTagsActivity.setHTMLSourceCode(HTMLSourceCode);
        SearchTagsActivity.setHTMLElements(tags);
        startActivity(elementsIntent);
    }

    @SuppressLint("WrongConstant")
    void setTextViewTextOuter(String original) {
        if (original == null || HTMLSourceCode == null) {
            Toast.makeText(this, "Element cannot be empty", 0).show();
            return;
        }
        new Thread(() -> MainActivity.HTMLSourceCode = MainActivity.removeScriptTags(MainActivity.HTMLSourceCode));
        Intent switchIntent = new Intent(this, ViewHtmlActivity.class);
        switchIntent.putExtra("original", original);
        startActivity(switchIntent);
    }
}