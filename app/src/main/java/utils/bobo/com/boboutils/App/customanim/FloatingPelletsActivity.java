package utils.bobo.com.boboutils.App.customanim;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import utils.bobo.com.boboutils.R;

/**
 * Created by huangzb1 on 2018/2/23.
 */

public class FloatingPelletsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_pellets);
        FloatingPelletsView floatingPelletsView = (FloatingPelletsView) this.findViewById(R.id.floatingPelletsView);
        floatingPelletsView.addPellet(new FirstPellet(((BitmapDrawable)getDrawable(R.drawable.ic_launcher)).getBitmap()));
        floatingPelletsView.startAnimation(100);
    }
    private class FirstPellet extends  FloatingPelletsView.Pellet{
        private Bitmap mBitmap;
        FirstPellet(Bitmap bitmap){
            mBitmap = bitmap;
        }
        @Override
        public float getX(FloatingPelletsView view, int animIndex) {
            return view.getWidth()*(animIndex%10)/10;
        }

        @Override
        public float getY(FloatingPelletsView view, int animIndex) {
            return 0;
        }

        @Override
        public float getScaleX(FloatingPelletsView view, int animIndex) {
            return 1;
        }

        @Override
        public float getScaleY(FloatingPelletsView view, int animIndex) {
            return 1;
        }

        @Override
        public Bitmap getBitmap() {
            return mBitmap;
        }
    }
}
