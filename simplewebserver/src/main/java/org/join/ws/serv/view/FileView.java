package org.join.ws.serv.view;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.entity.FileEntity;
import org.join.ws.Constants.Config;
import org.join.ws.serv.GzipFilter;
import org.join.ws.serv.entity.GzipFileEntity;
import org.join.ws.serv.support.GzipUtil;
import org.join.ws.serv.support.MIME;

import android.util.Log;

/**
 * 文件视图渲染
 * @author join
 */
public class FileView extends BaseView<File, String> {

    static final String TAG = "FileView";
    static final boolean DEBUG = false || Config.DEV_MODE;

    /**
     * @details contentType为null时，默认通过{@link MIME#getMimeType(File)}获取且charset为{@link Config#ENCODING}
     * @param contentType 文件响应类型
     * @see BaseView#render(Object, Object)
     */
    @Override
    public HttpEntity render(HttpRequest request, final File file, String contentType)
            throws IOException {
        if (contentType == null) {
            String mine = MIME.getFromFile(file);
            contentType = null == mine ? "charset=" + Config.ENCODING : mine + ";charset="
                    + Config.ENCODING;
        }
        if (Config.USE_GZIP && GzipUtil.getSingleton().isGZipSupported(request)
                && GzipFilter.isGzipFile(file)) {
            if (Config.USE_FILE_CACHE) {
                File cacheFile = new File(Config.FILE_CACHE_DIR, file.getName() + Config.EXT_GZIP);
                if (cacheFile.exists()) {
                    if (DEBUG)
                        Log.d(TAG, "Read from cache " + cacheFile);
                } else {
                    GzipUtil.getSingleton().gzip(file, cacheFile);
                    if (DEBUG)
                        Log.d(TAG, "Cache to " + cacheFile + " and read it.");
                }
                return new GzipFileEntity(cacheFile, contentType, true);
            } else {
                if (DEBUG)
                    Log.d(TAG, "Directly return gzip stream for " + file);
                return new GzipFileEntity(file, contentType, false);
            }
        }
        return new FileEntity(file, contentType);
    }

}
