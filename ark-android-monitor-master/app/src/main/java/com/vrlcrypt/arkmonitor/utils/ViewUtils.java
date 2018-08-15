package com.vrlcrypt.arkmonitor.utils;

import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.models.Status;

public class ViewUtils {

    public static String getEditTextValue (View v, int viewId, String def) {
        String str = ((EditText) v.findViewById(viewId)).getText().toString();

        if (str.equals(""))
            return def;
        else
            return str;
    }

    @BindingAdapter("colorTint")
    public static void setColorTint(View view, @ColorRes int status) {
        switch (status) {
            case Status.FORGING:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.forging));
                break;
            case Status.AWAITING_SLOT:
            case Status.AWAITING_STATUS:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.awaitingslot));
                break;
            case Status.MISSING:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.missing));
                break;
            case Status.NOT_FORGING:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.notforging));
                break;
        }
    }

}
