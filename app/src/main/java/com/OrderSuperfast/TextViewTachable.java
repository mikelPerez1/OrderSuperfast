package com.OrderSuperfast;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

// clase para tachar de un color (rojo en este caso) un textview
public class TextViewTachable extends androidx.appcompat.widget.AppCompatTextView {
    public Paint paint;
    public boolean addStrike = false;
    public TextViewTachable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }
    public TextViewTachable(Context context) {
        super(context);
        init(context);
    }

    public TextViewTachable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.rojo, context.getTheme()));
        paint.setStrokeWidth(getResources().getDisplayMetrics().density * 6);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (addStrike) {
            Layout layout = getLayout();
            for (int i = 0; i < layout.getLineCount(); i++) {
                int baseline =layout.getLineTop(i) +(10+(layout.getLineBaseline(i)-layout.getLineTop(i) )/ 2);
                System.out.println("lineas "+baseline);
                canvas.drawLine(layout.getLineLeft(i), baseline, layout.getLineRight(i), baseline, paint);
            }
        }
    }

    public void setStrike(boolean addStrike) {
        this.addStrike = addStrike;
        invalidate();
    }

}