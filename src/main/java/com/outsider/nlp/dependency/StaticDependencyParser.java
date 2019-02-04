package com.outsider.nlp.dependency;

import com.outsider.nlp.postagger.StaticPOSTagger;
import com.outsider.nlp.segmenter.StaticSegmenter;

public class StaticDependencyParser {
	private static MaxEntDependencyParser maxEntDependencyParser;
	
	public static MaxEntDependencyParser getMaxEntDependencyParser() {
		if(maxEntDependencyParser == null) {
			maxEntDependencyParser = new MaxEntDependencyParser();
			maxEntDependencyParser.setSegmenter(StaticSegmenter.getCRFPPSegmenter());
			maxEntDependencyParser.setPosTagger(StaticPOSTagger.getPOSTagger());
		}
		return maxEntDependencyParser;
	}
}
