package com.bobo.commonutils.file;


import com.bobo.commonutils.inferface.UpdateProgressInferface;

/**
 * 加密、解密文件接口
 * For encrypt or decrypt file
 * @author zhenbohuang
 *
 */
public interface FileEncryptor {
	/**
	 * 加密/解密返回值：成功
	 * Encrypt/Decrypt return code: success
	 */
	public static final int SUCCEEDED = 1;
	
	/**
	 * 加密/解密返回值：无效的源文件,比如不存在
	 * Encrypt/Decrypt return code: Invaild source file
	 */
	public static final int FAILED_INVALID_SOURCE_FILE = -1;
	
	/**
	 * 加密/解密返回值：目标空间不足
	 * Encrypt/Decrypt return code: Insufficient memory on specified media
	 */
	public static final int FAILED_INSUFFICIENT_STORAGE = -2;
	
	/**
	 * 加密/解密返回值：未知错误
	 * Encrypt/Decrypt return code: Unknown error
	 */
	public static final int FAILED_UNKNOWN = -0xff;
	/**
	 * 加密文件
	 * Encrypt file
	 * @param srcFile 源文件  Source file
	 * @param desFile 加密目标文件  Destination file
	 * @param key 密钥 encrypt's key
	 * @return 
	 * {@link #SUCCEEDED}
	 * {@link #FAILED_INVALID_SOURCE_FILE}
	 * {@link #FAILED_INSUFFICIENT_STORAGE}
	 * {@link #FAILED_UNKNOWN}
	 */
	public int encryptFile(String srcFile, String desFile, String key, UpdateProgressInferface progressInferface);
	/**
	 * 解密文件
	 * Decrypt file
	 * @param srcFile 加密源文件  Source file
	 * @param desFile 解密目标文件  Destination file
	 * @param key 密钥 encrypt's key
	 * @return
	 * {@link #SUCCEEDED}
	 * {@link #FAILED_INVALID_SOURCE_FILE}
	 * {@link #FAILED_INSUFFICIENT_STORAGE}
	 * {@link #FAILED_UNKNOWN}
	 */
	public int decryptFile(String srcFile, String desFile, String key, UpdateProgressInferface progressInferface);
}
