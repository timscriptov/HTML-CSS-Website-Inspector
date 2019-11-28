package com.mcal.websiteanalyzerpro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

public class SessionAdapter extends ArrayAdapter<String> {
    private Context context;
    private int selectedPosition = -1;
    private List<String> titles;

    public SessionAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    public SessionAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        this.titles = objects;
        this.context = context;
    }

    @SuppressLint("WrongConstant")
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.session_item, null);
            v.findViewById(R.id.marked);
        }
        ((TextView) v.findViewById(R.id.session_title)).setText(titles.get(position));
        RadioButton r = v.findViewById(R.id.marked);
        r.setChecked(position == selectedPosition);
        r.setTag(Integer.valueOf(position));
        r.setOnClickListener(view -> {
            selectedPosition = (Integer) view.getTag();
            notifyDataSetChanged();
        });
        return v;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}