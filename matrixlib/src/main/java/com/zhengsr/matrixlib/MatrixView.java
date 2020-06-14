package com.zhengsr.matrixlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author by zhengshaorui on 2020/6/13
 * Describe:
 */
public class MatrixView extends View {
    private static final String TAG = "MatrixView";
    private static final int STROKE = 1;
    private static final int LINE = 2;

    private Matrix mMatrix;
    private int mWidth, mHeight;
    private Bitmap mSrcBitmap;
    private RectF mRectF;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    /**
     * attrs
     */
    private int mType;
    private float mRadius;

    /**
     * logic
     */
    private float mLastX,mLastY;


    public MatrixView(Context context) {
        this(context, null);
    }

    public MatrixView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatrixView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MatrixView);
        int res = ta.getResourceId(R.styleable.MatrixView_mv_res, -1);
        if (res != -1) {
            mSrcBitmap = BitmapFactory.decodeResource(getResources(), res);
        }
        int color = ta.getColor(R.styleable.MatrixView_mv_out_color, Color.WHITE);
        mType = ta.getInteger(R.styleable.MatrixView_mv_line_type, LINE);
        mRadius = ta.getDimensionPixelOffset(R.styleable.MatrixView_mv_circle_size, 1);
        ta.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(1.5f);
        mPaint.setStyle(Paint.Style.STROKE);
        mRectF = new RectF();
        mMatrix = new Matrix();

        setClickable(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        if (mSrcBitmap != null) {
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(mBitmap);
            }
            int dw = mSrcBitmap.getWidth();
            int dh = mSrcBitmap.getHeight();
            float offset = mRadius + mPaint.getStrokeWidth();
            mRectF.set(offset, offset, w - offset, h - offset);
            //清屏
            mCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            //画图
            mCanvas.drawBitmap(mSrcBitmap, null, mRectF, null);
            offset = mRadius;
            mRectF.set(offset, offset, w - offset, h - offset);
            mCanvas.drawRect(mRectF, mPaint);

            mCanvas.save();
            mPaint.setStyle(Paint.Style.FILL);
            mCanvas.drawCircle(mRectF.left, mRectF.top, mRadius, mPaint);
            mCanvas.drawCircle(mRectF.left, mRectF.bottom, mRadius, mPaint);
            mCanvas.drawCircle(mRectF.right, mRectF.bottom, mRadius, mPaint);
            mCanvas.drawCircle(mRectF.right, mRectF.top, mRadius, mPaint);
            mCanvas.restore();

        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, mMatrix, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int count = event.getPointerCount();
        //拿到多指的中心点
        /*float x = 0, y = 0;
        for (int i = 0; i < count; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        if (count > 1) {
            x /= count;
            y /= count;
        }*/
        float x = event.getX();
        float y = event.getY();


        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;
                mMatrix.postTranslate(dx,dy);
                mMatrix.mapRect(mRectF);
                mLastX = x;
                mLastY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }


        return true;
    }
}
