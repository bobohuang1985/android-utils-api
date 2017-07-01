package org.join.ws.serv.req;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.join.ws.serv.req.objs.FileRow;
import org.join.ws.serv.support.Progress;
import org.join.ws.serv.view.ViewFactory;
import org.join.ws.util.CommonUtil;

/**
 * @brief 目录浏览页面请求处理
 * @author join
 */
public class HttpFBHandler implements HttpRequestHandler {
	static final boolean DEBUG = false || Config.DEV_MODE;
    private CommonUtil mCommonUtil = CommonUtil.getSingleton();
    private ViewFactory mViewFactory = ViewFactory.getSingleton();

    private String webRoot;

    public HttpFBHandler(final String webRoot) {
        this.webRoot = webRoot;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        String target = URLDecoder.decode(request.getRequestLine().getUri(), Config.ENCODING);
        File file;
        if (target.equals("/")) {
            file = new File(Config.SERV_INDEX_FILE);
            target = Config.SERV_INDEX_FILE_TARGET;
        } else if (/*!target.startsWith(Config.SERV_ROOT_DIR) && */!target.startsWith(this.webRoot)) {
            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
            response.setEntity(resp403(request));
            return;
        } else {
            file = new File(Config.SERV_ROOT_DIR+target.substring(webRoot.length()));
        }

        HttpEntity entity;
        String contentType = "text/html;charset=" + Config.ENCODING;
        if (!file.exists()) { // 不存在
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            entity = resp404(request);
        } else if (file.canRead()) { // 可读
            response.setStatusCode(HttpStatus.SC_OK);
            if (file.isDirectory()) {
                //entity = respView(request, file);
                response.setStatusCode(HttpStatus.SC_FORBIDDEN);
                entity = resp403(request);
            } else if(target.startsWith(Config.SERV_ATTACHMENTS_TARGET)){
                entity = respDownloadFile(request, file);
                response.addHeader("Content-Description", "File Transfer");
                //contentType = "application/octet-stream";//entity.getContentType().getValue();
                response.setHeader("Content-Type", "application/octet-stream");
                response.addHeader("Content-Disposition", "attachment;filename=" + encodeFilename(file));
                response.setHeader("Content-Transfer-Encoding", "binary");
                response.setEntity(entity);
                //不能调用Progress.clear();否则下载无法成功
                return;
            }else{
            	//skip webRoot
                entity = respView(request, target.substring(webRoot.length()));
            }
        } else { // 不可读
            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
            entity = resp403(request);
        }

        response.setHeader("Content-Type", contentType);
        response.setEntity(entity);

        Progress.clear();
    }
    
    /*huangzb add begin*/
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
    /**
     * 下载文件
     * @param request
     * @param file
     * @return
     * @throws IOException
     */
    private HttpEntity respDownloadFile(HttpRequest request, final File file) throws IOException {
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
        return entity;
    }
    /*huangzb add end*/
    private HttpEntity respFile(HttpRequest request, File file) throws IOException {
        return mViewFactory.renderFile(request, file);
    }

    private HttpEntity resp403(HttpRequest request) throws IOException {
        return mViewFactory.renderTemp(request, "403.html");
    }

    private HttpEntity resp404(HttpRequest request) throws IOException {
        return mViewFactory.renderTemp(request, "404.html");
    }

    /*private HttpEntity respView(HttpRequest request, File dir) throws IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("dirpath", dir.getPath()); // 目录路径
        data.put("hasParent", !isSamePath(dir.getPath(), this.webRoot)); // 是否有上级目录
        data.put("fileRows", buildFileRows(dir)); // 文件行信息集合
        return mViewFactory.renderTemp(request, "view.html", data);
    }*/
    private HttpEntity respView(HttpRequest request, String target) throws IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        //data.put("dirpath", dir.getPath()); // 目录路径
        //data.put("hasParent", !isSamePath(dir.getPath(), this.webRoot)); // 是否有上级目录
        //data.put("fileRows", buildFileRows(dir)); // 文件行信息集合
        return mViewFactory.renderTemp(request, target, data);
    }
    private boolean isSamePath(String a, String b) {
        String left = a.substring(b.length(), a.length()); // a以b开头
        if (left.length() >= 2) {
            return false;
        }
        if (left.length() == 1 && !left.equals("/")) {
            return false;
        }
        return true;
    }

    private List<FileRow> buildFileRows(File dir) {
        File[] files = dir.listFiles(); // 目录列表
        if (files != null) {
            sort(files); // 排序
            ArrayList<FileRow> fileRows = new ArrayList<FileRow>();
            for (File file : files) {
                fileRows.add(buildFileRow(file));
            }
            return fileRows;
        }
        return null;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd ahh:mm");

    private FileRow buildFileRow(File f) {
        boolean isDir = f.isDirectory();
        String clazz, name, link, size;
        if (isDir) {
            clazz = "icon dir";
            name = f.getName() + "/";
            link = f.getPath() + "/";
            size = "";
        } else {
            clazz = "icon file";
            name = f.getName();
            link = f.getPath();
            size = mCommonUtil.readableFileSize(f.length());
        }
        FileRow row = new FileRow(clazz, name, link, size);
        row.time = sdf.format(new Date(f.lastModified()));
        if (f.canRead()) {
            row.can_browse = true;
            if (Config.ALLOW_DOWNLOAD) {
                row.can_download = true;
            }
            if (f.canWrite() && !hasWsDir(f)) {
                if (Config.ALLOW_DELETE) {
                    row.can_delete = true;
                }
                if (Config.ALLOW_UPLOAD && isDir) {
                    row.can_upload = true;
                }
            }
        }
        return row;
    }

    private boolean hasWsDir(File f) {
        return HttpDelHandler.hasWsDir(f);
    }

    /** 排序：文件夹、文件，再各安字符顺序 */
    private void sort(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if (f1.isDirectory() && !f2.isDirectory()) {
                    return -1;
                } else if (!f1.isDirectory() && f2.isDirectory()) {
                    return 1;
                } else {
                    return f1.toString().compareToIgnoreCase(f2.toString());
                }
            }
        });
    }

}
