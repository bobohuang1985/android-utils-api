package org.join.ws.serv.support;

import java.io.File;
import java.util.HashMap;

import org.join.ws.util.CommonUtil;

/**
 * @brief MIME类型
 * @details 更多： http://www.w3school.com.cn/media/media_mimeref.asp
 * @author Join
 */
public class MIME {

    public static HashMap<String, String> map;

    static {
        map = new HashMap<String, String>();
        /* 办公 */
        map.put("wps", "application/vnd.ms-works");
        map.put("dot", "application/msword");
        map.put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        map.put("doc", "application/msword");
        map.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        map.put("pdf", "application/pdf");
        map.put("pps", "application/vnd.ms-powerpoint");
        map.put("ppt", "application/vnd.ms-powerpoint");
        map.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        map.put("xls", "application/vnd.ms-excel");
        map.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        /* 图像 */
        map.put("bmp", "image/bmp");
        map.put("gif", "image/gif");
        map.put("jpeg", "image/jpeg");
        map.put("jpg", "image/jpeg");
        map.put("png", "image/png");
        /* 音视频/flash */
        map.put("swf", "application/x-shockwave-flash");
        map.put("3gp", "video/3gpp");
        map.put("asf", "video/x-ms-asf");
        map.put("avi", "video/x-msvideo");
        map.put("m3u", "audio/x-mpegurl");
        map.put("m4a", "audio/mp4a-latm");
        map.put("m4b", "audio/mp4a-latm");
        map.put("m4p", "audio/mp4a-latm");
        map.put("m4u", "video/vnd.mpegurl");
        map.put("m4v", "video/x-m4v");
        map.put("mov", "video/quicktime");
        map.put("mp2", "audio/x-mpeg");
        map.put("mp3", "audio/x-mpeg");
        map.put("mp4", "video/mp4");
        map.put("mpc", "application/vnd.mpohun.certificate");
        map.put("mpe", "video/mpeg");
        map.put("mpeg", "video/mpeg");
        map.put("mpg", "video/mpeg");
        map.put("mpg4", "video/mp4");
        map.put("mpga", "audio/mpeg");
        map.put("wav", "audio/x-wav");
        map.put("wma", "audio/x-ms-wma");
        map.put("wmv", "audio/x-ms-wmv");
        map.put("rmvb", "audio/x-pn-realaudio");
        map.put("ogg", "audio/ogg");
        /* 执行文件 */
        map.put("apk", "application/vnd.android.package-archive");
        map.put("bin", "application/octet-stream");
        map.put("exe", "application/octet-stream");
        /* 程序文件 */
        map.put("c", "text/plain");
        map.put("class", "application/octet-stream");
        map.put("conf", "text/plain");
        map.put("cpp", "text/plain");
        map.put("txt", "text/plain");
        // map.put("xml", "text/xml");
        map.put("xml", "text/plain");
        map.put("h", "text/plain");
        map.put("htm", "text/html");
        map.put("html", "text/html");
        map.put("css", "text/css");
        map.put("js", "application/x-javascript");
        map.put("jar", "application/java-archive");
        map.put("java", "text/plain");
        map.put("log", "text/plain");
        map.put("msg", "application/vnd.ms-outlook");
        map.put("prop", "text/plain");
        map.put("rc", "text/plain");
        map.put("rtf", "application/rtf");
        map.put("sh", "text/plain");
        /* 压缩文件 */
        map.put("gtar", "application/x-gtar");
        map.put("gz", "application/x-gzip");
        map.put("rar", "application/x-rar-compressed");
        map.put("tar", "application/x-tar");
        map.put("tgz", "application/x-compressed");
        map.put("z", "application/x-compress");
        map.put("zip", "application/zip");
        // 注释为大概分类
        map.put("", "*/*");
    }

    public static String get(String ext) {
        return map.get(ext.toLowerCase());
    }

    /**
     * 获得某文件的MIME类型
     * @param file 文件
     * @return MIME类型
     */
    public static String getFromFile(File file) {
        return get(CommonUtil.getSingleton().getExtension(file));
    }

}
