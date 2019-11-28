package com.mcal.websiteanalyzerpro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchTagsActivity extends AppCompatActivity {
    private static String HTMLSourceCode;
    private static ArrayList<String> elements;
    private ArrayAdapter<String> adapter;
    private ListView lv;
    private SearchTagsActivity searchTagsActivity;
    private ArrayList<String> tempArrayList;

    public static void setHTMLSourceCode(String htmlSourceCode) {
        HTMLSourceCode = htmlSourceCode;
    }

    public static void setHTMLElements(ArrayList<String> pickedElements) {
        elements = pickedElements;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tags);
        searchTagsActivity = this;
        tempArrayList = new ArrayList<>();
        setSupportActionBar(findViewById(R.id.toolbar));
        if (elements == null) {
            elements = new ArrayList<>();
            elements.add("No elements to display");
        } else if (elements.size() == 0) {
            elements.add("No elements to display");
        }
        this.lv = findViewById(R.id.list_view);
        EditText inputSearch = findViewById(R.id.searchTags);
        adapter = new ArrayAdapter(this, R.layout.list_item, R.id.product_name, elements);
        this.lv.setAdapter(adapter);
        if (inputSearch != null) {
            inputSearch.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    tempArrayList.clear();
                    int textlength = cs.length();
                    for (String c : SearchTagsActivity.elements) {
                        if (textlength <= c.length() && c.toLowerCase().contains(cs.toString().toLowerCase())) {
                            tempArrayList.add(c);
                        }
                    }
                    adapter = new ArrayAdapter(searchTagsActivity, R.layout.list_item, R.id.product_name, tempArrayList);
                    lv.setAdapter(adapter);
                }

                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                }

                public void afterTextChanged(Editable arg0) {
                }
            });
        }
    }

    @SuppressLint("WrongConstant")
    public void onClick(View v) {
        String tagCode = ""/*BuildConfig.FLAVOR*/ + ((TextView) v).getText();
        if (HTMLSourceCode != null) {
            Intent switchIntent = new Intent(this, ViewHtmlActivity.class);
            switchIntent.putExtra("original", tagCode);
            startActivity(switchIntent);
            finish();
            return;
        }
        Toast.makeText(getBaseContext(), "Element cannot be empty", 0).show();
    }

    protected void onDestroy() {
        HTMLSourceCode = null;
        elements = null;
        adapter = null;
        lv = null;
        searchTagsActivity = null;
        tempArrayList = null;
        super.onDestroy();
    }
}