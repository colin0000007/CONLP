package com.outsider.nlp.segmenter;

import com.zhifac.crf4j.Tagger;

/**
 * 为Crfpp适配的一个分词工具类
 * @author outsider
 *
 */
public class SegmentationUtils4Crfpp {
	/**
	 * 分词方法，标注结果必须是BMES，大写
	 * @param sentence 句子
	 * @param tagger crfpp标注器
	 * @return
	 */
	public static String[] seg(String sentence, Tagger tagger) {
		//清除之前的待预测数据
		tagger.clear();
		//添加待预测数据
		for(int i = 0; i < sentence.length(); i++) {
			tagger.add(sentence.charAt(i) + "");
		}
		//预测
		tagger.parse();
		//转换为char结果并解码
		char[] predict = new char[sentence.length()];
		for(int i = 0; i < predict.length; i++) {
			int yInt = tagger.y(i);
			char yName = tagger.yname(yInt).charAt(0);
			predict[i] = yName;
		}
		//解码
		return SegmentationUtils.decode(predict, sentence);
	}
}
