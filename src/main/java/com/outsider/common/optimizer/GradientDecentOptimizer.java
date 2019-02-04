package com.outsider.common.optimizer;
public class GradientDecentOptimizer implements Optimizer{
	/**
	 * 梯度下降优化 单线程
	 * @param parameters
	 * @param x
	 * @param lambda
	 * @param alpha
	 */
	public double[] batchGradientDecent(GradientCalculator calculator, 
			double[] parameters, double[][] x, double[] y, 
			float lambda, float alpha, double epsilon, int maxIter) {
		int iter = 1;
		//如果传入的最大迭代次数是<=0说明不想设置这个参数，
		//那么将最大迭代次数设置为默认无限大
		if(maxIter <= 0) {
			maxIter = Integer.MAX_VALUE;
		}
		while(iter <= maxIter) {
			//更新参数
			double[] tmpParameter = parameters.clone();
			double[] gradients = calculator.calcGradient(tmpParameter, x, y,lambda, alpha);
			for(int i = 0; i < parameters.length; i++) {
				parameters[i] = parameters[i] - alpha * gradients[i];
				System.out.println(gradients[i]);
			}
			//之前收敛条件搞错了，收敛直接判断所有梯度小于一个值就认为基本到达最小点
			//判断是否收敛
			boolean isConvergent = true;
			for(int i = 0;i < parameters.length; i++) {
				//只要有一个梯度是大于epsilon的，那么就没有收敛
				if(Math.abs(gradients[i]) > epsilon) {
					isConvergent = false;
					break;
				}
			}
			//如果收敛就返回最终的参数
			if(isConvergent) {
				return parameters;
			}
			System.out.println("iter:"+iter);
			iter++;
		}
		//达到最大迭代次数还是没有返回参数说明参数还没有求解出来
		System.err.println("达到最大迭代次数,参数求解失败!");
		System.err.println("solve parameters failed!");
		return null;
	}

	@Override
	public double[] optimize(GradientCalculator calculator, double[] parameters, double[][] x, double[] y, float lambda,
			float alpha, double epsilon, int maxIter) {
		return batchGradientDecent(calculator, parameters, x, y, lambda, alpha, epsilon, maxIter);
		
	}
}
