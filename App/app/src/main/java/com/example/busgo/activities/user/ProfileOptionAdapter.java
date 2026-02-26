package com.example.busgo.activities.user;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.busgo.R;

public class ProfileOptionAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] titles;
    private final int[] icons;

    public ProfileOptionAdapter(Activity context, String[] titles, int[] icons) {
        super(context, R.layout.item_profile_option, titles);
        this.context = context;
        this.titles = titles;
        this.icons = icons;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.item_profile_option, parent, false);

        ImageView imgIcon = rowView.findViewById(R.id.imgIcon);
        TextView tvTitle = rowView.findViewById(R.id.txtOption);

        imgIcon.setImageResource(icons[position]);
        tvTitle.setText(titles[position]);

        return rowView;
    }
}