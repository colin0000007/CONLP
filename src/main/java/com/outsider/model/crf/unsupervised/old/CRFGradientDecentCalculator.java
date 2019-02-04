package com.outsider.model.crf.unsupervised.old;

import com.outsider.common.optimizer.GradientCalculator;

public class CRFGradientDecentCalculator implements GradientCalculator{
	
	private UnsupervisedCRF crf;
	public CRFGradientDecentCalculator(UnsupervisedCRF crf) {
		this.crf = crf;
	}
	@Override
	public double[] calcGradient(double[] parameters, double[][] x, double[] y, float lambda, float alpha) {
		
		
		//return crf.calcGradient(alpha, beta, observations);
		return null;
	}

}
