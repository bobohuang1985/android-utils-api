package org.join.ws.serv.support;

import java.io.File;
import java.lang.reflect.Method;

import android.webkit.MimeTypeMap;

/**
 * @deprecated see {@link MIME}
 * @brief 获取文件的MIME类型
 * @author join
 */
public class MimeType {

    private static MimeTypeMap sMimeTypeMap;

    static {
        sMimeTypeMap = MimeTypeMap.getSingleton();
        loadEntry(sMimeTypeMap, "application/x-javascript", "js");
    }

    /** 反射调用其loadEntry方法 */
    private static void loadEntry(MimeTypeMap mimeTypeMap, String mimeType, String extension) {
        Class<? extends MimeTypeMap> cls = mimeTypeMap.getClass();
        try {
            Method method = cls.getDeclaredMethod("loadEntry", String.class, String.class);
            method.setAccessible(true);
            method.invoke(mimeTypeMap, new Object[] { mimeType, extension });
        } catch (Exception e) {
        }
    }

    /**
     * 获得某文件的MIME类型
     * @param file 文件
     * @return MIME类型，可能为null。
     */
    public static String getFromFile(File file) {
        String ext = getExtension(file);
        return ext.endsWith("") ? null : sMimeTypeMap.getMimeTypeFromExtension(ext);
    }

    /**
     * 获取文件后缀名，不带`.`
     * @param file 文件
     * @return 文件后缀
     */
    private static String getExtension(File file) {
        String name = file.getName();
        int i = name.lastIndexOf('.');
        int p = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        return i > p ? name.substring(i + 1) : "";
    }

}
