package com.outsider.nlp.segmenter;

import java.util.List;

public interface SegmentationPredictor {
	/**
	 * 单个句子分词
	 * @param text
	 * @return
	 */
	String[] seg(String text);
	/**
	 * 多个句子分词
	 * @param texts
	 * @return
	 */
	List<String[]> seg(String[] texts);
}
