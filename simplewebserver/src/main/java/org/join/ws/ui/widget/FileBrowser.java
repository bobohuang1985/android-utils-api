package org.join.ws.ui.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @brief 文件浏览组件
 * @author join
 */
public class FileBrowser extends ListView implements android.widget.AdapterView.OnItemClickListener {

    static final String TAG = "FileBrowser";

    private final String namespace = "http://github.com/joinAero/AndroidWebServ/";

    /** 根目录 */
    private String rootDir;

    /** 上级目录名称 */
    private String parentDirName = ". .";

    /**
     * @brief 保存当前目录中所有的File对象（每一个File对象表示目录或文件）
     */
    private List<File> fileList = new ArrayList<File>();

    /**
     * @brief 当前显示的目录
     */
    private File currentDir;

    /**
     * @brief folder_def属性值
     * @details 表示目录的默认图片资源id。
     */
    private int folderResId;

    /**
     * @brief folder_parent属性值
     * @details 表示上级目录的图片资源id。
     */
    private int folderParentResId;

    /**
     * @brief file_def属性值
     * @details 表示文件的默认图片资源id。
     */
    private int fileResId;

    /**
     * @brief 文件对应扩展名的图片资源id集合
     * @details key表示文件扩展名，value表示该扩展名对应的图像资源ID。
     */
    private Map<String, Integer> extResIdMap = new HashMap<String, Integer>();

    /**
     * @brief display属性值。 
     * @details true：只显示当前目录中的文件列表。
     */
    private int display = DISPLAY_ALL;

    public static final int DISPLAY_ALL = -1;
    public static final int DISPLAY_FILE = 0;
    public static final int DISPLAY_FOLDER = 1;

    /**
     * @brief back_resp属性值
     * @details true：响应返回键回上级目录。
     */
    private boolean isBackResp;

    /**
     * @brief sort属性值
     * @details true：以上一级、文件夹、文件顺序排序。
     */
    private boolean isSort;

    private FileListAdapter mListAdapter;
    private OnBrowserListener mBrowserListener;

    public FileBrowser(Context context) {
        super(context);
        initFileBrowser(context);
    }

