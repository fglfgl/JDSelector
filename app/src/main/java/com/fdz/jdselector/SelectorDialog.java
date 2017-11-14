package com.fdz.jdselector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.fdz.library.selector.ui.JDSelector;

/**
 * @author : fangguiliang
 * @version : 1.0.0
 * @since : 2017/11/14
 */

public class SelectorDialog extends Dialog {

    public SelectorDialog(Context context) {
        super(context, R.style.CustomerDialogTheme);
    }

    public SelectorDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public SelectorDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void init(Activity activity, JDSelector selector) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogSelector = inflater.inflate(R.layout.dialog_selector, null, false);
        dialogSelector.findViewById(R.id.iv_close).setOnClickListener(v -> dismiss());
        if (selector != null) {
            ((FrameLayout) dialogSelector.findViewById(R.id.fl_selector)).addView(selector.getContextView());
        }
        setContentView(dialogSelector);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) (getContext()).getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        params.height = dm.heightPixels / 2;
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
    }
}
