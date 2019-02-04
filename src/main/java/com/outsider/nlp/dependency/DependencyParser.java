package com.outsider.nlp.dependency;

import java.util.List;

public interface DependencyParser {
	/**
	 * 依存句法分析方法
	 * @param words 词组
	 * @param natures 词性组
	 * @return 依存分析结果，格式为CoNLL格式
	 */
	CoNLLSentence parse(String[] words, String[] natures);
	/**
	 * 依存句法分析
	 * @param words 多个词组
	 * @param natures 多个词性组
	 * @return 多个依存句法分词结果，格式为CoNLL格式
	 */
	CoNLLSentence[] parse(List<String[]> wordsOfSentences, List<String[]> naturesOfSentences);
	/**
	 * 依存句法分析，依赖分词器和词性标注器
	 * @param sentence 未处理的句子
	 * @return 依存分析结果 ，格式为CoNLL格式
	 */
	CoNLLSentence parse(String sentence);
	/**
	 * 依存句法分析，依赖分词器和词性标注器
	 * @param sentences 多个句子
	 * @return 依存分析结果 ，格式为CoNLL格式
	 */
	CoNLLSentence[] parse(String[] sentences);
}
