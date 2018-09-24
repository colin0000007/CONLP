package com.outsider.constants.nlp;

public class PathConstans {
	/**
	 * 词性标注的词典默认路径
	 */
	public static final String DIC_4_POSTAGGER = "./model/dictionary/dic2014&1998dat";
	/**
	 * 词性标注int到词典的映射默认路径
	 */
	public static final String WORD_NATURE_MAPPING_4_POSTAGGER = "./model/dictionary/int2wordNatureMap";
	/**
	 * 一阶HMM分词模型默认路径
	 */
	public static final String FIRST_ORDER_HMM_SEGMENTER = "./model/FirstOrderHMMSegmenter";
	/**
	 * 二阶HMM分词默认路径
	 */
	public static final String SEOND_ORDER_HMM_SEGMENTER = "./model/SecondOrderHMMSegmenter";
	/**
	 * 一阶HMM词性标注模型默认路径
	 */
	public static final String FIRST_ORDER_HMM_POSTAGGER = "./model/SecondOrderHMMPOSTagger";
	
	/**
	 * int到词性名字的映射默认路径
	 */
	public static final String INT_2_WORD_NATURE_MAPPING = "./model/dictionary/int2NatureName";
	/**
	 * 
	 */
	public static final String WORD_NATURE_2_INT_MAPPING = "./model/dictionary/natureName2Int";
}
