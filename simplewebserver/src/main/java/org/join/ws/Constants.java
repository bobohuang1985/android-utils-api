package org.join.ws;

import android.os.Environment;

/**
 * @brief 应用设置常量
 * @author join
 */
public final class Constants {

    public static String APP_DIR_NAME = "/.boboUtils/";
    public static String APP_DIR = Environment.getExternalStorageDirectory() + APP_DIR_NAME;

    public static class Config {
        public static final boolean DEV_MODE = false;

        public static int PORT = 7766;
        public static String WEBROOT = "/";

        /** 服务资源文件 */
        public static final String SERV_ROOT_DIR = APP_DIR;//APP_DIR + "root/";

        /** 渲染模板目录 */
        public static final String SERV_TEMP_DIR = APP_DIR;//SERV_ROOT_DIR + "temp/";
        
        /** 首页文件 */
        public static final String SERV_INDEX_FILE_TARGET = "/index.html";
        public static final String SERV_INDEX_FILE = APP_DIR+"index.html";//APP_DIR + "root/";
        
        /** 附件目录 */
        public static final String SERV_ATTACHMENTS_TARGET = "/attachments/";
        /** 统一编码 */
        public static final String ENCODING = "UTF-8";

        /** 是否允许下载 */
        public static boolean ALLOW_DOWNLOAD = true;
        /** 是否允许删除 */
        public static boolean ALLOW_DELETE = true;
        /** 是否允许上传 */
        public static boolean ALLOW_UPLOAD = true;

        /** The threshold, in bytes, below which items will be retained in memory and above which they will be stored as a file. */
        public static final int THRESHOLD_UPLOAD = 1024 * 1024; // 1MB

        /** 是否使用GZip */
        public static boolean USE_GZIP = true;
        /** GZip扩展名 */
        public static final String EXT_GZIP = ".gz"; // used in cache

        /** 是否使用文件缓存 */
        public static boolean USE_FILE_CACHE = true;
        /** 文件缓存目录 */
        public static final String FILE_CACHE_DIR = APP_DIR + "cache/";

        /** 缓冲字节长度=1024*4B */
        public static final int BUFFER_LENGTH = 4096;
    }

}
