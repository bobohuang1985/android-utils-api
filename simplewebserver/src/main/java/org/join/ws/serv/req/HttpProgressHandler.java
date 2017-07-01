package org.join.ws.serv.req;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.join.ws.Constants.Config;
import org.join.ws.serv.support.HttpPostParser;
import org.join.ws.serv.support.Progress;

import android.util.Log;

public class HttpProgressHandler implements HttpRequestHandler {

    static final String TAG = "HttpProgressHandler";
    static final boolean DEBUG = false || Config.DEV_MODE;

    public HttpProgressHandler() {
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        HttpPostParser parser = new HttpPostParser();
        Map<String, String> params = parser.parse(request);
        String id = params.get("id");
        if (id == null) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            return;
        }
        String progress = Progress.get(id) + "";
        if (DEBUG)
            Log.d(TAG, id + ": " + progress);
        response.setEntity(new StringEntity(progress, Config.ENCODING));
    }

}
