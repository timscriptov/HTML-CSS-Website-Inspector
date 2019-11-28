package com.mcal.websiteanalyzerpro;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class ManageSessionsActivity extends AppCompatActivity {
    private SessionAdapter adapter;
    private String htmlsourcecode;
    private ListView lv;
    private ManageSessionsActivity manageSessionsActivity;
    private ArrayList<Session> sessions;
    private ArrayList<String> tempArrayList;
    private ArrayList<String> titles;
    private String url;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_sessions_activity);
        manageSessionsActivity = this;
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        htmlsourcecode = MainActivity.getHTMLSourceCode();
        url = MainActivity.getUrl();
        lv = (ListView) findViewById(R.id.manageSessions_list);
        EditText inputSearch = (EditText) findViewById(R.id.searchTags);
        sessions = new ArrayList<>();
        titles = new ArrayList<>();
        loadSessions();
        tempArrayList = new ArrayList<>();
        adapter = new SessionAdapter(this, R.layout.session_item, R.id.session_title, titles);
        lv.setAdapter(adapter);
        inputSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                tempArrayList.clear();
                int textlength = cs.length();
                Iterator it = titles.iterator();
                while (it.hasNext()) {
                    String c = (String) it.next();
                    if (textlength <= c.length() && c.toLowerCase().contains(cs.toString().toLowerCase())) {
                        tempArrayList.add(c);
                    }
                }
                adapter = new SessionAdapter(manageSessionsActivity, R.layout.session_item, R.id.product_name, tempArrayList);
                lv.setAdapter(adapter);
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    private void loadSessions() {
        String json = getApplication().getSharedPreferences(getApplication().getApplicationInfo().name, 0).getString("session", "notfound");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Session>>() {
        }.getType();
        if (json.equals("notfound")) {
            System.out.println("Not found");
        } else {
            sessions = gson.fromJson(json, type);
        }
        Iterator it = sessions.iterator();
        while (it.hasNext()) {
            titles.add(((Session) it.next()).getTitle());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_sessions_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.saveSession) {
            Session session = new Session();
            if (htmlsourcecode == null || url == null) {
                Toast.makeText(this, "Session must not be empty", 0).show();
            } else {
                session.setHTMLSourceCode(htmlsourcecode);
                session.setUrl(url);
                sessions.add(session);
                saveSession(sessions);
                Toast.makeText(this, "Session saved", 0).show();
                titles.add(session.getTitle());
                adapter = new SessionAdapter(this, R.layout.session_item, R.id.session_title, titles);
                lv.setAdapter(adapter);
            }
        } else if (id == R.id.loadSession) {
            if (adapter.getSelectedPosition() == -1) {
                Toast.makeText(this, "Please select session first", 0).show();
            } else {
                MainActivity.loadAlteredCode(sessions.get(adapter.getSelectedPosition()).getHTMLSourceCode(), sessions.get(adapter.getSelectedPosition()).getUrl());
                saveSession(sessions);
                finish();
            }
        } else if (id == R.id.deleteSession) {
            if (adapter.getSelectedPosition() == -1) {
                Toast.makeText(this, "Please select session first", 0).show();
            } else {
                titles.remove(adapter.getSelectedPosition());
                sessions.remove(adapter.getSelectedPosition());
                adapter = new SessionAdapter(this, R.layout.session_item, R.id.session_title, titles);
                lv.setAdapter(adapter);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveSession(final ArrayList<Session> session) {
        new Thread() {
            public void run() {
                Editor ed = getApplication().getSharedPreferences(getApplication().getApplicationInfo().name, 0).edit();
                ed.putString("session", new Gson().toJson(session));
                ed.apply();
            }
        }.start();
    }

    protected void onDestroy() {
        super.onDestroy();
        saveSession(sessions);
    }
}
