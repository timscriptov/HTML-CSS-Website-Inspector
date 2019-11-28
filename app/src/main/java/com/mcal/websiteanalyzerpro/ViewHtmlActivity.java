package com.mcal.websiteanalyzerpro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.ArrayList;
import org.jsoup.Jsoup;

public class ViewHtmlActivity extends AppCompatActivity {
    private CharSequence charSequence;
    private int currentIndex = 0;
    private ArrayList<Integer> foundWords;
    private String html = null;
    private String original = null;
    private EditText pageSource;
    private ScrollView scrollView;
    private EditText searchQuery;
    private boolean searchQueryChanged = false;
    private SpannableString str;
    private String word = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_html);
        foundWords = new ArrayList<>();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        original = getIntent().getStringExtra("original");
        pageSource = (EditText) findViewById(R.id.pageSource);
        html = MainActivity.getHTMLSourceCode();
        if (html == null) {
            Toast.makeText(this, "Something went wrong. Please try again. ", 0).show();
            finish();
        } else if (html.isEmpty()) {
            Toast.makeText(this, "Something went wrong. Please try again. ", 0).show();
            finish();
        }
        if (original != null) {
            pageSource.setText(original);
        } else {
            pageSource.postDelayed(new Runnable() {
                public void run() {
                    pageSource.setText(Jsoup.parse(html).toString());
                }
            }, 100);
        }
        pageSource.setSingleLine(false);
        searchQuery = (EditText) findViewById(R.id.searchfield);
        searchQuery.setImeOptions(3);
        scrollView = (ScrollView) findViewById(R.id.scrollToTarget);
        setOnActionListener();
        pageSource.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ((InputMethodManager) getSystemService("input_method")).showSoftInput(pageSource, 1);
            }
        });
    }

    private void setOnActionListener() {
        searchQuery.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 3) {
                    return false;
                }
                scrollToWord(false);
                scrollToWord(false);
                return true;
            }
        });
        searchQuery.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charSequence = s;
                highLightText(pageSource, charSequence.toString(), true);
                word = s.toString();
                searchQueryChanged = true;
            }
        });
    }

    private void highLightText(EditText textView, String searchString, boolean highlight) throws IndexOutOfBoundsException {
        String s = textView.getText().toString();
        this.str = new SpannableString(s);
        if (searchString != null && !searchString.equalsIgnoreCase(""/*BuildConfig.FLAVOR*/) && searchString.length() >= 2) {
            int startIndex = 0;
            while (true) {
                startIndex = s.indexOf(searchString, startIndex);
                if (startIndex < 0) {
                    break;
                }
                if (highlight) {
                    str.setSpan(new BackgroundColorSpan(-256), startIndex, searchString.length() + startIndex, 33);
                } else {
                    foundWords.add(Integer.valueOf(startIndex));
                }
                startIndex++;
            }
        }
        if (highlight) {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        pageSource.setText(str);
                    } catch (IndexOutOfBoundsException ignore) {
                        System.out.println(ignore.toString());
                    }
                }
            });
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                return true;
            case R.id.up /*2131623949*/:
                if (searchQueryChanged) {
                    currentIndex = 0;
                    foundWords.clear();
                    searchQueryChanged = false;
                    new Thread() {
                        public void run() {
                            try {
                                highLightText(pageSource, word, false);
                            } catch (IndexOutOfBoundsException e) {
                            }
                        }
                    }.start();
                }
                scrollToWord(true);
                return true;
            case R.id.viewInBrowser /*2131624107*/:
                if (original == null || html == null) {
                    MainActivity.loadAlteredCode(String.valueOf(pageSource.getText()));
                } else if (html.contains(original)) {
                    html = html.replace(original, String.valueOf(pageSource.getText()));
                    MainActivity.loadAlteredCode(html);
                }
                finish();
                return true;
            case R.id.down /*2131624121*/:
                if (searchQueryChanged) {
                    currentIndex = 0;
                    foundWords.clear();
                    searchQueryChanged = false;
                    new Thread() {
                        public void run() throws IndexOutOfBoundsException {
                            try {
                                highLightText(pageSource, word, false);
                            } catch (IndexOutOfBoundsException e) {
                            }
                        }
                    }.start();
                }
                scrollToWord(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void scrollToWord(boolean up) {
        if (foundWords != null && foundWords.size() > 0 && pageSource.getLayout() != null) {
            if (up) {
                currentIndex--;
            } else {
                currentIndex++;
            }
            if (currentIndex <= 0 || currentIndex >= foundWords.size()) {
                currentIndex = 0;
            }
            scrollView.scrollTo(0, pageSource.getLayout().getLineTop(pageSource.getLayout().getLineForOffset(foundWords.get(currentIndex).intValue())));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    protected void onDestroy() {
        pageSource = null;
        searchQuery = null;
        html = null;
        original = null;
        scrollView = null;
        super.onDestroy();
    }
}
