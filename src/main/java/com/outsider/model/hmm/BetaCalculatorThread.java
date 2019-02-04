package com.outsider.model.hmm;

import java.util.List;
/**
 * betaº∆À„œﬂ≥Ã
 * @author outsider
 *
 */
public class BetaCalculatorThread implements Runnable{
	private UnsupervisedFirstOrderGeneralHMM hmm;
	private List<SequenceNode> nodes;
	private double[][] beta;
	
	public BetaCalculatorThread(UnsupervisedFirstOrderGeneralHMM hmm, List<SequenceNode> nodes, double[][] beta) {
		super();
		this.hmm = hmm;
		this.nodes = nodes;
		this.beta = beta;
	}

	@Override
	public void run() {
		hmm.calcBeta(nodes, beta);
	}
	
}
