package com.thl.verticalscaleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

public class VerticalScaleView extends View {
    private Context mContext;
    private Paint centerLinePaint;
    private Paint grayLinePaint;
    private Paint txtPaint;
    private int space = 30;
    private int startValue = 0;
    private int endValue = 255;
    private int value = 0;
    private int width;
    private int height;
    private float mLastY;
    private int touchSlop;
    private Scroller mScroller;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int maxScrollX = 1000; // 最大允许滑出范围
    private int currentOffset; // 当前偏移
    private VelocityTracker mVelocityTracker;
    private boolean isFastScroll;
    private ScaleCallback mListener;
    private int number;
    private int BASELINE_OFFSET;
    private int interval;
    private int textOffset;

    private int maxScaleLength;//大刻度长度
    private int midScaleLength;//中刻度长度
    private int minScaleLength;//小刻度长度

    public VerticalScaleView(Context context) {
        this(context, null);
    }

    public VerticalScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        centerLinePaint = new Paint();
        centerLinePaint.setAntiAlias(true);
        centerLinePaint.setColor(getResources().getColor(R.color.yellow));
        centerLinePaint.setStrokeWidth(5);

        grayLinePaint = new Paint();
        grayLinePaint.setAntiAlias(true);
        grayLinePaint.setColor(getResources().getColor(R.color.age_text));
        grayLinePaint.setStrokeWidth(5);

        txtPaint = new Paint();
        txtPaint.setAntiAlias(true);
        txtPaint.setColor(getResources().getColor(R.color.age_text));
        txtPaint.setTextSize(45);

