package com.mcal.websiteanalyzerpro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.session_item, null);
            RadioButton radioButton = v.findViewById(R.id.marked);
        }
        ((TextView) v.findViewById(R.id.session_title)).setText((CharSequence) titles.get(position));
        RadioButton r = v.findViewById(R.id.marked);
        r.setChecked(position == selectedPosition);
        r.setTag(Integer.valueOf(position));
        r.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                selectedPosition = ((Integer) view.getTag()).intValue();
                notifyDataSetChanged();
            }
        });
        return v;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
