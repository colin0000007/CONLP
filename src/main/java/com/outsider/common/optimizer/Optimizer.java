package com.outsider.common.optimizer;

/**
 * 优化器接口
 * @author outsider
 *
 */
public interface Optimizer {
	/**
	 * 优化求解参数
	 * @param calculator
	 * @param parameters
	 * @param x
	 * @param y
	 * @param lambda
	 * @param alpha
	 * @param epsilon
	 * @param maxIter
	 * @return 返回参数结果
	 */
	double[] optimize(GradientCalculator calculator, 
			double[] parameters, double[][] x, double[] y, 
			float lambda, float alpha, double epsilon, int maxIter);
}
