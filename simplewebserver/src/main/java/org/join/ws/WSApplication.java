package org.join.ws;

import java.io.IOException;

import net.asfun.jangod.lib.TagLibrary;
import net.asfun.jangod.lib.tag.ResColorTag;
import net.asfun.jangod.lib.tag.ResStrTag;
import net.asfun.jangod.lib.tag.UUIDTag;

import org.join.ws.Constants.Config;
import org.join.ws.serv.TempCacheFilter;
import org.join.ws.service.WSService;
import org.join.ws.ui.PreferActivity;
import org.join.ws.util.CopyUtil;

import android.app.Application;
import android.content.Intent;

/**
 * @brief 应用全局
 * @author join
 */
public class WSApplication{
    private static WSApplication self;
    private static Application mApp;

    private Intent wsServIntent;

    public void onCreate(Application app) {
        mApp = app;
        wsServIntent = new Intent(app,WSService.class);

        initAppDir();
        initJangod();
        initAppFilter();

        /*if (!Config.DEV_MODE) {
            new CrashHandler(this);
        }*/

        PreferActivity.restoreAll();
    }

    public static synchronized WSApplication getInstance() {
        if(self == null){
            self = new WSApplication();
        }
        return self;
    }
    public static Application getApp() {
        return mApp;
    }
    /**
     * @brief 开启全局服务
     */
    public void startWsService() {
        mApp.startService(wsServIntent);
    }

    /**
     * @brief 停止全局服务
     */
    public void stopWsService() {
        mApp.stopService(wsServIntent);
    }

    /**
     * @brief 初始化应用目录
     */
    public void initAppDir() {
        CopyUtil mCopyUtil = new CopyUtil(mApp.getApplicationContext());
        // mCopyUtil.deleteFile(new File(Config.SERV_ROOT_DIR)); // 清理服务文件目录
        try {
            // 重新复制到SDCard，仅当文件不存在时
            mCopyUtil.assetsCopy("WebServer", Config.SERV_ROOT_DIR, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief 初始化Jangod，添加自定义内容
     */
    private void initJangod() {
        /* custom tags */
        TagLibrary.addTag(new ResStrTag());
        TagLibrary.addTag(new ResColorTag());
        TagLibrary.addTag(new UUIDTag());
        /* custom filters */
    }

    /**
     * @brief 初始化应用过滤器
     */
    private void initAppFilter() {
        /* TempCacheFilter */
        TempCacheFilter.addCacheTemps("403.html", "404.html", "503.html");
        /* GzipFilter */
    }

}
