package org.join.ws.serv.support;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.fileupload.ParameterParser;
import org.apache.http.HttpRequest;

/**
 * @brief Get参数既简单解析
 * @author Join
 */
public class HttpGetParser {

    private static final String GET_METHOD = "GET";

    public static boolean isGetMethod(HttpRequest request) {
        String method = request.getRequestLine().getMethod();
        return GET_METHOD.equalsIgnoreCase(method);
    }

    /**
     * @brief 解析请求的get信息
     * @param request Http请求
     * @return 名称与值的Map集合
     * @throws IOException
     * @warning 需保证是post请求且不是multipart的。
     */
    public Map<String, String> parse(HttpRequest request) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        return parser.parse(getContent(request), '&');
    }

    public String getContent(HttpRequest request) {
        String uri = request.getRequestLine().getUri();
        int index = uri.indexOf('?');
        return index == -1 || index + 1 >= uri.length() ? null : uri.substring(index + 1);
    }

}
