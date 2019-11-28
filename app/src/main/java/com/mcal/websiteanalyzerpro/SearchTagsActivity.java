package com.mcal.websiteanalyzerpro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;

public class SearchTagsActivity extends AppCompatActivity {
    private static String HTMLSourceCode;
    private static ArrayList<String> elements;
    private ArrayAdapter<String> adapter;
    private ListView lv;
    private SearchTagsActivity searchTagsActivity;
    private ArrayList<String> tempArrayList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tags);
        searchTagsActivity = this;
        tempArrayList = new ArrayList<>();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (elements == null) {
            elements = new ArrayList<>();
            elements.add("No elements to display");
        } else if (elements.size() == 0) {
            elements.add("No elements to display");
        }
        this.lv = (ListView) findViewById(R.id.list_view);
        EditText inputSearch = (EditText) findViewById(R.id.searchTags);
        adapter = new ArrayAdapter(this, R.layout.list_item, R.id.product_name, elements);
        this.lv.setAdapter(adapter);
        if (inputSearch != null) {
            inputSearch.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    tempArrayList.clear();
                    int textlength = cs.length();
                    Iterator it = SearchTagsActivity.elements.iterator();
                    while (it.hasNext()) {
                        String c = (String) it.next();
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

    public static void setHTMLSourceCode(String htmlSourceCode) {
        HTMLSourceCode = htmlSourceCode;
    }

    public static void setHTMLElements(ArrayList<String> pickedElements) {
        elements = pickedElements;
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
