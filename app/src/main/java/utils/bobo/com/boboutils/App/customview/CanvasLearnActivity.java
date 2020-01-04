package utils.bobo.com.boboutils.App.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bobohuang on 2019/10/18.
 */
public class CanvasLearnActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CanvasLearnView(this));
    }
    private static class CanvasLearnView extends View {
        public CanvasLearnView(Context context) {
            super(context);
        }

        public CanvasLearnView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public CanvasLearnView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public CanvasLearnView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
        @Override
        public void onDraw(Canvas canvas){
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            paint.setAlpha((int)(255 * 0.3));
            paint.setColor(0x80000000);
            paint.setPathEffect(new CornerPathEffect(20));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);

            super.onDraw(canvas);
            int top = 0;
            final int rectSize = 50;
            canvas.save();
            //1: 原点画矩形
            canvas.drawRect(new Rect(0, 0, rectSize, top + rectSize), paint);
            top += rectSize;
            //2: 画布放大后画矩形
            canvas.scale(2,2);
            canvas.drawRect(new Rect(0, 50, rectSize, top + rectSize), paint);
            top += rectSize;
            //3: 画布再放大后画矩形
            canvas.scale(2,2);
            canvas.drawRect(new Rect(0, 100, rectSize, top + rectSize), paint);
            canvas.restore();

            //画线
            canvas.save();
            canvas.translate(400, 0);
            int lineLen = 50;
            top = 50;
            //1:画线
            canvas.drawLine(0,top, lineLen, top, paint);
            //2:移动后划线
            canvas.translate(0, 50);
            canvas.drawLine(0,top, lineLen, top, paint);

            //3:画布先移动，再放大后画线
            canvas.translate(0, 50);
            canvas.scale(2,2);
            canvas.drawLine(0,top, lineLen, top, paint);
            //3:画布先放大，再移动，后画线
            canvas.translate(0, 50);
            canvas.drawLine(0,top, lineLen, top, paint);
            canvas.restore();

        }
    }

}
