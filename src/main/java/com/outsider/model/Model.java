package com.outsider.model;
/**
 * 所有模型的接口
 * @author outsider
 *
 * @param <TrainDataType> 训练数据类型
 * @param <PredictionDataType> 预测数据类型
 * @param <PredictionResultType> 预测结果类型
 */
public interface Model<TrainDataType,PredictionDataType,PredictionResultType> {
	/**
	 * 所有模型的基本训练方法
	 * @param data
	 */
	void train(TrainDataType data);
	/**
	 * 所有模型的基本预测方法
	 * @param predictionData
	 * @return
	 */
	PredictionResultType predict(PredictionDataType predictionData);
	/**
	 * 重新初始化模型的参数，
	 * 在测试模型时可能用到
	 */
	void reInitParameters();
}
