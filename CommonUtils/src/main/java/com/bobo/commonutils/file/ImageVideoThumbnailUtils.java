package com.bobo.commonutils.file;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

public class ImageVideoThumbnailUtils {
	/**
	 * get video's thumbnail
	 * @param context
	 * @param videopath 
	 *   视频路径
	 *   video's path
	 * @return
	 */
	public static Bitmap getVideoThumbnail(Context context,String videopath){
		Bitmap bitmap = getVideoThumbnailFormMediaStore(context,videopath);
		if(bitmap==null){
			bitmap = ThumbnailUtils.createVideoThumbnail(videopath, Images.Thumbnails.MINI_KIND);
		}
		return bitmap;
	}
	/**
	 * get video's thumbnail
	 * @param context
	 * @param videopath 
	 *   视频路径
	 *   video's path
	 * @return
	 */
	public static Bitmap getImageThumbnail(Context context,String imagepath){
		Bitmap bitmap = getImageThumbnailFormMediaStore(context,imagepath);
		if(bitmap==null){
			bitmap = getImageThumbnail(imagepath, 96,96);
		}
		return bitmap;
	}
	/**
	* 根据指定的图像路径和大小来获取缩略图
	* 此方法有两点好处：
	*     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	*        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
	*     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
	*        用这个工具生成的图像不会被拉伸。
	* @param imagePath 图像的路径
	* @param width 指定输出图像的宽度
	* @param height 指定输出图像的高度
	* @return 生成的缩略图
	*/
	private static Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
	/**
	 * get video's thumbnail form android media store
	 * @param context
	 * @param videopath 
	 *   视频路径
	 *   video's path
	 * @return
	 */
	private static Bitmap getVideoThumbnailFormMediaStore(Context context,
			String videopath) {
		ContentResolver testcr = context.getContentResolver();
		String[] projection = { MediaStore.Video.Media.DATA,
				MediaStore.Video.Media._ID, };
		String whereClause = MediaStore.Video.Media.DATA + " = '" + videopath
				+ "'";
		Cursor cursor = testcr.query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
				whereClause, null, null);
		int _id = 0;
		//String videoPath = "";
		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		if (cursor.moveToFirst()) {

			int _idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
			//int _dataColumn = cursor
			//		.getColumnIndex(MediaStore.Video.Media.DATA);
			do {
				_id = cursor.getInt(_idColumn);
				//videoPath = cursor.getString(_dataColumn);
			} while (cursor.moveToNext());
		}
		cursor.close();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(testcr, _id,
				Images.Thumbnails.MINI_KIND, options);
		return bitmap;
	}
	/**
	 * get image's thumbnail form android media store
	 * @param context
	 * @param imagepath
	 * 	  图片路径
	 *  image's path
	 * @return
	 */
	private static Bitmap getImageThumbnailFormMediaStore(Context context,String imagepath) {
		ContentResolver testcr = context.getContentResolver();
		String[] projection = { Images.Media.DATA,
				Images.Media._ID, };
		String whereClause = Images.Media.DATA + " = '" + imagepath
				+ "'";
		Cursor cursor = testcr.query(
				Images.Media.EXTERNAL_CONTENT_URI, projection,
				whereClause, null, null);
		int _id = 0;
		//String imagePath = "";
		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		if (cursor.moveToFirst()) {

			int _idColumn = cursor.getColumnIndex(Images.Media._ID);
			//int _dataColumn = cursor
			//		.getColumnIndex(MediaStore.Images.Media.DATA);

			do {
				_id = cursor.getInt(_idColumn);
				//imagePath = cursor.getString(_dataColumn);
			} while (cursor.moveToNext());
		}
		cursor.close();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = Images.Thumbnails.getThumbnail(testcr, _id,
				Images.Thumbnails.MINI_KIND, options);
		return bitmap;
	}
}
