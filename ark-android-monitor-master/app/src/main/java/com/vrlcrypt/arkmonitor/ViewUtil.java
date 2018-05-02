package com.vrlcrypt.arkmonitor;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ViewUtil {

    @BindingAdapter("spinnerEntries")
    public static void setSpinnerEntries(View v, ArrayAdapter arrayAdapter) {
        ((Spinner) v).setAdapter(arrayAdapter);
    }

    @BindingAdapter("onItemSelected")
    public static void setOnItemSelected(View v, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        ((Spinner) v).setOnItemSelectedListener(onItemSelectedListener);
    }

}
