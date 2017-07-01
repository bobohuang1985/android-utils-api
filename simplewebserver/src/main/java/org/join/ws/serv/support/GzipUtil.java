package org.join.ws.serv.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.join.ws.Constants.Config;

/**
 * Gzip工具类
 * @author join
 */
public class GzipUtil {

    static class Holder {
        static GzipUtil instance = new GzipUtil();
    }

    public static GzipUtil getSingleton() {
        return Holder.instance;
    }

    private GzipUtil() {
    }

    /**
     * 判断请求是否支持Gzip
     * @param request Http请求
     * @return 支持与否
     */
    public boolean isGZipSupported(HttpRequest request) {
        Header header = request.getFirstHeader("Accept-Encoding");
        return ((header != null) && header.getValue().toLowerCase().indexOf("gzip") != -1);
    }

    /**
     * 输出Gzip文件
     * @param from 输入文件
     * @param to 输出文件
     * @throws IOException
     */
    public void gzip(File from, File to) throws IOException {
        to.getParentFile().mkdirs();
        to.createNewFile();
        FileInputStream fis = new FileInputStream(from);
        FileOutputStream fos = new FileOutputStream(to);
        try {
            gzip(fis, fos);
        } finally {
            fis.close();
            fos.close();
        }
    }

    /**
     * @brief 输出Gzip流
     * @warning It won't close the streams.
     * @param from 输入流
     * @param to 输出流
     * @throws IOException
     */
    public void gzip(InputStream from, OutputStream to) throws IOException {
        GZIPOutputStream gos = new GZIPOutputStream(to);
        int count;
        byte[] buffer = new byte[Config.BUFFER_LENGTH];
        while ((count = from.read(buffer)) != -1) {
            gos.write(buffer, 0, count);
        }
        gos.finish();
        gos.flush();
    }

}
