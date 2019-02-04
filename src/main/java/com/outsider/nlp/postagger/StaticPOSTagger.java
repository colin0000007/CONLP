package com.outsider.nlp.postagger;

import com.outsider.constants.nlp.PathConstans;

public class StaticPOSTagger {
	private static FirstOrderHMMPOSTagger firstOrderHMMPOSTagger;
	public static FirstOrderHMMPOSTagger getPOSTagger() {
		if(firstOrderHMMPOSTagger == null) {
			firstOrderHMMPOSTagger = new FirstOrderHMMPOSTagger();
			firstOrderHMMPOSTagger.open(PathConstans.FIRST_ORDER_HMM_POSTAGGER, null);
		}
		return firstOrderHMMPOSTagger;
	}
}
