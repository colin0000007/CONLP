package com.outsider.nlp.segmenter;

import com.outsider.model.hmm.SecondOrderGeneralHMM;

public class SecondOrderHMMSegmenter extends SecondOrderGeneralHMM implements Segmenter{

	public SecondOrderHMMSegmenter(int stateNum, int observationNum) {
		super(stateNum, observationNum);
	}
	public SecondOrderHMMSegmenter() {
		super();
	}
	public SecondOrderHMMSegmenter(int stateNum, int observationNum, double[] pi, double[][] transfer_probability1,
			double[][] emission_probability) {
		super(stateNum, observationNum, pi, transfer_probability1, emission_probability);
	}
}
