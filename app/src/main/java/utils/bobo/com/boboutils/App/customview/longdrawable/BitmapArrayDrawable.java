package utils.bobo.com.boboutils.App.customview.longdrawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangzb1 on 2018/7/10.
 */

public class BitmapArrayDrawable extends Drawable {
    private List<Bitmap> mBitmapList = new ArrayList<>();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        Rect localRect = getBounds();
        if (localRect != null) {
            canvas.translate(localRect.left, localRect.top);
        }
        for (int i = 0; i < mBitmapList.size(); i++){
            Bitmap bitmap = mBitmapList.get(i);
            if (!canvas.quickReject(0.0F, 0.0F, bitmap.getWidth(), bitmap.getHeight(),
                    Canvas.EdgeType.BW)) {
                canvas.drawBitmap(bitmap, 0.0F, 0.0F, this.mPaint);
            }
            canvas.translate(0.0F, bitmap.getHeight());
        }
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
    public void addBitmap(Bitmap bitmap){
        mBitmapList.add(bitmap);
    }
    public void clearAllBitmaps(){
        mBitmapList.clear();
    }
}
