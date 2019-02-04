package com.outsider.common.optimizer;

/**
 *梯度下降中的梯度计算需要外部实现，所以这里定义接口
 * @author outsider
 *
 */
public interface GradientCalculator extends Calculator{
	/**
	 * 梯度的计算,需要计算出一组梯度的值
	 * @param parameters 当前的参数值
	 * @param x 一组样本的x值
	 * @param y 样本的y值
	 * @param lambda 正则项的乘法系数
	 * @param alpha 学习效率
	 * @return 返回计算好的一组梯度值
	 */
	double[] calcGradient(double[] parameters, double[][] x, 
			double[] y, float lambda, float alpha);
	
}
