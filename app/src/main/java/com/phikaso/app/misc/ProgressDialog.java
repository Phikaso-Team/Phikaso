package com.phikaso.app.misc;

import android.app.Dialog;
import android.view.Window;

import com.phikaso.app.R;

import android.content.Context;

public class ProgressDialog extends Dialog {

    public ProgressDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);
    }
}
