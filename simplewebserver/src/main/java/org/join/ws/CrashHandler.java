package org.join.ws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import org.join.web.serv.R;
import org.join.ws.util.CommonUtil;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/** 
 * @brief 异常崩溃处理类
 * @details 当程序发生未捕获异常时，由该类来接管程序并记录发送错误报告。 
 * @author join
 */
public class CrashHandler implements UncaughtExceptionHandler {

    /** 错误日志文件名称 */
    static final String LOG_NAME = ".crash";

    /** 系统默认的UncaughtException处理类 */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /** 上下文对象 */
    private Context mContext;

    /**
     * @brief 构造函数
     * @details 获取系统默认的UncaughtException处理器，设置该CrashHandler为程序的默认处理器 。
     * @param context 上下文
     */
    public CrashHandler(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /** 
     * @brief 当UncaughtException发生时会转入该函数来处理 
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 如果用户没有处理则让系统默认的异常处理器来处理
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 等待会后结束程序
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }

    }

    /** 
     * @brief 自定义错误处理，收集错误信息 
     * @details 发送错误报告等操作均在此完成
     * @param ex 异常
     * @return true：如果处理了该异常信息；否则返回false。
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return true;
        }
        // 提示错误消息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext,
                        mContext.getString(R.string.info_crash, Constants.APP_DIR + LOG_NAME),
                        Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        // 保存错误报告文件
        saveCrashInfoToFile(ex);
        return true;
    }

    /** 
     * @brief 保存错误信息到文件中
     * @param ex 异常
     */
    private void saveCrashInfoToFile(Throwable ex) {
        final StackTraceElement[] stack = ex.getStackTrace();
        final String message = ex.getMessage();
        /* 准备错误日志文件 */
        File logFile = new File(Constants.APP_DIR + LOG_NAME);
        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }
        /* 写入错误日志 */
        FileWriter fw = null;
        CommonUtil mCommonUtil = CommonUtil.getSingleton();
        final String lineFeed = "\r\n";
        try {
            fw = new FileWriter(logFile, true);
            fw.write(mCommonUtil.currentTime(mCommonUtil.FORMAT_YMDHMS).toString() + lineFeed
                    + lineFeed);
            fw.write(message + lineFeed);
            for (int i = 0; i < stack.length; i++) {
                fw.write(stack[i].toString() + lineFeed);
            }
            fw.write(lineFeed);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fw)
                    fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
