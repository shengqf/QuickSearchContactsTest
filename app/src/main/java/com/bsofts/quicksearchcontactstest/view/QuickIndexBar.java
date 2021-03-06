package com.bsofts.quicksearchcontactstest.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bsofts.quicksearchcontactstest.Constants;
import com.bsofts.quicksearchcontactstest.R;
import com.bsofts.quicksearchcontactstest.util.SizeUtils;


/**
 * 根据拼音首字母快速查找联系人
 */
public class QuickIndexBar extends View {

    private static final String[] LETTERS = Constants.LETTERS;

    private Context mContext;
    private Paint mTextPaint;
    private Paint mBgPaint;
    private float mCellWidth;
    private float mCellHeight;
    private Rect mBounds;
    private int mTouchIndex = -1;
    private OnLetterUpdateListener mOnLetterUpdateListener;

    public QuickIndexBar(Context context) {
        super(context, null);
    }

    public QuickIndexBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initPaint();
    }

    private void initPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        //mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.main));
        //mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        //mTextPaint.setTextSize(SizeUtils.sp2px(14));

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(ContextCompat.getColor(mContext, R.color.text_tips));
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
        mCellHeight = height * 1.0f / LETTERS.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < LETTERS.length; i++) {
            String text = LETTERS[i];
            if (mBounds == null) {
                mBounds = new Rect();
            }
            /**
             * 该方法可以计算出字符串的准确高度和宽度，并缓存到bounds里面，
             * 这样当我们需要的时候，就可以直接从bounds里面取值了
             */
            mTextPaint.getTextBounds(text, 0, text.length(), mBounds);
            int textHeight = mBounds.height();
            float textWidth = mTextPaint.measureText(text);
            int x = (int) ((mCellWidth - textWidth) / 2.0f);
            int y = (int) (textHeight + (mCellHeight - textHeight) / 2.0f + i * mCellHeight);
            if (i == mTouchIndex) {
                mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.white));
                mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
                mTextPaint.setTextSize(SizeUtils.sp2px(16));
                canvas.drawCircle(mCellWidth / 2.0f, i * mCellHeight + mCellHeight / 2.0f, getRadius(mCellWidth, mCellHeight), mBgPaint);
            } else {
                mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.main));
                mTextPaint.setTypeface(Typeface.DEFAULT);
                mTextPaint.setTextSize(SizeUtils.sp2px(14));
            }
            canvas.drawText(LETTERS[i], x, y, mTextPaint);//第二、三个参数是字母左小角的x、y坐标
        }
    }

    private float getRadius(float width, float height) {
        if (width > height) {
            return height / 2.0f;
        }
        return width / 2.0f;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int oldIndex = mTouchIndex;
        int index = (int) (event.getY() / mCellHeight);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (index >= 0 && index < LETTERS.length) {
                    if (oldIndex != index && mOnLetterUpdateListener != null) {
                        mOnLetterUpdateListener.UpdateLetter(LETTERS[index]);
                        mTouchIndex = index;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (index >= 0 && index < LETTERS.length) {
                    if (index != mTouchIndex) {
                        if (oldIndex != index && mOnLetterUpdateListener != null) {
                            mOnLetterUpdateListener.UpdateLetter(LETTERS[index]);
                            mTouchIndex = index;
                            invalidate();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //mTouchIndex = -1;
                if (mOnLetterUpdateListener != null) {
                    mOnLetterUpdateListener.InvisibleTextView();
                }
                break;
        }
        return true;
    }

    public interface OnLetterUpdateListener {
        void UpdateLetter(String letter);

        void InvisibleTextView();
    }

    public void setOnLetterUpdateListener(OnLetterUpdateListener listener) {
        this.mOnLetterUpdateListener = listener;
    }

}
