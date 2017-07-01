package org.join.ws.serv.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.message.BasicHeader;
import org.join.ws.Constants.Config;

/**
 * 基础Gzip实体
 * @author Join
 */
public abstract class GzipEntity extends AbstractHttpEntity implements Cloneable {

    /**
     * @brief 输入流拷贝进输出流
     * @warning When outstream is GZIPOutputStream, it will call finish(). But won't close any stream.
     * @param instream 输入流
     * @param outstream
     * @throws IOException 输出流
     */
    protected void copy(InputStream instream, OutputStream outstream) throws IOException {
        byte[] tmp = new byte[Config.BUFFER_LENGTH];
        int l;
        while ((l = instream.read(tmp)) != -1) {
            outstream.write(tmp, 0, l);
        }
        if (outstream instanceof GZIPOutputStream) {
            ((GZIPOutputStream) outstream).finish();
        }
        outstream.flush();
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public InputStream getContent() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        writeTo(buf);
        return new ByteArrayInputStream(buf.toByteArray());
    }

    @Override
    public Header getContentEncoding() {
        return new BasicHeader("Content-Encoding", "gzip");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
