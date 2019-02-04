package com.outsider.zhifac.crf4j.extension;

import com.zhifac.crf4j.ModelImpl;
import com.zhifac.crf4j.Tagger;

public class CrfppUtils {
	/**
	 * 根据模型文件生成tagger
	 * @param modelFile
	 * @param nbest
	 * @param vlevel
	 * @param costFactor
	 * @return
	 */
	public static Tagger open(String modelFile, int nbest, int vlevel, double costFactor) {
		ModelImpl model = new ModelImpl();
		model.open(modelFile, nbest, vlevel, costFactor);
		return model.createTagger();
	}
	
	/**
	 * crf++一个通用的标注器，按照输入标签返回字符串标签结果
	 * @param tagger
	 * @param sequence
	 * @return
	 */
	public static String[] tag(Tagger tagger, String[] sequence) {
		//清除之前的待预测数据
		tagger.clear();
		//添加待预测数据
		for(int i = 0; i < sequence.length; i++) {
			tagger.add(sequence[i]);
		}
		//预测
		tagger.parse();
		//转换为char结果并解码
		String[] predict = new String[sequence.length];
		for(int i = 0; i < predict.length; i++) {
			int yInt = tagger.y(i);
			String yName = tagger.yname(yInt);
			predict[i] = yName;
		}
		return predict;
	}
}
