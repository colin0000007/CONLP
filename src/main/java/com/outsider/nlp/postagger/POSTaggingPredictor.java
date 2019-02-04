package com.outsider.nlp.postagger;

public interface POSTaggingPredictor {
	/**
	 * 标注方法
	 * @param words 词语数组
	 * @return
	 */
	String[] tag(String[] words);
}
