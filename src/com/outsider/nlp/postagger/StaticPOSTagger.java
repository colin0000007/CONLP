package com.outsider.nlp.postagger;

public class StaticPOSTagger {
	private static FirstOrderHMMPOSTagger firstOrderHMMPOSTagger = new FirstOrderHMMPOSTagger();
	static {
		firstOrderHMMPOSTagger.loadModel(null);
	}
	public static POSTagger getPOSTagger() {
		return firstOrderHMMPOSTagger;
	}
}
