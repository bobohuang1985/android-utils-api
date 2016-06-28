package com.bobo.commonutils.file;

import java.io.File;
import java.io.FileOutputStream;
import android.util.Log;

import com.bobo.commonutils.inferface.UpdateProgressInferface;

/**
 * 简单的文件加密解密接口
 * Encrypt or decrypt file
 * @author zhenbohuang
 *
 */
public class EasyFileEncryptor implements FileEncryptor{
	private final static String TAG = "EasyFileEncryptor";
	@Override
	public int encryptFile(String srcFile, String desFile, String key,UpdateProgressInferface progressInferface) {
		xorFile(srcFile,desFile,key,progressInferface);
		return SUCCEEDED;
	}

	@Override
	public int decryptFile(String srcFile, String desFile, String key,UpdateProgressInferface progressInferface) {
		xorFile(srcFile,desFile,key,progressInferface);
		return SUCCEEDED;
	}
	private static final int KEY_LENGTH = 8;
	private void xorFile(String srcFile, String desFile, String key,UpdateProgressInferface progressInferface){
		try {
			byte[] rawKey = key.getBytes("UTF-8");
			if(rawKey.length<8){
				rawKey = new byte[]{0,1,2,3,4,5,6,7};
			}
			final long totalLen = FileUtils.getFileLengthInBytes(srcFile);
			long curLen = 0;
			java.io.FileInputStream fosfrom = new java.io.FileInputStream(
					new File(srcFile));
			FileOutputStream fosto = new FileOutputStream(new File(desFile));
			byte bt[] = new byte[1024*20];
			int c;
			int keyIndex = 0;
			boolean firstDoXor = false;
			//开始一段使用异或加密
			//中间文件对调
			//最后一段如果不是对称的，也使用异或加密
			while ((c = fosfrom.read(bt)) > 0) {
				keyIndex = 0;
				if((!firstDoXor)||((c%2)!=0)){
					firstDoXor = true;
					for(int i=0;i<c;i++){
						bt[i] = (byte) (bt[i]^rawKey[keyIndex]);
						keyIndex++;
						if(keyIndex>=KEY_LENGTH){
							keyIndex = 0;
						}
					}
					fosto.write(bt, 0, c); // 将内容写到新文件当中
				}else{
					fosto.write(bt, c/2, c/2); // 将内容写到新文件当中
					fosto.write(bt, 0, c/2); // 将内容写到新文件当中
				}
				curLen += c;
				if(progressInferface!=null){
					progressInferface.progressUpdate((int)(curLen*100/totalLen), null);
				}
				if(Thread.interrupted()){
					break;
				}
			}
			fosfrom.close();
			fosto.close();
		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
		}
	}
}
