package com.name.social_helper_r_p;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.RequiresApi;

import com.name.social_helper_r_p.R;

import java.util.logging.Handler;

public class Loader extends View {
    int width = 400;
    int random1 = 0, random2 = 340;
    Runnable runnable = null;
    DisplayMetrics metrics = null;
    Handler handler;
    int color = 0;

    int rotate = 0;

    public Loader(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setBackgroundColor(Color.TRANSPARENT);
        //System.out.println("PRINT");
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.loader);
        color = a.getColor(R.styleable.loader_progress_color, 0);

        ValueAnimator animator = ValueAnimator.ofInt(0, 360);
        animator.setRepeatCount(Animation.INFINITE);
        animator.setDuration(800);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rotate = (int) animator.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint line_settings = new Paint(Paint.ANTI_ALIAS_FLAG);
        line_settings.setColor(getContext().getColor(R.color.button_1));
        line_settings.setStrokeWidth(width/20);
        line_settings.setStrokeCap(Paint.Cap.ROUND);
        line_settings.setStyle(Paint.Style.STROKE);


        /*canvas.drawArc(12,12,width-12, width-12, 180, 90,
                false,line_settings );*/

        canvas.save();
        canvas.rotate(rotate, width/2, width/2);
        canvas.drawArc((width/10),(width/10),width-(width/10), width-(width/10), 0, 90,
                false,line_settings );
        canvas.restore();
        canvas.save();
        canvas.rotate(-rotate, width/2, width/2);
        canvas.drawArc((2*width/10),(2*width/10),width-(2*width/10), width-(2*width/10), 180, 90,
                false,line_settings );
        canvas.restore();
       // canvas.drawLine(0, 0, random1, random2, line_settings);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width);
    }



}
