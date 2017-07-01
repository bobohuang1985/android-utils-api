package org.join.ws.serv.req;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.httpserv.HttpServFileUpload;
import org.apache.commons.fileupload.httpserv.HttpServRequestContext;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.join.ws.Constants.Config;
import org.join.ws.serv.support.HttpGetParser;
import org.join.ws.serv.support.Progress;

/**
 * @brief 上传请求处理
 * @waring
 *   1) `Unsafe JavaScript attempt to access frame...` maybe occur in chrome, which caused by iframe way of `ajaxfileupload.js`. 
 *   more: `http://stackoverflow.com/questions/5660116/unsafe-javascript-attempt-to-access-frame-in-google-chrome`
 * @author join
 */
public class HttpUpHandler implements HttpRequestHandler {

    static final String TAG = "HttpUpHandler";
    static final boolean DEBUG = false || Config.DEV_MODE;

    private String webRoot;

    public HttpUpHandler(final String webRoot) {
        this.webRoot = webRoot;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        if (!Config.ALLOW_UPLOAD) {
            response.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
            return;
        }
        if (!HttpServFileUpload.isMultipartContent(request)) {
            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
            return;
        }

        HttpGetParser parser = new HttpGetParser();
        Map<String, String> params = parser.parse(request);
        String dir = params.get("dir");
        String id = params.get("id");
        Header referer = request.getFirstHeader("Referer");
        if (dir == null || id == null || referer == null) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            return;
        }
        dir = URLDecoder.decode(dir, Config.ENCODING);
        String refPath = new URL(URLDecoder.decode(referer.getValue(), Config.ENCODING)).getPath();

        final File uploadDir; // upload directory
        if (refPath.equals("/")) {
            uploadDir = new File(this.webRoot, dir);
        } else if (!refPath.startsWith(this.webRoot)) {
            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
            return;
        } else {
            uploadDir = new File(refPath, dir);
        }

        // TODO Decide if file exists and if there are enough free spaces.
        if (uploadDir.isDirectory()) {
            response.setStatusCode(HttpStatus.SC_OK);
            try {
                processFileUpload(request, uploadDir, id);
                response.setEntity(new StringEntity("ok", Config.ENCODING));
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            }
        } else {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }

    /** Process file upload */
    private void processFileUpload(HttpRequest request, File uploadDir, String id) throws Exception {
        FileItemFactory factory = new DiskFileItemFactory(Config.THRESHOLD_UPLOAD, uploadDir);
        HttpServFileUpload fileUpload = new HttpServFileUpload(factory);
        fileUpload.setProgressListener(new MyProgressListener(id));

        List<FileItem> fileItems = fileUpload.parseRequest(new HttpServRequestContext(request));
        Iterator<FileItem> iter = fileItems.iterator();
        while (iter.hasNext()) {
            FileItem item = iter.next();

            if (item.isFormField()) {
                processFormField(item);
            } else {
                processUploadedFile(item, uploadDir);
            }
        }
    }

    /** Process a regular form field */
    private void processFormField(FileItem item) {
    }

    /** Process a file upload */
    private void processUploadedFile(FileItem item, File uploadDir) throws Exception {
        String fileName = item.getName();
        File uploadedFile = new File(uploadDir, fileName);
        item.write(uploadedFile);
    }

    /** Create a progress listener */
    class MyProgressListener implements ProgressListener {

        final String mId;

        public MyProgressListener(String id) {
            mId = id;
        }
        
        @Override
        public void update(long pBytesRead, long pContentLength, int pItems) {
            if (pContentLength != -1) {
                int progress = (int) (pBytesRead * 100 / pContentLength);
                Progress.update(mId, progress);
            }
        }

    }

}
