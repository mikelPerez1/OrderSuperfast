package com.OrderSuperfast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FadeItemDecoration2 extends RecyclerView.ItemDecoration {
    private final Paint gradientPaint;
    private final int gradientHeight;

    public FadeItemDecoration2(int gradientHeight) {
        this.gradientHeight = gradientHeight;

        gradientPaint = new Paint();
        gradientPaint.setShader(new LinearGradient(
                0f, 0f, 0f, gradientHeight,
                Color.WHITE, Color.TRANSPARENT, Shader.TileMode.CLAMP));
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);

        int childCount = parent.getChildCount();
        int width = parent.getWidth();
        int height = parent.getHeight();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int bottom = child.getBottom();
            int top = bottom - gradientHeight;

            float opacity = calculateOpacity(child, height, gradientHeight);
            gradientPaint.setAlpha((int) (opacity * 255));

            canvas.drawRect(0, top, width, bottom, gradientPaint);
        }
    }

    private float calculateOpacity(View child, int recyclerViewHeight, int gradientHeight) {
        int bottom = child.getBottom();
        int childHeight = child.getHeight();
        int totalHeight = recyclerViewHeight - gradientHeight;
        float distanceFromBottom = totalHeight - bottom;

        System.out.println("distancia "+distanceFromBottom + " " +gradientHeight + " "+ distanceFromBottom/gradientHeight);
        if (distanceFromBottom < 0) {
            return 1.0f;
        } else if (distanceFromBottom > gradientHeight) {
            return 0.0f;
        } else {
            return distanceFromBottom / gradientHeight;
        }
    }
}
