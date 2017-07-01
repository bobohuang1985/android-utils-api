package org.join.ws.serv.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Gzip字节数组实体
 * @author Join
 */
public class GzipByteArrayEntity extends GzipEntity {

    protected final byte[] byteArray;
    protected final boolean isGzipByteArray;

    /**
     * Gzip字节数组实体构造函数
     * @param byteArray 字节数组
     * @param isGzipByteArray 是否是Gzip压缩过的字节数组
     */
    public GzipByteArrayEntity(byte[] byteArray, boolean isGzipByteArray) {
        super();
        if (byteArray == null) {
            throw new IllegalArgumentException("byteArray may not be null");
        }
        this.byteArray = byteArray;
        this.isGzipByteArray = isGzipByteArray;
    }

    @Override
    public long getContentLength() {
        return isGzipByteArray ? byteArray.length : -1;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream instream = new ByteArrayInputStream(this.byteArray);
        if (!isGzipByteArray) {
            outstream = new GZIPOutputStream(outstream);
        }
        try {
            copy(instream, outstream);
        } finally {
            instream.close();
        }
    }

}
