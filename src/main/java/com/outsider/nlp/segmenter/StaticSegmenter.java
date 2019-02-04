package com.outsider.nlp.segmenter;

import com.outsider.constants.nlp.PathConstans;

public class StaticSegmenter {
	private static FirstOrderHMMSegmenter firstOrderHMMSegmenter;
	private static SecondOrderHMMSegmenter secondOrderHMMSegmenter;
	private static SupervisedCRFSegmenter supervisedCRFSegmenter;
	private static CrfppSegmenter crfppSegmenter;
	public static FirstOrderHMMSegmenter getFirstOrderHMMSegmenter() {
		if(firstOrderHMMSegmenter == null) {
			firstOrderHMMSegmenter = new FirstOrderHMMSegmenter();
			firstOrderHMMSegmenter.open(PathConstans.FIRST_ORDER_HMM_SEGMENTER, null);
		}
		return firstOrderHMMSegmenter;
	}
	public static SecondOrderHMMSegmenter getSecondOrderHMMSegmenter() {
		if(secondOrderHMMSegmenter == null) {
			secondOrderHMMSegmenter = new SecondOrderHMMSegmenter();
			secondOrderHMMSegmenter.open(PathConstans.SEOND_ORDER_HMM_SEGMENTER, null);
		}
		return secondOrderHMMSegmenter;
	}
	
	public static SupervisedCRFSegmenter getSupervisedCRFSegmenter() {
		if(supervisedCRFSegmenter == null) {
			supervisedCRFSegmenter = new SupervisedCRFSegmenter();
			supervisedCRFSegmenter.open(PathConstans.SUPERVISED_CRF_SEGMENTER, null);
		}
		return supervisedCRFSegmenter;
	}
	
	public static CrfppSegmenter getCRFPPSegmenter() {
		if(crfppSegmenter == null) {
			crfppSegmenter = new CrfppSegmenter();
			crfppSegmenter.open(PathConstans.CRFPP_SEGMENTER);
		}
		return crfppSegmenter;
	}
}
