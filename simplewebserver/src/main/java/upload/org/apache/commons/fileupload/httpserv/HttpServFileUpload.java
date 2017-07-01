package org.apache.commons.fileupload.httpserv;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;

public class HttpServFileUpload extends FileUpload {

    /** Constant for HTTP POST method. */
    private static final String POST_METHOD = "POST";

    public static final boolean isMultipartContent(HttpRequest request) {
        String method = request.getRequestLine().getMethod();
        if (!POST_METHOD.equalsIgnoreCase(method)) {
            return false;
        }
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            return false;
        }
        return FileUploadBase.isMultipartContent(new HttpServRequestContext(
                (HttpEntityEnclosingRequest) request));
    }

    public HttpServFileUpload() {
        super();
    }

    public HttpServFileUpload(FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }

    public List<FileItem> parseRequest(HttpRequest request) throws FileUploadException {
        return parseRequest(new HttpServRequestContext(request));
    }

    public Map<String, List<FileItem>> parseParameterMap(HttpRequest request)
            throws FileUploadException {
        return parseParameterMap(new HttpServRequestContext(request));
    }

    public FileItemIterator getItemIterator(HttpRequest request) throws FileUploadException,
            IOException {
        return super.getItemIterator(new HttpServRequestContext(request));
    }

}
