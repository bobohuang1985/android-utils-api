package utils.bobo.com.boboutils.App.customview.longdrawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * Created by huangzb1 on 2018/7/10.
 */

public class LongScreenshotScrollView extends View {
    private final static int STATUS_IDLE = 0;
    private final static int STATUS_TOUCHED_SCROLLING = 1;//touched moving
    //srcolling by inertia after touch up and have velocity
    private final static int STATUS_SCROLLING_BY_INERTIA = 2;
    private Scroller mScroller;
    private float mLastTouchX, mLastTouchY;
    private float mScrollerY = 0;
    private int mBitmapWidth;
    private int mTotalBitmapHeight;
    private BitmapArrayDrawable mBitmapArrayDrawable = new BitmapArrayDrawable();
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private int mCurStatus = STATUS_IDLE;
    private float mAutoScrollStepOffset = 1;
    private boolean mIsAutoScrollStoped = true;

    public LongScreenshotScrollView(Context context) {
        this(context, null);
    }

    public LongScreenshotScrollView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LongScreenshotScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LongScreenshotScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        mScroller = new Scroller(this.getContext());
        mAutoScrollStepOffset =  (int)(2.0f * getResources().getDisplayMetrics().density);
        if(mAutoScrollStepOffset < 1){
            mAutoScrollStepOffset = 1;
        }
    }
    private int getHeightInner() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }
    private int getWidthInner() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }
    public void addBitmap(Bitmap bitmap){
        if (mBitmapWidth == 0){
            mBitmapWidth = bitmap.getWidth();
        }
        mTotalBitmapHeight += bitmap.getHeight();
        mBitmapArrayDrawable.addBitmap(bitmap);
        invalidate();
    }
    public void clearAllBitmaps(){
        mBitmapWidth = 0;
        mTotalBitmapHeight = 0;
        mBitmapArrayDrawable.clearAllBitmaps();
        invalidate();
    }
    private void autoScroll(float offset){
        computeScrollY(-offset);
    }
    public void startAutoScrollAnim(){
        mIsAutoScrollStoped = false;
        post(mAutoScrollRunnable);
    }
    public void stopAutoScrollAnim(){
        mIsAutoScrollStoped = true;
        removeCallbacks(mAutoScrollRunnable);
    }
    private Runnable mAutoScrollRunnable = new Runnable() {
        public void run() {
            if (mIsAutoScrollStoped) {
                return;
            }
            autoScroll(mAutoScrollStepOffset);
            post(mAutoScrollRunnable);
        }
    };
    public int getCurCropTotalHeight(){
        float scrollerY = -mScrollerY;
        float scale = (float)getWidthInner()/(float)mBitmapWidth;
        scrollerY = scrollerY + getHeightInner();
        return (int) (scrollerY/scale);
    }
    private void computeScrollY(float offset){
        float scrollerY = mScrollerY+offset;
        if(scrollerY > 0.0f){
            scrollerY = 0;
        }else {
            float scale = (float)getWidthInner()/(float)mBitmapWidth;
            float maxHeight = (float) mTotalBitmapHeight*scale;
            float minOffset = maxHeight-getHeightInner();
            minOffset = minOffset>0 ? (-minOffset):0;
            if (scrollerY < minOffset){
                scrollerY = minOffset;
            }
        }
        if(mScrollerY == scrollerY){
            return;
        }
        mScrollerY = scrollerY;
        invalidate();
    }
    private int getMaxScrollY(){
        float scale = (float)getWidthInner()/(float)mBitmapWidth;
        float maxHeight = (float) mTotalBitmapHeight*scale;
        int viewHeight = getHeightInner();
        if (maxHeight > viewHeight){
            return (int)(maxHeight-viewHeight);
        }else{
            return 0;
        }
    }
    private int getVelocityY(int maxVelocity) {
        int velocity = (int)mVelocityTracker.getYVelocity();
        return Math.min(maxVelocity, Math.max(-maxVelocity, velocity));
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mCurStatus = STATUS_TOUCHED_SCROLLING;
                stopAutoScrollAnim();
                if(!mScroller.isFinished()){
                    mScroller.forceFinished(true);
                }

                if(mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                computeScrollY(y - mLastTouchY);
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(mCurStatus == STATUS_TOUCHED_SCROLLING) {
                    mCurStatus = STATUS_SCROLLING_BY_INERTIA;
                    mVelocityTracker.computeCurrentVelocity(1000);
                    mScroller.setFriction(ViewConfiguration.getScrollFriction());
                    mScroller.fling(0, (int) (-mScrollerY), 0, -getVelocityY(20000),
                            0, 0, 0, getMaxScrollY());
                    invalidate();
                }
                mVelocityTracker.clear();
                break;
        }
        mLastTouchX = x;
        mLastTouchY = y;
        return true;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmapWidth == 0){
            return;
        }
        canvas.save();
        switch (mCurStatus){
            case STATUS_IDLE:
            case STATUS_TOUCHED_SCROLLING:
                break;
            case STATUS_SCROLLING_BY_INERTIA:
                if (!mScroller.computeScrollOffset()){
                    mCurStatus = STATUS_IDLE;
                }
                mScrollerY = -mScroller.getCurrY();
                break;
        }
        canvas.translate(getPaddingLeft(), mScrollerY);
        float scale = (float)getWidthInner()/(float)mBitmapWidth;
        canvas.scale(scale,scale);
        mBitmapArrayDrawable.draw(canvas);
        canvas.restore();
        switch (mCurStatus){
            case STATUS_SCROLLING_BY_INERTIA:
                invalidate();
                break;
        }
    }
}
