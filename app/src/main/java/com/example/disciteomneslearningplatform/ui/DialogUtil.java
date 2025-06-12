package com.example.disciteomneslearningplatform.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;

import com.example.disciteomneslearningplatform.R;

public class DialogUtil {
    public static void applyButtonColor(AlertDialog dialog) {
        // Resolve your custom attr from the current theme
        Context ctx = dialog.getContext();
        TypedValue tv = new TypedValue();
        boolean found = ctx.getTheme()
                .resolveAttribute(R.attr.alert_options_color, tv, true);
        @ColorInt int textColor = found ? tv.data : Color.BLACK;

        // Now apply it
        Button pos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button neg = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        pos.setTextColor(textColor);
        neg.setTextColor(textColor);
    }

}
