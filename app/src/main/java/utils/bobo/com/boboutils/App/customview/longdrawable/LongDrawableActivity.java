package utils.bobo.com.boboutils.App.customview.longdrawable;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bobo.commonutils.file.AESHelper;
import com.morgoo.helper.Log;

import java.io.IOException;
import java.io.InputStream;

import utils.bobo.com.boboutils.R;

public class LongDrawableActivity extends Activity implements View.OnClickListener {
    private static final int MAX_IMAGE_INDEX = 19;
    private static final String TAG = "LongDrawableActivity";
    private LongScreenshotScrollView mScrollView;
    private HandlerThread mHandlerThread = new HandlerThread("LongDrawableActivity");
    private Handler mBgHandler;
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_long_drawable);
        mScrollView = (LongScreenshotScrollView)findViewById(R.id.scrollView);
        mHandlerThread.start();
        mBgHandler = new Handler(mHandlerThread.getLooper());
        mBgHandler.post(new LoadingRunnable(1));
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1: {
                break;
            }
        }
    }
    private class LoadingRunnable implements Runnable {
        private final int mIndex;
        LoadingRunnable(int index){
            mIndex = index;
        }
        @Override
        public void run() {
            InputStream is= null;
            try {
                is = getAssets().open("part_"+mIndex+".jpg");
                final Bitmap bitmap= BitmapFactory.decodeStream(is);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.addBitmap(bitmap);
                        if(mIndex == 1){
                            mScrollView.startAutoScrollAnim();
                        }
                    }
                });
                if (mIndex < MAX_IMAGE_INDEX){
                    mBgHandler.postDelayed(new LoadingRunnable(mIndex+1),100);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
