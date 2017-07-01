package org.join.ws.serv.req;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.join.ws.Constants;
import org.join.ws.Constants.Config;
import org.join.ws.serv.support.HttpPostParser;

/**
 * @brief 删除请求处理
 * @author join
 */
public class HttpDelHandler implements HttpRequestHandler {

    private String webRoot;

    public HttpDelHandler(final String webRoot) {
        this.webRoot = webRoot;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        if (!Config.ALLOW_DELETE) {
            response.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
            return;
        }
        if (!HttpPostParser.isPostMethod(request)) {
            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
            return;
        }
        HttpPostParser parser = new HttpPostParser();
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
            return;
        } else {
            file = new File(refPath, fname);
        }

        deleteFile(file);
        response.setStatusCode(HttpStatus.SC_OK);
        StringEntity entity = new StringEntity(file.exists() ? "1" : "0", Config.ENCODING); // 1: 失败；0：成功。
        response.setEntity(entity);
    }

    /** 递归删除File */
    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File f : files) {
                    deleteFile(f);
                }
            }
            if (!hasWsDir(file)) {
                file.delete();
            }
        } else {
            if (!hasWsDir(file)) {
                file.delete();
            }
        }
    }

    public static boolean hasWsDir(File f) {
        String path = f.isDirectory() ? f.getAbsolutePath() + "/" : f.getAbsolutePath();
        return path.indexOf(Constants.APP_DIR_NAME) != -1;
    }

}
