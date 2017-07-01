package org.join.ws.serv;

import java.io.IOException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.join.ws.Constants.Config;
import org.join.ws.serv.WebServer.OnWebServListener;

/**
 * @brief Web服务工作线程
 * @author join
 */
public class WorkerThread extends Thread {

    static final boolean DEBUG = false || Config.DEV_MODE;

    private final HttpService httpservice;
    private final HttpServerConnection conn;
    private final OnWebServListener listener;

    public WorkerThread(HttpService httpservice, HttpServerConnection conn,
            OnWebServListener listener) {
        super();
        this.httpservice = httpservice;
        this.conn = conn;
        this.listener = listener;
    }

    @Override
    public void run() {
        HttpContext context = new BasicHttpContext();
        try {
            while (WebServer.isLoop && !Thread.interrupted() && this.conn.isOpen()) {
                this.httpservice.handleRequest(this.conn, context);
            }
        } catch (ConnectionClosedException e) {
            if (DEBUG)
                System.err.println("Client closed connection");
        } catch (IOException e) {
            if (DEBUG) {
                System.err.println("I/O error: " + e.getMessage());
            }
            if (listener != null && e.getMessage() != null
                    && e.getMessage().startsWith("File not found >>> '")) {
                listener.onError(WebServer.ERR_TEMP_NOT_FOUND);
            }
        } catch (HttpException e) {
            if (DEBUG)
                System.err.println("Unrecoverable HTTP protocol violation: " + e.getMessage());
        } finally {
            try {
                this.conn.shutdown();
            } catch (IOException ignore) {
            }
        }
    }
}
