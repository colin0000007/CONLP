package com.outsider.nlp.lexicalanalyzer;

import com.outsider.nlp.postagger.POSTagger;
import com.outsider.nlp.postagger.StaticPOSTagger;
import com.outsider.nlp.segmenter.Segmenter;
import com.outsider.nlp.segmenter.StaticSegmenter;

/**
 * 基础词法分词器
 * 使用一阶HMM分词和一阶HMM词性标注
 * @author outsider
 *
 */
public class BasicLexicalAnalyzer {
	private static Segmenter segmenter = StaticSegmenter.getSegmenter();
	private static POSTagger posTagger = StaticPOSTagger.getPOSTagger();
	
	public static String[] analyze(String sentence) {
		String[] words = segmenter.predictAndReturnTerms(sentence);
		String[] result = posTagger.predictAndReturnStr(words);
		return result;
	}
	
	
}
