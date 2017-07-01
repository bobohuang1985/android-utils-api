package org.apache.commons.fileupload.httpserv;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.UploadContext;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;

public class HttpServRequestContext implements UploadContext {

    private final HttpEntityEnclosingRequest request;
    private final HttpEntity entity;

    public HttpServRequestContext(HttpRequest request) throws FileUploadException {
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            throw new FileUploadException(
                    "Unacceptable HttpRequest, it must be instanceof HttpEntityEnclosingRequest");
        }
        this.request = ((HttpEntityEnclosingRequest) request);
        this.entity = this.request.getEntity();
    }

    public HttpServRequestContext(HttpEntityEnclosingRequest request) {
        this.request = request;
        this.entity = request.getEntity();
    }

    @Override
    public String getCharacterEncoding() {
        Header header = entity.getContentEncoding();
        return header == null ? null : header.getValue();
    }

    @Override
    public String getContentType() {
        Header header = entity.getContentType();
        return header == null ? null : header.getValue();
    }

    @Override
    public int getContentLength() {
        return (int) entity.getContentLength();
    }

    @Override
    public long contentLength() {
        return entity.getContentLength();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return entity.getContent();
    }

    @Override
    public String toString() {
        return format("ContentLength=%s, ContentType=%s", this.contentLength(),
                this.getContentType());
    }

}
