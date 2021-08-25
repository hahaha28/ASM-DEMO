package com.example.asmdemo;

import android.util.Log;
import android.view.View;

public class Test implements View.OnClickListener {
    private long lastClickTime = 0L;

    @Override
    public void onClick(View v) {
            if (System.currentTimeMillis() - this.lastClickTime < 1000L) {
                this.lastClickTime = System.currentTimeMillis();
                return;
            }
            this.lastClickTime = System.currentTimeMillis();

            Log.e("tag","click");
    }
}
