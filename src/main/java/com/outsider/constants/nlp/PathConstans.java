package com.outsider.constants.nlp;

public class PathConstans {
	/**
	 * 词性标注的词典默认路径
	 */
	public static final String DIC_4_POSTAGGER = "./model/dictionary/dic2014&1998dat";
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
	public static final String FIRST_ORDER_HMM_POSTAGGER = "./model/FirstOrderHMMPOSTagger";
	
	/**
	 * int到词性名字的映射默认路径
	 */
	public static final String WORD_NATURE_MAPPING = "./model/dictionary";
	/**
	 * 监督学习的CRF分词器
	 */
	public static final String SUPERVISED_CRF_SEGMENTER = "./model/SupervisedCRFSegmenter";

	public static final String CRFPP_SEGMENTER = "./model/crfpp/segmenter/crfSeg_ctb8_sku.m";
	
	public static final String DEPENDENCY_PARSER_MAXENT = "./model/dependencyParser/maxent_openlp.bin";
	
	public static final String FIRST_ORDER_HMM_NER = "./model/ner/hmm";
	
	public static final String SUPERVISED_CRF_NER = "./model/ner";

}
