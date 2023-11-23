package com.OrderSuperfast.Modelo.Clases;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.OrderSuperfast.Vista.MainActivity;

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

    private Activity context1;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);


    }


    public void setMainActivity(Activity a) {
        this.context1 = a;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        System.out.println("keyback " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
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

            if(context1 instanceof MainActivity){
                ((MainActivity) context1).desplazarPagina();
            }
            this.clearFocus();

        }
        return false;
    }
}