        // 新增部分 start
        ViewConfiguration viewConfiguration = ViewConfiguration.get(mContext);
        touchSlop = viewConfiguration.getScaledTouchSlop();
        mScroller = new Scroller(mContext);
        mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        // 新增部分 end
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            width = (int) (mContext.getResources().getDisplayMetrics().density * 200 + 0.5);
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = mContext.getResources().getDisplayMetrics().heightPixels;
        }
        setMeasuredDimension(width, height);

        BASELINE_OFFSET = height / 2;
        int x = (number - startValue) * space - BASELINE_OFFSET + BASELINE_OFFSET % space;
        if (x % space != 0) {
            x -= x % space;
        }

        int w = getMeasuredWidth();
        Log.e("taohaili", "width:" + w);
        maxScaleLength = width / 7;
        midScaleLength = width / 8;
        minScaleLength = maxScaleLength / 2;

        scrollTo(0, x);
        computeAndCallback(x);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredWidth = getMeasuredWidth();
        Log.e("taohaili","startValue:"+startValue);
        Log.e("taohaili","endValue:"+endValue);
        txtPaint.setTextSize(45);
        for (int i = startValue; i < endValue + 1; i++) {
            if (i > value) {
                txtPaint.setColor(getResources().getColor(R.color.red));
                grayLinePaint.setColor(getResources().getColor(R.color.red));
            } else {
                txtPaint.setColor(getResources().getColor(R.color.bule));
                grayLinePaint.setColor(getResources().getColor(R.color.bule));
            }
            int lineHeight = minScaleLength;
            if (i % interval == 0) {
                lineHeight = maxScaleLength;
                int y = (i - startValue) * space;
                if (y > 0 || y < height) {
                    canvas.drawText(String.valueOf(i), measuredWidth / 2 - 2*lineHeight, y + textOffset, txtPaint);
                }
            } else if (i % 5 == 0) {
                lineHeight = midScaleLength;
            }
            int startY = (i - startValue) * space;
            if (startY > 0 || startY < height) {
                //从控件宽度-linHeight画到控件宽度
                canvas.drawLine(measuredWidth / 2 - lineHeight, startY, measuredWidth / 2 + lineHeight, startY, grayLinePaint);
            }
        }


        centerLinePaint.setStrokeWidth(10);
        int startX = BASELINE_OFFSET + getScrollY() - BASELINE_OFFSET % space;
        Log.e("taohaili","maxScaleLength:"+maxScaleLength);

        //画指针线
        canvas.drawLine(measuredWidth / 2-maxScaleLength-20, startX, measuredWidth / 2+maxScaleLength+20, startX, centerLinePaint);

        centerLinePaint.setColor(getResources().getColor(R.color.blue));
        centerLinePaint.setStyle(Paint.Style.FILL);
        Path path = new Path();
        path.moveTo(measuredWidth / 2+maxScaleLength+40, startX - 10);
        path.lineTo(measuredWidth / 2+maxScaleLength+40, startX + 10);
        path.lineTo(measuredWidth / 2+maxScaleLength +25, startX);
        path.close();
        canvas.drawPath(path, centerLinePaint);

        //画圆角矩形
        centerLinePaint.setStyle(Paint.Style.FILL);
        RectF r = new RectF();
        r.left = measuredWidth / 2+maxScaleLength+40;
        r.top = startX  - 45;
        r.right = measuredWidth -60;
        r.bottom = startX  + 45;
        canvas.drawRoundRect(r, 10, 10, centerLinePaint);

        centerLinePaint.setStyle(Paint.Style.STROKE);

        txtPaint.setColor(getResources().getColor(R.color.white));
        txtPaint.setTextSize(60);
        String value = String.valueOf(this.value) ;
        Rect rect2 = new Rect();
        txtPaint.getTextBounds(value, 0, value.length(), rect2);
        int w2 = rect2.width();
        int h2 = rect2.height();
        canvas.drawText(value, measuredWidth / 2+maxScaleLength+60, startX + h2 / 2, txtPaint);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        obtainVelocityTracker();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = event.getY();
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isFastScroll = false;
                float moveY = event.getY();
                currentOffset = (int) (moveY - mLastY);
                scrollTo(0, getScrollY() - currentOffset);
                computeAndCallback(getScrollY());
                mLastY = moveY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) mVelocityTracker.getYVelocity();
                if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                    isFastScroll = true;
                    flingY(-initialVelocity);
                } else {
                    int y = getScrollY();
                    if (y % space != 0) {
                        y -= y % space;
                    }
                    if (y < -BASELINE_OFFSET) {
                        y = -BASELINE_OFFSET + BASELINE_OFFSET % space;
                    } else if (y > (endValue - startValue) * space - BASELINE_OFFSET) {
                        y = (endValue - startValue) * space - BASELINE_OFFSET + BASELINE_OFFSET % space;
                    }
                    scrollTo(0, y);
                    computeAndCallback(y);
                }
                releaseVelocityTracker();
                break;
        }
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
        }
        return true;
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrY();
            scrollTo(0, x);
            computeAndCallback(x);
            postInvalidate();
        } else {
            if (isFastScroll) {
                int x = mScroller.getCurrY() + BASELINE_OFFSET % space;
                if (x % space != 0) {
                    x -= x % space;
                }
                scrollTo(0, x);
                computeAndCallback(x);
                postInvalidate();
            }
        }
    }


    /**
     * 释放 速度追踪器
     */
    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    /**
     * 初始化 速度追踪器
     */
    private void obtainVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    /**
     * 惯性滑动
     */
    public void flingY(int velocityY) {
        mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, -BASELINE_OFFSET, (endValue - startValue) * space - BASELINE_OFFSET);
        awakenScrollBars(mScroller.getDuration());
        invalidate();
    }


    /**
     * 计算并回调位置信息
     */
    private void computeAndCallback(int scrollX) {
        if (mListener != null) {
            int finalX = BASELINE_OFFSET + scrollX;
            if (finalX % space != 0) {
                finalX -= finalX % space;
            }
            this.value = startValue + finalX / space;
            mListener.onRulerSelected((endValue - startValue), this.value);
        }
    }

    public void setRuleListener(ScaleCallback mListener) {
        this.mListener = mListener;
    }

    /**
     * 设置number的值
     */
    public void setNumber(int number) {
        this.number = number;
        this.value = number;
    }


    /**
     * 设置number的值
     */
    public void setValue(int number) {
        this.number = number;
        this.value = number;
        int x = (number - startValue) * space - BASELINE_OFFSET + BASELINE_OFFSET % space;
        if (x % space != 0) {
            x -= x % space;
        }
        scrollTo(0, x);
        computeAndCallback(x);

    }

    /**
     * 设置刻度尺的最小值
     */
    public void setMin(int min) {
        this.startValue = min;
    }

    /**
     * 设置刻度尺的最大值
     */
    public void setMax(int max) {
        this.endValue = max;
    }

    /**
     * 设置刻度尺的数字显示间距值
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setTextOffset(int textOffset) {
        this.textOffset = textOffset;
    }


    public interface ScaleCallback {
        void onRulerSelected(int length, int value);
    }

}
