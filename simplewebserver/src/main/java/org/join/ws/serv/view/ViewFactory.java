package org.join.ws.serv.view;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;

/**
 * @brief 视图工厂类
 * @details 渲染各类的视图，且经此的才会考虑GZip、文件缓存等。
 * @author Join
 */
public class ViewFactory {

    static class Holder {
        static ViewFactory instance = new ViewFactory();
    }

    public static ViewFactory getSingleton() {
        return Holder.instance;
    }

    /** Type of {@link StringView } */
    public static final int TYPE_STRING = 0x0001;
    /** Type of {@link FileView } */
    public static final int TYPE_FILE = 0x0002;
    /** Type of {@link TempView } */
    public static final int TYPE_TEMPLATE = 0x0003;

    private ViewFactory() {
    }

    /**
     * 用某视图类来渲染内容和参数
     * @param request Http请求
     * @param view 某视图类
     * @param content 渲染内容
     * @param args 渲染参数
     * @return 渲染后的Http实体
     * @throws IOException
     */
    public <T, A> HttpEntity render(HttpRequest request, BaseView<T, A> view, T content, A args)
            throws IOException {
        return view.render(request, content, args);
    }

    /**
     * @brief 渲染各类视图，获取其Http实体
     * @param request Http请求
     * @param type {@link #TYPE_STRING}, {@link #TYPE_FILE}, {@link #TYPE_TEMPTALE}
     * @param content 渲染内容，可以为字符串、文件、模板文件
     * @param args 渲染参数，对应内容为格式字符串用的对象数组、文件响应类型的字符串、模板文件上下文对象的Map集合
     * @return 渲染后的Http实体
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public <T, A> HttpEntity render(HttpRequest request, int type, T content, A args)
            throws IOException {
        BaseView<T, A> view;
        switch (type) {
        case TYPE_STRING:
            view = (BaseView<T, A>) new StringView();
            break;
        case TYPE_FILE:
            view = (BaseView<T, A>) new FileView();
            break;
        case TYPE_TEMPLATE:
            view = (BaseView<T, A>) new TempView();
            break;
        default:
            throw new IOException("Unsupport view type.");
        }
        return render(request, view, content, args);
    }

    /**
     * @see #render(HttpRequest, int, Object, Object)，第四参数为null。
     */
    public <T> HttpEntity render(HttpRequest request, int type, T content) throws IOException {
        return this.render(request, type, content, null);
    }

    /**
     * @brief 渲染字符串成Http实体
     * @see StringView#render(HttpRequest, String, Object[])
     */
    public HttpEntity renderString(HttpRequest request, String content, Object[] args)
            throws IOException {
        return render(request, new StringView(), content, args);
    }

    /**
     * @see #renderString(HttpRequest, String, Object[])，第三参数为null。
     */
    public HttpEntity renderString(HttpRequest request, String content) throws IOException {
        return this.renderString(request, content, null);
    }

    /**
     * @brief 渲染文件成Http实体
     * @see FileView#render(HttpRequest, File, String)
     */
    public HttpEntity renderFile(HttpRequest request, File file, String contentType)
            throws IOException {
        return render(request, new FileView(), file, contentType);
    }

    /**
     * @see #renderFile(HttpRequest, File, String)，第三参数为null。
     */
    public HttpEntity renderFile(HttpRequest request, File file) throws IOException {
        return this.renderFile(request, file, null);
    }

    /**
     * @brief 渲染模板文件成Http实体
     * @see TempView#render(HttpRequest, String, Map)
     */
    public HttpEntity renderTemp(HttpRequest request, String tempFile, Map<String, Object> data)
            throws IOException {
        return render(request, new TempView(), tempFile, data);
    }

    /**
     * @see #renderString(HttpRequest, String, Object[])，第三参数为null。
     */
    public HttpEntity renderTemp(HttpRequest request, String tempFile) throws IOException {
        return this.renderTemp(request, tempFile, null);
    }

}
