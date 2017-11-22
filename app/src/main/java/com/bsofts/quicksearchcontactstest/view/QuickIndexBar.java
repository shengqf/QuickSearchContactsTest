package com.bsofts.quicksearchcontactstest.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bsofts.quicksearchcontactstest.util.SizeUtils;


/**
 * 根据拼音首字母快速查找联系人
 */

public class QuickIndexBar extends View {

    private static final String TAG = QuickIndexBar.class.getSimpleName();

    private static final String[] LETTERS = new String[]{
            "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X",
            "Y", "Z"};

    private Paint mPaint;
    private float mCellWidth;
    private float mCellHeight;
    private Rect mBounds;
    private int mTouchIndex = -1;

    public QuickIndexBar(Context context) {
        super(context,null);
    }

    public QuickIndexBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
//        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setTextSize(SizeUtils.sp2px(16));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mCellWidth = width;
        mCellHeight = height*1.0f/LETTERS.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < LETTERS.length; i++) {
            String text = LETTERS[i];
            if (mBounds == null){
                mBounds = new Rect();
            }
            /**
             * 该方法可以计算出字符串的准确高度和宽度，并缓存到bounds里面，
             * 这样当我们需要的时候，就可以直接从bounds里面取值了
             */
            mPaint.getTextBounds(text, 0, text.length(), mBounds);
            int textHeight = mBounds.height();
            float textWidth = mPaint.measureText(text);
            int x = (int) ((mCellWidth - textWidth) / 2.0f);
            int y = (int) (textHeight + (mCellHeight - textHeight)/ 2.0f + i * mCellHeight);
            mPaint.setColor(mTouchIndex == i ? Color.WHITE : Color.BLACK);
            //第二、三个参数是字母左小角的x、y坐标
            canvas.drawText(LETTERS[i],x,y, mPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                index = (int) (event.getY() / mCellHeight);
                if(index >= 0 && index < LETTERS.length){
                    if (listener != null){
                        listener.UpdataLetter(LETTERS[index]);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                index = (int) (event.getY() / mCellHeight);
                if(index >= 0 && index < LETTERS.length){
                    if (index != mTouchIndex){
                        //mTouchIndex = index;
                        if (listener != null){
                            listener.UpdataLetter(LETTERS[index]);
                        }
                        Log.d(TAG, "onTouchEvent: " + LETTERS[index]);
                        mTouchIndex = index;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mTouchIndex = -1;
                if (listener != null){
                    listener.InvisiableTextView();
                }
                break;
        }
        return true;
    }

    public interface OnLetterUpdataListener{
        void UpdataLetter(String letter);
        void InvisiableTextView();
    }

    private OnLetterUpdataListener listener;

    public void setOnLetterUpdataListener(OnLetterUpdataListener listener){
        this.listener = listener;
    }

}
