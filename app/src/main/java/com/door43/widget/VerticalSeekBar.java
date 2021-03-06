package com.door43.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar {

    private OnSeekBarChangeListener myListener;
    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener){
        this.myListener = mListener;
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(myListener!=null) {
                    myListener.onStartTrackingTouch(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                if(myListener != null) {
                    myListener.onProgressChanged(this, getMax() - (int) (getMax() * event.getY() / getHeight()), true);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(myListener != null) {
                    myListener.onStopTrackingTouch(this);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Store the actual progress (not the internal inverted progress), to allow restoring as
        // HorizontalScrollBar, which is not inverted. Do this by temporarily removing the inversion
        // prior to saving the instance state.
        super.setProgress(getMax() - getProgress());
        Parcelable result = super.onSaveInstanceState();
        super.setProgress(getMax() - getProgress());
        return result;
    }

    @Override
    public void onRestoreInstanceState(Parcelable instanceState) {
        super.onRestoreInstanceState(instanceState);

        // Since the instance state is saved without being inverted, restore the inverted internal
        // format on restore.
        super.setProgress(getMax() - getProgress());
    }
}