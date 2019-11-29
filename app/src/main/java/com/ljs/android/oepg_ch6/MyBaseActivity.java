package com.ljs.android.oepg_ch6;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MyBaseActivity extends AppCompatActivity {

    protected void logError(String msg) {
        Log.e(this.getClass().getSimpleName(), msg);
    }

    protected void logDebug(String msg) {
        Log.d(this.getClass().getSimpleName(), msg);
    }
}
