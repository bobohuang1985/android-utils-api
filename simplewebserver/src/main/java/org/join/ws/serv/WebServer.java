package org.join.ws.serv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.join.ws.Constants.Config;
import org.join.ws.serv.req.HttpDelHandler;
import org.join.ws.serv.req.HttpDownHandler;
import org.join.ws.serv.req.HttpFBHandler;
import org.join.ws.serv.req.HttpProgressHandler;
import org.join.ws.serv.req.HttpUpHandler;
import org.join.ws.util.CommonUtil;

/**
 * @brief Web服务类
 * @author join
 */
public class WebServer extends Thread {

    static final String TAG = "WebServer";
    static final boolean DEBUG = false || Config.DEV_MODE;

    public static final int ERR_UNEXPECT = 0x0101;
    public static final int ERR_PORT_IN_USE = 0x0102;
    public static final int ERR_TEMP_NOT_FOUND = 0x0103;

    private int port;
    private String webRoot;

    private ServerSocket serverSocket;
    /* package */static boolean isLoop;

    private OnWebServListener mListener;

    private ExecutorService pool; // 线程池

    public WebServer(int port, final String webRoot) {
        super();
        this.port = port;
        this.webRoot = webRoot;
        isLoop = false;

        pool = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            // Decide if port is in use.
            if (CommonUtil.getSingleton().isLocalPortInUse(port)) {
                if (mListener != null) {
                    mListener.onError(ERR_PORT_IN_USE);
                }
                return;
            }
            // 创建服务器套接字
            serverSocket = new ServerSocket(port);
            // 设置端口重用
            serverSocket.setReuseAddress(true);
            // 创建HTTP协议处理器
            BasicHttpProcessor httpproc = new BasicHttpProcessor();
            // 增加HTTP协议拦截器
            httpproc.addInterceptor(new ResponseDate());
            httpproc.addInterceptor(new ResponseServer());
            httpproc.addInterceptor(new ResponseContent());
            httpproc.addInterceptor(new ResponseConnControl());
            // 创建HTTP服务
            HttpService httpService = new HttpService(httpproc,
                    new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
            // 创建HTTP参数
            HttpParams params = new BasicHttpParams();
            params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                    .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                    .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                    .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                    .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "WebServer/1.1");
            // 设置HTTP参数
            httpService.setParams(params);
            // 创建HTTP请求执行器注册表
            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
            // 增加HTTP请求执行器
            reqistry.register(UrlPattern.DOWNLOAD, new HttpDownHandler(webRoot));
            reqistry.register(UrlPattern.DELETE, new HttpDelHandler(webRoot));
            reqistry.register(UrlPattern.UPLOAD, new HttpUpHandler(webRoot));
            reqistry.register(UrlPattern.PROGRESS, new HttpProgressHandler());
            reqistry.register(UrlPattern.BROWSE, new HttpFBHandler(webRoot));
            // 设置HTTP请求执行器
            httpService.setHandlerResolver(reqistry);
            // 回调通知服务开始
            if (mListener != null) {
                mListener.onStarted();
            }
            /* 循环接收各客户端 */
            isLoop = true;
            while (isLoop && !Thread.interrupted()) {
                // 接收客户端套接字
                Socket socket = serverSocket.accept();
                // 绑定至服务器端HTTP连接
                DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                conn.bind(socket, params);
                // 派送至WorkerThread处理请求
                Thread t = new WorkerThread(httpService, conn, mListener);
                t.setDaemon(true); // 设为守护线程
                pool.execute(t); // 执行
            }
        } catch (IOException e) {
            if (isLoop) { // 以排除close造成的异常
                // 回调通知服务出错
                if (mListener != null) {
                    mListener.onError(ERR_UNEXPECT);
                }
                if (DEBUG)
                    e.printStackTrace();
                isLoop = false;
            }
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                // 回调通知服务结束
                if (mListener != null) {
                    mListener.onStopped();
                }
            } catch (IOException e) {
            }
        }
    }

    public void close() {
        isLoop = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
        }
    }

    public interface OnWebServListener {
        void onStarted();

        void onStopped();

        void onError(int code);
    }

    public void setOnWebServListener(OnWebServListener mListener) {
        this.mListener = mListener;
    }

}
