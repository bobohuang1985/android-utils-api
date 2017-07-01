package org.join.ws.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.join.ws.Constants.Config;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * @brief 复制工具
 * @author join
 */
public class CopyUtil {

    private Context mContext;

    public CopyUtil(Context context) {
        mContext = context;
    }

    /**
     * @brief assets路径内内容复制进目录路径
     * @param assetsPath assets路径
     * @param dirPath 目录路径
     * @param isSmart true: only when a file doesn't exist; false: override.
     * @throws IOException
     */
    public void assetsCopy(String assetsPath, String dirPath, boolean isSmart) throws IOException {
        AssetManager am = mContext.getAssets();
        String[] list = am.list(assetsPath);
        if (list.length == 0) { // 文件
            File file = new File(dirPath);
            if (!isSmart || !file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                InputStream in = am.open(assetsPath);
                FileOutputStream fout = new FileOutputStream(file);
                write(in, fout); // 复制
            }
        } else { // 目录
            for (String path : list) {
                assetsCopy(join(assetsPath, path), join(dirPath, path), isSmart);
            }
        }
    }

    // Joins two path components, adding a separator only if necessary.
    private static String join(String prefix, String suffix) {
        int prefixLength = prefix.length();
        boolean haveSlash = (prefixLength > 0 && prefix.charAt(prefixLength - 1) == File.separatorChar);
        if (!haveSlash) {
            haveSlash = (suffix.length() > 0 && suffix.charAt(0) == File.separatorChar);
        }
        return haveSlash ? (prefix + suffix) : (prefix + File.separatorChar + suffix);
    }

    /**
     * 删除文件或整个目录
     * @param file 文件或目录
     */
    public void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File f : files) {
                    deleteFile(f);
                }
            }
            file.delete();
        } else {
            file.delete();
        }
    }

    private void write(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[Config.BUFFER_LENGTH];
        int count;
        try {
            while ((count = in.read(buf)) != -1) {
                out.write(buf, 0, count);
            }
            out.flush();
        } finally {
            in.close();
            out.close();
        }
    }

}
