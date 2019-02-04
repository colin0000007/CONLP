package com.outsider.model.hmm;

import java.util.List;
/**
 * gammaº∆À„œﬂ≥Ã
 * @author outsider
 *
 */
public class GammaCalculatorThread implements Runnable{
	private UnsupervisedFirstOrderGeneralHMM hmm;
	private List<SequenceNode> nodes;
	private double[][] alpha;
	private double[][] beta;
	private double[][] gamma;
	
	public GammaCalculatorThread(UnsupervisedFirstOrderGeneralHMM hmm, List<SequenceNode> nodes, double[][] alpha,
			double[][] beta, double[][] gamma) {
		super();
		this.hmm = hmm;
		this.nodes = nodes;
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
	}

	@Override
	public void run() {
		hmm.calcGamma(nodes, alpha, beta, gamma);
	}

}
