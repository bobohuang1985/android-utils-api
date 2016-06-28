package com.bobo.commonutils.file;

import com.bobo.commonutils.inferface.UpdateProgressInferface;


import javax.crypto.Cipher;

/**
 * AES文件加密解密接口
 * Encrypt or decrypt file by AES (Advanced Encryption Standard) 
 * @author zhenbohuang
 *
 */
public class AesFileEncryptor implements FileEncryptor{
	AESHelper mAESHelper = new AESHelper();
	@Override
	public int encryptFile(String srcFile, String desFile,String key,UpdateProgressInferface progressInferface) {
		if(FileUtils.getFileType(srcFile)!=FileUtils.TYPE_FILE){
			return FAILED_INVALID_SOURCE_FILE;
		}
		//FileUtils.copyfile(new File(srcFile), new File(desFile), true);
		if(mAESHelper.AESCipher(Cipher.ENCRYPT_MODE, srcFile, desFile, key)){
			return SUCCEEDED;
		}
		return FAILED_UNKNOWN;
	}

	@Override
	public int decryptFile(String srcFile, String desFile,String key,UpdateProgressInferface progressInferface) {
		if(FileUtils.getFileType(srcFile)!=FileUtils.TYPE_FILE){
			return FAILED_INVALID_SOURCE_FILE;
		}
		//FileUtils.copyfile(new File(srcFile), new File(desFile), true);
		if(mAESHelper.AESCipher(Cipher.DECRYPT_MODE, srcFile, desFile, key)){
			return SUCCEEDED;
		}
		return FAILED_UNKNOWN;
	}

}
