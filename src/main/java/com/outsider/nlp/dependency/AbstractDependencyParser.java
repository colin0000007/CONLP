package com.outsider.nlp.dependency;

import com.outsider.nlp.postagger.POSTagger;
import com.outsider.nlp.postagger.POSTaggingPredictor;
import com.outsider.nlp.segmenter.SegmentationPredictor;

/**
 * 抽象层的依存分析器，主要是为了定义依赖的分词器和词性标注器
 * @author outsider
 *
 */
public abstract class AbstractDependencyParser implements DependencyParser{
	/**
	 * 依赖的分词器
	 */
	protected SegmentationPredictor segmenter;
	/**
	 * 依赖的词性标注器
	 */
	protected POSTaggingPredictor posTagger;
	
	public void setSegmenter(SegmentationPredictor segmenter) {
		this.segmenter = segmenter;
	}
	public void setPosTagger(POSTagger posTagger) {
		this.posTagger = posTagger;
	}
	
}
