package com.vrlcrypt.arkmonitor.utils;

import android.view.View;
import android.widget.EditText;

public class ViewUtils {

    public static String getEditTextValue (View v, int viewId, String def) {
        String str = ((EditText) v.findViewById(viewId)).getText().toString();

        if (str.equals(""))
            return def;
        else
            return str;
    }

}
