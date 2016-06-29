package com.bobo.commonutils.file;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

/**
 * 文件通用操作类
 * Class for file common interface
 * @author zhenbohuang
 *
 */
public class FileUtils {
	private static final String TAG = "FileUtils";
	/**
	 * 文件类型：文件夹
	 * File type: directory
	 */
	public static final int TYPE_DIRECTORY = 0;
	/**
	 * 文件类型：文件
	 * File type: file
	 */
	public static final int TYPE_FILE = 1;
	/**
	 * 错误信息：文件不存在
	 * Error code: file is not exists.
	 */
	public static final int ERROR_FILE_NO_EXISTS = -1;
	/**
	 * 获取文件名
	 * get file name
	 * @param path
	 * @return
	 */
	public static String getFileName(String path){
        int separatorIndex = path.lastIndexOf(File.separator);
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
	}
	/**
	 * 获取文件名
	 * get file name
	 * @param path
	 * @return
	 */
	public static String getFileNameWithoutSuffix(String pathOrName){
		String name = getFileName(pathOrName);
		int lastDot = name.lastIndexOf(".");
		if ((lastDot < 0) || ((lastDot+1)>=name.length()))
			return name;
		return name.substring(0,lastDot);
	}
	/**
	 * 获取文件后缀
	 * git file's suffix
	 * @param pathOrName
	 * @return
	 */
	public static String getFileSuffix(String pathOrName){
		String name = getFileName(pathOrName);
		int lastDot = name.lastIndexOf(".");
        if ((lastDot < 0) || ((lastDot+1)>=name.length()))
            return "";
        String suffix = name.substring(lastDot);
        return suffix;
	}
	/**
	 * 获取文件大小 单位：字节
	 * get file size in bytes
	 * @param path
	 * @return
	 */
	public static long getFileLengthInBytes(String path){
        File file = new File(path);
        if(file.exists()&&file.isFile()){
        	return file.length();
        }
        return 0;
	}
	/**
	 * 获取文件类型
	 * get file type by path
	 * @param path
	 * @return 
	 * {@link #TYPE_DIRECTORY}
	 * {@link #TYPE_FILE}
	 * {@link #ERROR_FILE_NO_EXISTS}
	 */
	public static int getFileType(String path){
		File file = new File(path);
		if(file.exists()){
			if(file.isDirectory()){
				return TYPE_DIRECTORY;
			}
			return TYPE_FILE;
		}
		return ERROR_FILE_NO_EXISTS;
	}
    /**
     * 删除所有文件，包括文件夹
     * Del all file
     * @param file
     */
	public static void deleteAllFile(File file) {
		if (file.exists() == false) {
			return;
		} else {
			if (file.isFile()) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
				    file.delete();
					return;
				}
				for (File f : childFile) {
					deleteAllFile(f);
				}
			    file.delete();
			}
		}
	}
	/**
	 * 创建目录
	 * Create directory and is's parents
	 * @param dir
	 * @return true: create success ; false: create fail.
	 */
	public static boolean makeDirs(String dir) {
        File file = new File(dir);
        if(!file.exists()){
            return file.mkdirs();
        }
        return true;
    }
	/**
	 * 拷贝文件
	 * copy file
	 * @param fromFile 
	 * source file
	 * 源文件
	 * @param toFile
	 * 目标文件
	 *  Destination file
	 * @param rewrite
	 * @return true: copy success ; false: copy fail.
	 */
	public static boolean copyfile(File fromFile, File toFile,Boolean rewrite){
		if (!fromFile.exists()) {
			return false;
		}
		if (!fromFile.isFile()) {
			return false;
		}
		if (!fromFile.canRead()) {
			return false;
		}
		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists()) {
			if(rewrite){
				toFile.delete();
			}else{
				return false;
			}
		}
		try {
			java.io.FileInputStream fosfrom = new java.io.FileInputStream(
					fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024*20];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c); // 将内容写到新文件当中
			}
			fosfrom.close();
			fosto.close();
			return true;
		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
		}
		return false;
	}
	/**
	 * 根据uri获取文件的路径
	 * Get file path by file's uri
	 * @param context
	 * @param uri
	 * @return
	 */
	/*public static String getUriFilePath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
        	final String uriAuth = uri.getAuthority();
        	if(MediaStore.AUTHORITY.equals(uriAuth)){
        		MediaStore.MediaColumns.DATA
        	}
        	
            String[] projection = { "_data" };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }finally{
            	if(cursor!=null)
            		cursor.close();
            }
        }else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
 
        return null;
    }*/
	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @author paulburke
	 */
	public static String getUriFilePath(final Context context, final Uri uri) {

	    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

	    // DocumentProvider
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
	        // ExternalStorageProvider
	        if (isExternalStorageDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            if ("primary".equalsIgnoreCase(type)) {
	                return Environment.getExternalStorageDirectory() + "/" + split[1];
	            }

	            // TODO handle non-primary volumes
	        }
	        // DownloadsProvider
	        else if (isDownloadsDocument(uri)) {

	            final String id = DocumentsContract.getDocumentId(uri);
	            final Uri contentUri = ContentUris.withAppendedId(
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

	            return getDataColumn(context, contentUri, null, null);
	        }
	        // MediaProvider
	        else if (isMediaDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            Uri contentUri = null;
	            if ("image".equals(type)) {
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	            } else if ("video".equals(type)) {
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	            } else if ("audio".equals(type)) {
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	            }

	            final String selection = "_id=?";
	            final String[] selectionArgs = new String[] {
	                    split[1]
	            };

	            return getDataColumn(context, contentUri, selection, selectionArgs);
	        }
	    }
	    // MediaStore (and general)
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {
	        return getDataColumn(context, uri, null, null);
	    }
	    // File
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
	        String[] selectionArgs) {

	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = {
	            column
	    };

	    try {
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
	                null);
	        if (cursor != null && cursor.moveToFirst()) {
	            final int column_index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(column_index);
	        }
	    } finally {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		//MediaStore.AUTHORITY.equalsIgnoreCase(uri.getAuthority());
	    return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
	public static String getNewFileAvailableName(String directory,String name){
		File file = new File(directory+File.separator+name);
		if(!file.exists()){
			return name;
		}
		int lastDot = name.lastIndexOf(".");
		String suffix;
		String newName;
        if (lastDot < 0){
        	suffix = "";
        }else{
        	suffix = name.substring(lastDot).toLowerCase();
        }
        if (lastDot < 0){
        	newName = new String(name);
        }else if(lastDot == 0){
        	newName = "";
        }else{
        	newName = name.substring(0, lastDot);
        }
        int i = 1;
        while(true){
        	String newFullName = newName+"("+i+")"+suffix;
        	file = new File(directory+File.separator+newFullName);
    		if(!file.exists()){
    			return newFullName;
    		}
    		i++;
        }
	}
	/**
	 * 文件类型--音频
	 * File type -- Audio
	 */
	public static final int FILE_TYPE_AUDIO = 1;
	/**
	 * 文件类型--视频
	 * File type -- Video
	 */
	public static final int FILE_TYPE_VIDEO = 2;
	/**
	 * 文件类型--图片
	 * File type -- Picture
	 */
	public static final int FILE_TYPE_IMAGE = 3;
	/**
	 * 文件类型--Apk安装包
	 * File type -- Apk
	 */
	public static final int FILE_TYPE_APK = 4;
	/**
	 * 文件类型--文档
	 * File type -- document
	 */
	public static final int FILE_TYPE_DOCUMENT = 5;
	/**
	 * 文件类型--网页源文件
	 * File type -- html
	 */
	public static final int FILE_TYPE_HTML = 6;
	/**
	 * 文件类型--邮件文件
	 * File type -- Email
	 */
	public static final int FILE_TYPE_OUTLOOK = 7;
	/**
	 * 文件类型--PDF
	 * File type -- PDF
	 */
	public static final int FILE_TYPE_PDF = 8;
	//PPT
	/**
	 * 文件类型--PPT
	 * File type -- PPT
	 */
	public static final int FILE_TYPE_PRESENTATION = 9;
	//EXCEL
	/**
	 * 文件类型--Excel
	 * File type -- Excel
	 */
	public static final int FILE_TYPE_SPREADSHEET = 10;
	/**
	 * 文件类型--Txt
	 * File type -- Txt
	 */
	public static final int FILE_TYPE_TXT = 11;
	/**
	 * 文件类型--电子名片
	 * File type -- Vcard
	 */
	public static final int FILE_TYPE_VCF = 12;
	/**
	 * 文件类型--压缩包
	 * File type -- zip
	 */
	public static final int FILE_TYPE_ZIP = 13;
	/**
	 * 文件类型--压缩包
	 * File type -- rar
	 */
	//public static final int FILE_TYPE_RAR = 14;
	/**
	 * 文件类型--未知
	 * File type -- unknown
	 */
	public static final int FILE_TYPE_UNKNOWN = -1;
	static class MediaFileType {
	    
        int fileType;
        String mimeType;
        
        MediaFileType(int fileType, String mimeType) {
            this.fileType = fileType;
            this.mimeType = mimeType;
        }
    }
	private static HashMap<String, MediaFileType> sFileTypeMap 
    	= new HashMap<String, MediaFileType>();
	static{
		addFileType(".mp3", FILE_TYPE_AUDIO, "audio/mpeg");
        addFileType(".m4a", FILE_TYPE_AUDIO, "audio/mp4");
        addFileType(".wav", FILE_TYPE_AUDIO, "audio/x-wav");
        addFileType(".amr", FILE_TYPE_AUDIO, "audio/amr");
        addFileType(".awb", FILE_TYPE_AUDIO, "audio/amr-wb");
        addFileType(".wma", FILE_TYPE_AUDIO, "audio/x-ms-wma");    
        addFileType(".ogg", FILE_TYPE_AUDIO, "application/ogg");  
        addFileType(".midi", FILE_TYPE_AUDIO, "audio/midi");
        addFileType(".mid", FILE_TYPE_AUDIO, "audio/midi");
        addFileType(".xmf", FILE_TYPE_AUDIO, "audio/midi");
        addFileType(".rtttl",FILE_TYPE_AUDIO, "audio/midi");
        addFileType(".smf", FILE_TYPE_AUDIO, "audio/sp-midi");
        addFileType(".imy", FILE_TYPE_AUDIO, "audio/imelody");
        addFileType(".m3u", FILE_TYPE_AUDIO, "audio/x-mpegurl");
        addFileType(".pls", FILE_TYPE_AUDIO, "audio/x-scpls");
        addFileType(".mpega", FILE_TYPE_AUDIO, "audio/x-mpeg");
        addFileType(".aac", FILE_TYPE_AUDIO, "audio/aac");
        
        addFileType(".mp4", FILE_TYPE_VIDEO, "video/mp4");
        addFileType(".m4v", FILE_TYPE_VIDEO, "video/mp4");
        addFileType(".3gp", FILE_TYPE_VIDEO, "video/3gpp");
        addFileType(".3gpp", FILE_TYPE_VIDEO, "video/3gpp");
        addFileType(".3g2", FILE_TYPE_VIDEO, "video/3gpp2");
        addFileType(".3gpp2", FILE_TYPE_VIDEO, "video/3gpp2");
        addFileType(".wmv", FILE_TYPE_VIDEO, "video/x-ms-wmv");
        addFileType(".avi", FILE_TYPE_VIDEO, "video/x-msvideo");
        addFileType(".avx", FILE_TYPE_VIDEO, "video/x-rad-screenplay");
        addFileType(".rm", FILE_TYPE_VIDEO, "application/vnd.rn-realmedia");
        addFileType(".rmvb", FILE_TYPE_VIDEO, "application/vnd.rn-realmedia");
        addFileType(".mpeg", FILE_TYPE_VIDEO, "video/mpeg");
        addFileType(".mpg", FILE_TYPE_VIDEO, "video/mpeg");
        addFileType(".mpe", FILE_TYPE_VIDEO, "video/mpeg");
        addFileType(".mp2", FILE_TYPE_VIDEO, "video/mpeg");
        addFileType(".mpa", FILE_TYPE_VIDEO, "video/mpeg");
        addFileType(".mpv2", FILE_TYPE_VIDEO, "video/mpeg");
        addFileType(".mov", FILE_TYPE_VIDEO, "video/quicktime");
        addFileType(".qt", FILE_TYPE_VIDEO, "video/quicktime");
        
        addFileType(".jpg", FILE_TYPE_IMAGE, "image/jpeg");
        addFileType(".jpe", FILE_TYPE_IMAGE, "image/jpeg");
        addFileType(".jpeg", FILE_TYPE_IMAGE, "image/jpeg");
        addFileType(".jfif", FILE_TYPE_IMAGE, "image/pipeg");
        addFileType(".gif", FILE_TYPE_IMAGE, "image/gif");
        addFileType(".png", FILE_TYPE_IMAGE, "image/png");
        addFileType(".bmp", FILE_TYPE_IMAGE, "image/x-ms-bmp");
        addFileType(".wbmp", FILE_TYPE_IMAGE, "image/vnd.wap.wbmp");
        addFileType(".mpo", FILE_TYPE_IMAGE, "image/*");
        addFileType(".ico", FILE_TYPE_IMAGE, "image/x-icon");
        addFileType(".ief", FILE_TYPE_IMAGE, "image/ief");
        
        addFileType(".apk", FILE_TYPE_APK, "application/vnd.android.package-archive");
        
        addFileType(".doc", FILE_TYPE_DOCUMENT, "application/msword");
        addFileType(".dot", FILE_TYPE_DOCUMENT, "application/msword");
        addFileType(".docx", FILE_TYPE_DOCUMENT, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        addFileType(".dotx", FILE_TYPE_DOCUMENT, "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        addFileType(".docm", FILE_TYPE_DOCUMENT, "application/vnd.ms-word.document.macroEnabled.12");
        addFileType(".dotm", FILE_TYPE_DOCUMENT, "application/vnd.ms-word.template.macroEnabled.12");
       
        addFileType(".html", FILE_TYPE_HTML, "text/html");
        addFileType(".htm", FILE_TYPE_HTML, "text/html");
        addFileType(".xhtml", FILE_TYPE_HTML, "application/xhtml+xml");
        
        addFileType(".msg", FILE_TYPE_OUTLOOK, "application/msg");
        addFileType(".eml", FILE_TYPE_OUTLOOK, "application/eml");
        
        addFileType(".pdf", FILE_TYPE_PDF, "application/pdf");
        
        addFileType(".ppt", FILE_TYPE_PRESENTATION, "application/vnd.ms-powerpoint");
        addFileType(".pot", FILE_TYPE_PRESENTATION, "application/vnd.ms-powerpoint");
        addFileType(".pps", FILE_TYPE_PRESENTATION, "application/vnd.ms-powerpoint");
        addFileType(".ppa", FILE_TYPE_PRESENTATION, "application/vnd.ms-powerpoint");
        addFileType(".pptx", FILE_TYPE_PRESENTATION, "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        addFileType(".potx", FILE_TYPE_PRESENTATION, "application/vnd.openxmlformats-officedocument.presentationml.template");
        addFileType(".ppsx", FILE_TYPE_PRESENTATION, "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        addFileType(".ppam", FILE_TYPE_PRESENTATION, "application/vnd.ms-powerpoint.addin.macroEnabled.12");
        addFileType(".pptm", FILE_TYPE_PRESENTATION, "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
        addFileType(".potm", FILE_TYPE_PRESENTATION, "application/vnd.ms-powerpoint.template.macroEnabled.12");
        addFileType(".ppsm", FILE_TYPE_PRESENTATION, "application/vnd.ms-powerpoint.slideshow.macroEnabled.12");
        
        
        addFileType(".xls", FILE_TYPE_SPREADSHEET, "application/vnd.ms-excel");
        addFileType(".xlt", FILE_TYPE_SPREADSHEET, "application/vnd.ms-excel");
        addFileType(".xla", FILE_TYPE_SPREADSHEET, "application/vnd.ms-excel");
        addFileType(".xlsx", FILE_TYPE_SPREADSHEET, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        addFileType(".xltx", FILE_TYPE_SPREADSHEET, "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        addFileType(".xlsm", FILE_TYPE_SPREADSHEET, "application/vnd.ms-excel.sheet.macroEnabled.12");
        addFileType(".xltm", FILE_TYPE_SPREADSHEET, "application/vnd.ms-excel.template.macroEnabled.12");
        addFileType(".xlam", FILE_TYPE_SPREADSHEET, "application/vnd.ms-excel.addin.macroEnabled.12");
        addFileType(".xlsb", FILE_TYPE_SPREADSHEET, "application/vnd.ms-excel.sheet.binary.macroEnabled.12");
        
        addFileType(".txt", FILE_TYPE_TXT, "text/plain");
        
        addFileType(".vcf", FILE_TYPE_VCF, "text/vcard");
        
        
        addFileType(".zip", FILE_TYPE_ZIP, "application/zip");
        addFileType(".rar", FILE_TYPE_ZIP, "application/x-rar-compressed");
        
        //addFileType("WPL", FILE_TYPE_WPL, "application/vnd.ms-wpl");
	}
	static void addFileType(String extension, int fileType, String mimeType) {
        sFileTypeMap.put(extension, new MediaFileType(fileType, mimeType));
    }
	/**
	 * 获取文件的类型
	 * Get file's detail type 
	 * @param pathOrName file's name or path
	 * @return
	 * {@link #FILE_TYPE_AUDIO}
	 * {@link #FILE_TYPE_VIDEO}
	 * {@link #FILE_TYPE_IMAGE}
	 * {@link #FILE_TYPE_APK}
	 * {@link #FILE_TYPE_DOCUMENT}
	 * {@link #FILE_TYPE_HTML}
	 * {@link #FILE_TYPE_OUTLOOK}
	 * {@link #FILE_TYPE_PDF}
	 * {@link #FILE_TYPE_PRESENTATION}
	 * {@link #FILE_TYPE_SPREADSHEET}
	 * {@link #FILE_TYPE_TXT}
	 * {@link #FILE_TYPE_VCF}
	 * {@link #FILE_TYPE_ZIP}
	 */
	public static int getFileDetailType(String pathOrName){
		int lastDot = pathOrName.lastIndexOf(".");
        if ((lastDot < 0) || ((lastDot+1)>=pathOrName.length()))
            return FILE_TYPE_UNKNOWN;
        String suffix = pathOrName.substring(lastDot).toLowerCase();
        MediaFileType type =  sFileTypeMap.get(suffix);
        if(type!=null){
        	return type.fileType;
        }else{
        	return FILE_TYPE_UNKNOWN;
        }
	}
	public static String getFileMimeType(String pathOrName){
		int lastDot = pathOrName.lastIndexOf(".");
        if ((lastDot < 0) || ((lastDot+1)>=pathOrName.length()))
            return null;
        String suffix = pathOrName.substring(lastDot).toLowerCase();
        MediaFileType type =  sFileTypeMap.get(suffix);
        if(type!=null){
        	return type.mimeType;
        }else{
        	return null;
        }
	}
	/**
	 * delete the record in MediaStore
	 * @param context
	 * @param paths the delete files or folders in MediaStore
	 */
	public static void deleteFileInMediaStore(Context context,List<String> paths) {
		if (paths == null || paths.size() <= 0) {
			return;
		}

		Uri uri = MediaStore.Files.getContentUri("external");
		StringBuilder whereClause = new StringBuilder();
		whereClause.append("?");

		int itemsSize = paths.size() - 1;
		for (int i = 0; i < itemsSize; i++) {
			whereClause.append(",?");
		}

		String where = MediaStore.Files.FileColumns.DATA + " IN("
				+ whereClause.toString() + ")";
		// notice that there is a blank before "IN(".
		try {
			if (context != null && !paths.isEmpty()) {
				ContentResolver cr = context.getContentResolver();
				String[] whereArgs = new String[paths.size()];
				paths.toArray(whereArgs);
				//LogUtils.d(FileGlobal.TAG, "deleteFileInMediaStore,delete.");
				cr.delete(uri, where, whereArgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * delete the record in MediaStore
	 * @param context
	 * @param path
	 *            the delete file or folder in MediaStore
	 */
	public static void deleteFileInMediaStore(Context context,String path) {
		if (TextUtils.isEmpty(path)) {
			return;
		}
		Uri uri = MediaStore.Files.getContentUri("external");
		String where = MediaStore.Files.FileColumns.DATA + "=?";
		String[] whereArgs = new String[] { path };

		try {
			if (context != null) {
				ContentResolver cr = context.getContentResolver();
				cr.delete(uri, where, whereArgs);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
