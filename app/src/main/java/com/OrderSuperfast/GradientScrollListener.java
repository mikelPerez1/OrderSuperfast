package com.OrderSuperfast;import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GradientScrollListener extends RecyclerView.OnScrollListener {
    private final RecyclerView recyclerView;
    private final int gradientHeight;

    public GradientScrollListener(RecyclerView recyclerView, int gradientHeight) {
        this.recyclerView = recyclerView;
        this.gradientHeight = gradientHeight;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        int itemCount = recyclerView.getAdapter().getItemCount();

        if (lastVisibleItemPosition == itemCount - 1) {
            // El último elemento es visible
            View lastVisibleItem = recyclerView.getLayoutManager().findViewByPosition(lastVisibleItemPosition);
            if (lastVisibleItem != null) {
                float opacity = calculateOpacity(lastVisibleItem.getHeight(), gradientHeight);
                int startColor = Color.TRANSPARENT;
                int endColor = Color.argb((int) (255 * opacity), 255, 255, 255);
                Shader shader = new LinearGradient(0, 0, 0, gradientHeight, startColor, endColor, Shader.TileMode.CLAMP);

                Drawable gradientDrawable = createGradientDrawable(shader);
                lastVisibleItem.setBackground(gradientDrawable);
            }
        }
    }

    private float calculateOpacity(int itemHeight, int gradientHeight) {
        float fadeStart = gradientHeight * 0.5f;
        float fadeEnd = gradientHeight * 1.5f;
        float opacity = 1f;

        if (itemHeight > fadeStart) {
            if (itemHeight < fadeEnd) {
                opacity = (itemHeight - fadeStart) / (fadeEnd - fadeStart);
            } else {
                opacity = 0f;
            }
        }

        return opacity;
    }

    private Drawable createGradientDrawable(Shader shader) {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
        shapeDrawable.getPaint().setShader(shader);
        return shapeDrawable;
    }
}

