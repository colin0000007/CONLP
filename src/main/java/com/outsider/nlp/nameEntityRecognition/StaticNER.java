package com.outsider.nlp.nameEntityRecognition;

import com.outsider.constants.nlp.PathConstans;

public class StaticNER {
	private static FirstOrderHMMNER firstOrderHMMNER;
	
	public static FirstOrderHMMNER getFirstOrderHMMNER() {
		if(firstOrderHMMNER == null) {
			firstOrderHMMNER = new FirstOrderHMMNER();
			firstOrderHMMNER.open(PathConstans.FIRST_ORDER_HMM_NER, null);
		}
		return firstOrderHMMNER;
	}
	
}
