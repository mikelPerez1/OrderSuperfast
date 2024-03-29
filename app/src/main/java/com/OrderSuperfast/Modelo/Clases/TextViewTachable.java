package com.OrderSuperfast.Modelo.Clases;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.util.AttributeSet;

import com.OrderSuperfast.R;


/**
 * Clase que extiende de TextView para customizar un tachón rojo al texto
 */
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
            //si hay que tachar
            Layout layout = getLayout();
            for (int i = 0; i < layout.getLineCount(); i++) {
                //se obtiene la posición media del textview y se tacha
                int baseline =layout.getLineTop(i) +((int) ((layout.getLineBaseline(i)-layout.getLineTop(i))/ 1.45));
                System.out.println("lineas "+(layout.getLineBaseline(i)-layout.getLineTop(i) ));
                canvas.drawLine(layout.getLineLeft(i), baseline, layout.getLineRight(i), baseline, paint);
            }
        }
    }

    public void setStrike(boolean addStrike) {
        this.addStrike = addStrike;
        invalidate();
    }

}