package com.example.disciteomneslearningplatform.ui;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.widget.Button;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

public class AlertDialogButtons {
    public static void formatButtons(Context ctx, AlertDialog dialog, @AttrRes int colorAttr) {
        TypedValue tv = new TypedValue();
        boolean found = ctx.getTheme().resolveAttribute(colorAttr, tv, true);
        if (!found) {
            // fallback or bail
            return;
        }

        // 2) tv.resourceId if it points to a color resource, otherwise tv.data holds the color int
        @ColorInt int color = (tv.resourceId != 0)
                ? ContextCompat.getColor(ctx, tv.resourceId)
                : tv.data;
        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positive != null) {
            positive.setTextColor(color);
        }

        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (negative != null) {
            negative.setTextColor(color);
        }
    }

    public static void formatButton(Context ctx, Button b, @AttrRes int colorBgAttr, int colorTxtAttr) {
        TypedValue tv = new TypedValue();

        // Resolve background‐tint attribute
        int bgColor = 0;
        if (ctx.getTheme().resolveAttribute(colorBgAttr, tv, true)) {
            bgColor = (tv.resourceId != 0)
                    ? ContextCompat.getColor(ctx, tv.resourceId)
                    : tv.data;
            b.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        }

        // Resolve text‐color attribute
        int txtColor = 0;
        if (ctx.getTheme().resolveAttribute(colorTxtAttr, tv, true)) {
            txtColor = (tv.resourceId != 0)
                    ? ContextCompat.getColor(ctx, tv.resourceId)
                    : tv.data;
            b.setTextColor(txtColor);
        }

    }
    public static void formatButtonTextColor(Context ctx, Button b, int colorTxtAttr) {
        TypedValue tv = new TypedValue();

        // Resolve text‐color attribute
        int txtColor = 0;
        if (ctx.getTheme().resolveAttribute(colorTxtAttr, tv, true)) {
            txtColor = (tv.resourceId != 0)
                    ? ContextCompat.getColor(ctx, tv.resourceId)
                    : tv.data;
            b.setTextColor(txtColor);
        }

    }
}