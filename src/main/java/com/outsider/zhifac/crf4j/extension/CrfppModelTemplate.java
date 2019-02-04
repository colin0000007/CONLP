package com.outsider.zhifac.crf4j.extension;

import com.zhifac.crf4j.ModelImpl;
import com.zhifac.crf4j.Tagger;

/**
 * 一个使用crf++模型的标准模板
 * 使用crf++扩展其他应用可以直接继承这个模板
 * @author outsider
 *
 */
public class CrfppModelTemplate {
	protected Tagger tagger;
	/**
	 * 无参构造需要训练或者打开模型文件生成模型
	 */
	public CrfppModelTemplate() {
	}
	
	/**
	 * 直接构造的时候生成模型
	 * @param modelFile
	 * @param nbest
	 * @param vlevel
	 * @param costFactor
	 */
	public CrfppModelTemplate(String modelFile, int nbest, int vlevel, double costFactor) {
		this.tagger = CrfppUtils.open(modelFile, nbest, vlevel, costFactor);
	}
	/**
	 * 后期打开模型
	 * @param modelFile
	 * @param nbest
	 * @param vlevel
	 * @param costFactor
	 */
	public void open(String modelFile, int nbest, int vlevel, double costFactor) {
		ModelImpl model = new ModelImpl();
		model.open(modelFile, nbest, vlevel, costFactor);
		this.tagger =  model.createTagger();
	}
	public void open(String modelFile) {
		ModelImpl model = new ModelImpl();
		model.open(modelFile, 0, 0, 1);
		this.tagger =  model.createTagger();
	}
	
	/**
	 * 训练接口
	 * @param templateFilePath
	 * @param trainDataFilePath
	 * @param modelFile
	 * @param options
	 */
	public void train(String templateFilePath, String trainDataFilePath, String modelFile,
			String[] options) {
		CrfppTrainer.run(templateFilePath, trainDataFilePath, modelFile, options);
	}
}
