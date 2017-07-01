package org.join.ws.serv.req.objs;

/**
 * @brief 渲染的行信息
 * @note not obfuscated in this package
 * @author join
 */
public class FileRow {

    public String clazz;
    public String name;
    public String link;
    public String size;
    public String time;

    public boolean can_browse = false;
    public boolean can_download = false;
    public boolean can_delete = false;
    public boolean can_upload = false;

    public FileRow() {
    }

    public FileRow(String clazz, String name, String link, String size) {
        this.clazz = clazz;
        this.name = name;
        this.link = link;
        this.size = size;
    }

}
