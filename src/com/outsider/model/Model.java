package com.outsider.model;

/**
 * 为了方便以后扩展
 * 所有模型都基于此模型接口
 * @author outsider
 *
 */
public interface Model<TrainDataType,ParameterType,ReturnType,PredictDataType> {
	/**
	 * 底层的train方法，所有模型必须通过此方法才能训练
	 * @param data
	 * @param otherParameters
	 */
	void train(TrainDataType data, ParameterType...otherParameters);
	/**
	 * 底层predict的方法，所有模型必须通过此方法才能预测
	 * @param data
	 * @param otherParameters
	 * @return
	 */
	ReturnType predict(PredictDataType data, ParameterType...otherParameters);
	/**
	 * 保存模型
	 * @param path
	 */
	void saveModel(String directory);
	/**
	 * 加载模型
	 * @param path
	 */
	Object loadModel(String directory);
}
