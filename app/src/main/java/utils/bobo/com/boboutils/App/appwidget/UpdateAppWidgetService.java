package utils.bobo.com.boboutils.App.appwidget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileOutputStream;

import utils.bobo.com.boboutils.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UpdateAppWidgetService extends IntentService {
    private static final String ACTION_UPDATE_APP_WIDGET = "utils.bobo.com.boboutils.App.appwidget.action.UPDATE_APP_WIDGET";

    public UpdateAppWidgetService() {
        super("UpdateAppWidgetService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateAppWidget(Context context) {
        Intent intent = new Intent(context, UpdateAppWidgetService.class);
        intent.setAction(ACTION_UPDATE_APP_WIDGET);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_APP_WIDGET.equals(action)) {
                handleActionUpdateAppWidget();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateAppWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName thisWidget = new ComponentName(this, CustomViewAppWidget.class);
        int ids[] = appWidgetManager.getAppWidgetIds(thisWidget);
        if (ids == null || ids.length <= 0) {
            return;
        }
        int width = this.getResources().getDimensionPixelSize(R.dimen.custom_app_widget_image_width);
        int height = this.getResources().getDimensionPixelSize(R.dimen.custom_app_widget_image_height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.custom_app_widget_line_color));
        paint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.custom_app_widget_line_width));
        canvas.drawLine(0, 0, width / 2, height, paint);
        canvas.drawLine(width / 2, height, width, 0, paint);
        String path = saveBitmapToPic(this, "customview.png", bitmap);
        bitmap.recycle();
        File newFile = new File(path);
        Uri imageUri = CustomViewFileProvider.getUriForFile(this, CustomViewFileProvider.AUTHORITIES, newFile);

        CharSequence widgetText = getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.custom_view_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setImageViewUri(R.id.appwidget_image, imageUri);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(thisWidget, views);
    }

    private String saveBitmapToPic(Context context, String name, Bitmap b) {
        if (b == null) {
            return null;
        }
        context.deleteFile(name);
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(name, Context.MODE_PRIVATE);
            //fos = new FileOutputStream(screenShotFile,Context.MODE_WORLD_READABLE);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                return context.getFilesDir() + "/" + name;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
