package com.outsider.nlp.postagger;

import com.outsider.common.util.Storable;
import com.outsider.model.SequenceModel;
import com.outsider.nlp.lexicalanalyzer.LexicalAnalysisResult;

/**
 * 词性标注接口
 * @author outsider
 * 继承Storable和SequenceModel是为了暴露接口，但是也有相应的缺点
 */
public interface POSTagger extends Storable, SequenceModel, POSTaggingPredictor{
	/**
	 * 直接传入处理好的数据，LexicalAnalysisResult中封装了词语数组和对应的词性数组
	 * @param result
	 */
	void train(LexicalAnalysisResult result);
}
