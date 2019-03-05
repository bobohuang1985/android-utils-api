package utils.bobo.com.boboutils.App.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.morgoo.helper.Log;

import java.io.IOException;
import java.io.InputStream;

import utils.bobo.com.boboutils.R;

public class CustomAccessibilityActivity extends Activity {
    private static final String TAG = "CustomAccessibilityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cutom_accessibility);
        ViewGroup root = (ViewGroup) findViewById(R.id.root);
        CustomView customView = new CustomView(this);
        customView.setImageResource(R.drawable.ic_launcher);
        customView.setClickable(true);
        customView.setFocusable(true);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        root.addView(customView, layoutParams);
        customView.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        Log.d(TAG, "onHover ACTION_HOVER_ENTER");
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        Log.d(TAG, "onHover ACTION_HOVER_MOVE");
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        Log.d(TAG, "onHover ACTION_HOVER_EXIT");
                        break;
                    default:
                        Log.d(TAG, "onHover event.getAction() = " + event.getAction());
                        break;
                }
                return false;
            }
        });
    }

    public static class CustomView extends ImageView {

        public CustomView(Context context) {
            super(context);
        }

        public CustomView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        boolean mDownTouch = false;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);

            // Listening for the down and up touch events
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "onTouchEvent Down");
                    mDownTouch = true;
                    return true;

                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "onTouchEvent UP");
                    if (mDownTouch) {
                        mDownTouch = false;
                        performClick(); // Call this method to handle the response, and
                        // thereby enable accessibility services to
                        // perform this action for a user who cannot
                        // click the touchscreen.
                        return true;
                    }
            }
            return false; // Return false for other touch events
        }
    }
}
