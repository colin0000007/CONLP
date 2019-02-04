package com.outsider.model.hmm;

import java.util.List;
/**
 * ksiº∆À„œﬂ≥Ã
 * @author outsider
 *
 */
public class KsiCalculatorThread implements Runnable{
	private UnsupervisedFirstOrderGeneralHMM hmm;
	private List<SequenceNode> nodes;
	private double[][] alpha;
	private double[][] beta;
	private double[][][] ksi;
	
	public KsiCalculatorThread(UnsupervisedFirstOrderGeneralHMM hmm, List<SequenceNode> nodes, double[][] alpha,
			double[][] beta, double[][][] ksi) {
		super();
		this.hmm = hmm;
		this.nodes = nodes;
		this.alpha = alpha;
		this.beta = beta;
		this.ksi = ksi;
	}

	@Override
	public void run() {
		hmm.calcKsi(nodes, alpha, beta, ksi);
	}
	
}
