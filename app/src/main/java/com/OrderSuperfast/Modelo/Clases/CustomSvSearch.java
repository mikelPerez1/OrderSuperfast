package com.OrderSuperfast.Modelo.Clases;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

public class CustomSvSearch extends SearchView {
    private Activity context;

    public CustomSvSearch(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListaActivity(Activity a) {
        this.context = a;
    }


    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {

        System.out.println("KEYEVENT " + event.getKeyCode());

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            // User has pressed Back key. So hide the keyboard
            context.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            // this.clearFocus();


        }
        return false;
    }
}
