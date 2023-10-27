package com.OrderSuperfast;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

public class CustomEditTextNumbers extends androidx.appcompat.widget.AppCompatEditText {

    private Activity context1;

    public CustomEditTextNumbers(Context context, AttributeSet attrs) {
        super(context, attrs);


    }


    public void setActivity(Activity a) {
        this.context1 = a;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        System.out.println("keyback " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ENTER) {
            // User has pressed Back key. So hide the keyboard
            context1.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

            );

            this.clearFocus();

        }
        return false;
    }
}