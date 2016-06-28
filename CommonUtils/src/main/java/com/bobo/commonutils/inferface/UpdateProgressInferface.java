package com.bobo.commonutils.inferface;


/**
 * 进度提醒相关接口
 * update progress inferface
 * @author zhenbohuang
 *
 */
public interface UpdateProgressInferface {
	/**
	 * @param percent: cur percent
	 * @param object: custom object
	 */
	public void progressUpdate(int percent, Object object);
}
