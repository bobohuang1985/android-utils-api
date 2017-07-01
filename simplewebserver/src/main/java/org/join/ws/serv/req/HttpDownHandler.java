package org.join.ws.serv.req;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.join.ws.Constants.Config;
import org.join.ws.serv.support.HttpGetParser;
import org.join.ws.serv.view.ViewFactory;

/**
 * @brief 下载请求处理
 * @author join
 */
public class HttpDownHandler implements HttpRequestHandler {

    static final String TAG = "HttpDownHandler";
    static final boolean DEBUG = false || Config.DEV_MODE;

    private String webRoot;

    public HttpDownHandler(final String webRoot) {
        this.webRoot = webRoot;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        if (!Config.ALLOW_DOWNLOAD) {
            response.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
            response.setEntity(ViewFactory.getSingleton().renderTemp(request, "503.html"));
            return;
        }
        HttpGetParser parser = new HttpGetParser();
        Map<String, String> params = parser.parse(request);
        String fname = params.get("fname");
        Header referer = request.getFirstHeader("Referer");
        if (fname == null || referer == null) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            return;
        }
        fname = URLDecoder.decode(fname, Config.ENCODING);
        String refPath = new URL(URLDecoder.decode(referer.getValue(), Config.ENCODING)).getPath();

        final File file;
        if (refPath.equals("/")) {
            file = new File(this.webRoot, fname);
        } else if (!refPath.startsWith(this.webRoot)) {
            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
            response.setEntity(ViewFactory.getSingleton().renderTemp(request, "403.html"));
            return;
        } else {
            file = new File(refPath, fname);
        }

        final String encoding = isGBKAccepted(request) ? "GBK" : Config.ENCODING;

        HttpEntity entity = new EntityTemplate(new ContentProducer() {
            @Override
            public void writeTo(OutputStream outstream) throws IOException {
                if (file.isFile()) {
                    write(file, outstream);
                } else {
                    zip(file, outstream, encoding);
                }
            }
        });
        response.setStatusCode(HttpStatus.SC_OK);
        response.addHeader("Content-Description", "File Transfer");
        response.setHeader("Content-Type", "application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename=" + encodeFilename(file));
        response.setHeader("Content-Transfer-Encoding", "binary");
        // 在某平板自带浏览器上下载失败，比较下能成功下载的响应头，这里少了Content-Length。但设了，反而下不了了。
        response.setEntity(entity);
    }

    private String encodeFilename(File file) throws IOException {
        String filename = URLEncoder.encode(getFilename(file), Config.ENCODING);
        return filename.replace("+", "%20");
    }

    private String getFilename(File file) {
        return file.isFile() ? file.getName() : file.getName() + ".zip";
    }

    /**
     * @brief 写入文件
     * @param inputFile 输入文件
     * @param outstream 输出流
     * @throws IOException
     */
    private void write(File inputFile, OutputStream outstream) throws IOException {
        FileInputStream fis = new FileInputStream(inputFile);
        try {
            int count;
            byte[] buffer = new byte[Config.BUFFER_LENGTH];
            while ((count = fis.read(buffer)) != -1) {
                outstream.write(buffer, 0, count);
            }
            outstream.flush();
        } catch (IOException e) {
            if (DEBUG)
                e.printStackTrace();
            throw e;
        } finally {
            fis.close();
            outstream.close();
        }
    }

    /**
     * @brief 压缩目录至输出流
     * @param inputFile 压缩目录（或文件）
     * @param outstream 输出流
     * @param encoding 编码
     * @throws IOException
     */
    private void zip(File inputFile, OutputStream outstream, String encoding) throws IOException {
        ZipOutputStream zos = null;
        try {
            // 创建ZIP文件输出流
            zos = new ZipOutputStream(outstream);
            zos.setEncoding(encoding);
            // 递归压缩文件进zip文件流
            zip(zos, inputFile, inputFile.getName());
        } catch (IOException e) {
            if (DEBUG)
                e.printStackTrace();
            throw e; // 抛出IOException
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
    }

    /** 递归压缩文件进zip文件流 */
    private void zip(ZipOutputStream zos, File file, String base) throws IOException {
        if (file.isDirectory()) { // 目录时
            File[] files = file.listFiles();
            if (null != files && files.length > 0) {
                for (File f : files) {
                    zip(zos, f, base + "/" + f.getName()); // 递归
                }
            } else {
                zos.putNextEntry(new ZipEntry(base + "/")); // 加入目录条目
                zos.closeEntry();
            }
        } else {
            zos.putNextEntry(new ZipEntry(base)); // 加入文件条目
            FileInputStream fis = new FileInputStream(file); // 创建文件输入流
            try {
                int count; // 读取计数
                byte[] buffer = new byte[Config.BUFFER_LENGTH]; // 缓冲字节数组
                /* 写入zip输出流 */
                while ((count = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, count);
                }
            } finally {
                zos.flush();
                zos.closeEntry();
                fis.close();
            }
        }
    }

    /**
     * 判断请求是否接受GBK
     * @param request Http请求
     * @return 接受与否
     */
    private boolean isGBKAccepted(HttpRequest request) {
        Header header = request.getFirstHeader("Accept-Charset");
        return ((header != null) && header.getValue().toLowerCase().indexOf("gbk") != -1);
    }

}