    public FileBrowser(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileBrowser(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initFileBrowser(context);

        folderResId = attrs.getAttributeResourceValue(namespace, "folder_def", 0);
        folderParentResId = attrs
                .getAttributeResourceValue(namespace, "folder_parent", folderResId);
        fileResId = attrs.getAttributeResourceValue(namespace, "file_def", 0);
        String value = attrs.getAttributeValue(namespace, "display");
        if (value != null) {
            if (value.equals("file")) {
                display = DISPLAY_FILE;
            } else if (value.equals("folder")) {
                display = DISPLAY_FOLDER;
            }
        }
        isBackResp = attrs.getAttributeBooleanValue(namespace, "back_resp", false);
        isSort = attrs.getAttributeBooleanValue(namespace, "sort", true);

        // 动态扩展名属性
        int index = 1;
        while (true) {
            String extName = attrs.getAttributeValue(namespace, "ext_name" + index);
            int fileImageResId = attrs.getAttributeResourceValue(namespace, "ext_image" + index, 0);
            // 如果读取不到extName或fileImage属性时跳出循环
            if ("".equals(extName) || extName == null || fileImageResId == 0) {
                break;
            }
            extResIdMap.put(extName, fileImageResId);
            index++;
        }
    }

    private void initFileBrowser(Context context) {
        setOnItemClickListener(this);

        mListAdapter = new FileListAdapter(context);
        setAdapter(mListAdapter);

        rootDir = "/"; // 根目录设为'/'
        browse(rootDir); // 初始浏览根目录
    }

    /**
     * @brief 浏览某一目录
     * @param dir 目录
     */
    public void browse(String dir) {
        File file = new File(dir);
        if (!file.exists() || !file.isDirectory() || !file.canRead()) {
            Log.e(TAG, dir + " access denied!");
            return;
        }
        currentDir = file;
        refresh();
    }

    /**
     * @brief 刷新显示内容
     */
    public void refresh() {
        updateFiles();
        mListAdapter.notifyDataSetChanged();
    }

    /**
     * @brief 获得文件后缀名
     */
    private String getExtName(String filename) {
        int position = filename.lastIndexOf(".");
        if (position >= 0)
            return filename.substring(position + 1).toLowerCase();
        else
            return "";
    }

    /**
     * @brief 扫描当前目录，并将当前目录的File对象集合添加到fileList变量中
     * @return true：扫描目录有成功；false：扫描目录有错误。
     */
    private void updateFiles() {
        fileList.clear();

        // 非根目录时，增加null以显示一个“..”
        if (!isRootDir())
            fileList.add(null);

        File[] files = currentDir.listFiles();

        for (File file : files) {
            if (display == DISPLAY_ALL) {
                fileList.add(file);
            } else if (display == DISPLAY_FILE && file.isFile()) {
                fileList.add(file);
            } else if (display == DISPLAY_FOLDER && file.isDirectory()) {
                fileList.add(file);
            }
        }

        // 排序
        if (isSort) {
            sortFileList(fileList);
        }
    }

    private boolean isRootDir() {
        return currentDir.getPath().equals(rootDir);
    }

    private void sortFileList(List<File> list) {
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if (null == f1) {
                    return -1;
                } else if (null == f2) {
                    return 1;
                } else if (f1.isDirectory() && !f2.isDirectory()) {
                    return -1;
                } else if (!f1.isDirectory() && f2.isDirectory()) {
                    return 1;
                } else {
                    return f1.toString().compareToIgnoreCase(f2.toString());
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = fileList.get(position);
        if (file == null) { // 上级时
            toParentDir();
        } else if (file.isDirectory()) { // 目录时
            if (file.exists() && file.canRead()) {
                currentDir = file;
                refresh(); // 刷新
                if (mBrowserListener != null) {
                    mBrowserListener.onDirItemClick(currentDir.getPath());
                }
            } else {
                Toast.makeText(getContext(), "Access denied!", Toast.LENGTH_SHORT).show();
            }
        } else { // 文件时
            if (mBrowserListener != null) {
                mBrowserListener.onFileItemClick(file.getPath());
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果按下的是返回键、相应返回上级目录且不是根目录
        if (isBackResp && keyCode == KeyEvent.KEYCODE_BACK && !isRootDir()) {
            toParentDir();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toParentDir() {
        currentDir = currentDir.getParentFile();
        refresh(); // 刷新
        if (mBrowserListener != null) {
            mBrowserListener.onDirItemClick(currentDir.getPath());
        }
    }

    /**
     * @brief 浏览监听接口
     * @author join
     */
    public interface OnBrowserListener {

        /**
         * @brief 文件点击
         * @param filename 文件名
         */
        public void onFileItemClick(String filename);

        /**
         * @brief 目录点击
         * @param path 目录路径
         */
        public void onDirItemClick(String path);

    }

    /**
     * @brief 设定文件浏览监听接口
     */
    public void setOnBrowserListener(OnBrowserListener listener) {
        this.mBrowserListener = listener;
    }

    /**
     * @brief 自定义Adapter类
     */
    private class FileListAdapter extends BaseAdapter {

        private Context context;

        public FileListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return fileList.size();
        }

        @Override
        public Object getItem(int position) {
            return fileList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return createView(position, convertView);
        }

        private View createView(int position, View convertView) {
            View v;
            if (convertView == null) {
                LinearLayout fileLayout = new LinearLayout(context);
                fileLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));
                fileLayout.setOrientation(LinearLayout.HORIZONTAL);
                fileLayout.setGravity(Gravity.CENTER_VERTICAL);
                fileLayout.setPadding(5, 10, 0, 10);

                ImageView ivFile = new ImageView(context);
                ivFile.setTag(1);
                ivFile.setLayoutParams(new LayoutParams(48, 48));

                TextView tvFile = new TextView(context);
                tvFile.setTag(2);
                tvFile.setTextColor(android.graphics.Color.WHITE);
                tvFile.setTextAppearance(context, android.R.style.TextAppearance_Large);
                tvFile.setPadding(5, 5, 0, 0);
                tvFile.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));

                fileLayout.addView(ivFile);
                fileLayout.addView(tvFile);

                v = fileLayout;
            } else {
                v = convertView;
            }
            bindView(position, v);
            return v;
        }

        private void bindView(int position, View view) {
            ImageView ivFile = (ImageView) view.findViewWithTag(1);
            TextView tvFile = (TextView) view.findViewWithTag(2);

            if (fileList.get(position) == null) { // 上一级
                if (folderResId > 0)
                    ivFile.setImageResource(folderParentResId);
                tvFile.setText(parentDirName);
            } else if (fileList.get(position).isDirectory()) { // 文件夹时
                if (folderResId > 0)
                    ivFile.setImageResource(folderResId);
                tvFile.setText(fileList.get(position).getName());
            } else { // 文件时
                tvFile.setText(fileList.get(position).getName());
                Integer resId = extResIdMap.get(getExtName(fileList.get(position).getName()));
                if (resId != null && resId > 0) {
                    ivFile.setImageResource(resId);
                } else if (fileResId > 0) {
                    ivFile.setImageResource(fileResId);
                }
            }
        }

    }

}