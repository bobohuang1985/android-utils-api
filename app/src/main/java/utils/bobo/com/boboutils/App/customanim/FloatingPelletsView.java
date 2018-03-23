package utils.bobo.com.boboutils.App.customanim;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FloatingPelletsView extends View {
    private List<Pellet> mPelletList = new ArrayList<>();
    private Paint mPaint;
    private int mAnimIndexIndex;
    private boolean mIsRuning;
    private long mFrameIntervalMs;

    public FloatingPelletsView(Context context) {
        super(context);
    }

    public FloatingPelletsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public FloatingPelletsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public FloatingPelletsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 防抖动
        mPaint.setDither(true);
        // 开启图像过滤
        mPaint.setFilterBitmap(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsRuning) {
            for (Pellet pellet : mPelletList) {
                drawPellet(pellet, canvas, mPaint);
            }
        }
    }

    private void drawPellet(Pellet pellet, Canvas canvas, Paint paint) {
        Bitmap bitmap = pellet.getBitmap();
        if (bitmap == null) {
            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Rect srcRect = new Rect(0, 0, width, height);
        Rect destRect = new Rect();
        float x = pellet.getX(this, mAnimIndexIndex);
        float y = pellet.getY(this, mAnimIndexIndex);
        destRect.set((int) x, (int) y, (int) x + width, (int) y + height);
        canvas.save();
        canvas.scale(pellet.getScaleX(this, mAnimIndexIndex), pellet.getScaleY(this, mAnimIndexIndex));
        canvas.drawBitmap(bitmap, srcRect, destRect, paint);
        canvas.restore();
    }

    public void addPellet(Pellet pellet) {
        mPelletList.add(pellet);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mIsRuning) {
                mAnimIndexIndex++;
                postInvalidate();
                mHandler.sendEmptyMessageDelayed(0, mFrameIntervalMs);
            }
        }
    };

    /**
     * @param frameIntervalMs 帧间隔时间
     */
    public void startAnimation(int frameIntervalMs) {
        mFrameIntervalMs = frameIntervalMs;
        mIsRuning = true;
        mAnimIndexIndex = 0;
        postInvalidate();
        mHandler.sendEmptyMessageDelayed(0, mFrameIntervalMs);
    }

    public static abstract class Pellet {
        /**
         * @param view
         * @param animIndex 动画到第几帧
         * @return
         */
        public abstract float getX(FloatingPelletsView view, int animIndex);

        public abstract float getY(FloatingPelletsView view, int animIndex);

        public abstract float getScaleX(FloatingPelletsView view, int animIndex);

        public abstract float getScaleY(FloatingPelletsView view, int animIndex);

        public abstract Bitmap getBitmap();
    }
}
