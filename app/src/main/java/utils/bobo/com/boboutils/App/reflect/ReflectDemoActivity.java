package utils.bobo.com.boboutils.App.reflect;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.bobo.commonutils.utils.ProxyUtil;

import utils.bobo.com.boboutils.App.customview.longdrawable.LongScreenshotScrollView;
import utils.bobo.com.boboutils.R;

/**
 * Created by huangzb1 on 2018/7/24.
 */

public class ReflectDemoActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);
        TextView textView = (TextView) findViewById(R.id.textView);
        try {
            boolean isSame = ProxyUtil.isOverrideMethod("onTouchEvent",
                    LongScreenshotScrollView.class,
                    View.class, MotionEvent.class);
            String text = "LongScreenshotScrollView View onTouchEvent same:"+isSame;

            isSame = ProxyUtil.isOverrideMethod("onTouchEvent",
                    CustomLongScreenshotScrollView.class,
                    View.class, MotionEvent.class);
            text += "\r\nCustomLongScreenshotScrollView View onTouchEvent same:"+isSame;

            isSame = ProxyUtil.isOverrideMethod("onTouchEvent",
                    CustomView.class,
                    View.class, MotionEvent.class);
            text += "\r\nCustomView View onTouchEvent same:"+isSame;
            isSame = ProxyUtil.isOverrideMethod("onTouchEvent",
                    CustomView2.class,
                    View.class, MotionEvent.class);
            text += "\r\nCustomView2 View onTouchEvent same:"+isSame;
            textView.setText(text);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    private static class CustomLongScreenshotScrollView extends LongScreenshotScrollView{

        public CustomLongScreenshotScrollView(Context context) {
            super(context);
        }

        public CustomLongScreenshotScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomLongScreenshotScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public CustomLongScreenshotScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }
    private static class CustomView extends View{

        public CustomView(Context context) {
            super(context);
        }

        public CustomView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }
    private static class CustomView2 extends CustomView{

        public CustomView2(Context context) {
            super(context);
        }

        public CustomView2(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomView2(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public CustomView2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }
}
