package com.OrderSuperfast;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FadeItemDecoration extends RecyclerView.ItemDecoration {
    private final Paint gradientPaint;
    private final int gradientHeight;
    private final int totalHeight;
    private int totalChilds;
    private Rect recta;

    public FadeItemDecoration(int totalHeight) {
        this.gradientHeight = 170;
        gradientPaint = new Paint();
        this.totalHeight=totalHeight;

    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        int childCount = parent.getChildCount();

        totalChilds=parent.getAdapter().getItemCount();
        int width = parent.getWidth();
        int height = parent.getHeight();

        if (childCount > 0) {

            int lastVisiblePosition = findLastVisiblePosition(parent);
            Log.d(" childs ", lastVisiblePosition +" and "+ parent.getAdapter().getItemCount());
            if(lastVisiblePosition!=parent.getAdapter().getItemCount()-1) {
                Log.d("position", String.valueOf(lastVisiblePosition));
                if (lastVisiblePosition == RecyclerView.NO_POSITION) {
                    return;
                }
                Log.d("fade", String.valueOf(parent.getChildCount()));
                View lastChild = parent.findViewHolderForAdapterPosition(lastVisiblePosition).itemView;
                recta = new Rect();
                boolean isVisible = lastChild.getGlobalVisibleRect(recta);


                int bottom = lastChild.getBottom();
                float opacity = calculateOpacity(lastChild, height, gradientHeight);
                int opacidad=(int) (opacity*255);
                int top = lastChild.getBottom() - gradientHeight;

                System.out.println("opacidad int "+opacidad);
                gradientPaint.setShader(new LinearGradient(
                        0f, top, 0f, bottom,
                        Color.TRANSPARENT, Color.argb(opacidad, 255, 255, 255), Shader.TileMode.CLAMP));

                canvas.drawRect(0, top, width, bottom, gradientPaint);
            }

        }
    }

    private float calculateOpacity(View lastChild, int recyclerViewHeight, int gradientHeight) {
        int bottom = lastChild.getBottom();
        int lastChildHeight = lastChild.getHeight();
        int childHeihgts=lastChildHeight*totalChilds;
        float distanceFromBottom = recyclerViewHeight;
        float fadeStart = gradientHeight * 0.2f; // Gradiente comienza a aparecer cuando el último elemento está a la mitad de la altura del gradiente
        float fadeEnd = gradientHeight +recyclerViewHeight; // Gradiente se muestra completamente cuando el último elemento está a una y media veces la altura del gradiente
        float opacity = 1f;


        Log.d("fade",String.valueOf(gradientHeight));
        Log.d("fade",String.valueOf(recta.height()));

        System.out.println("opacidad" +gradientHeight+ " "+recta.height());

        if(recyclerViewHeight<=childHeihgts) {

            System.out.println("ASFASFAFS");
            if (gradientHeight+140 > recta.height()) {
                double d = (float) recta.height()/((float) gradientHeight+140f);

                System.out.println("d es = a "+d);
                opacity = 2f* (float) d;
                System.out.println("opacidad" + opacity);
            } else {
                opacity = 1f;
            }

        }

        return opacity;
    }

    private int findLastVisiblePosition(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            return linearLayoutManager.findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            return gridLayoutManager.findLastVisibleItemPosition();
        }
        // Handle other layout managers if needed

        return RecyclerView.NO_POSITION;
    }
}
