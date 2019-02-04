package com.outsider.nlp.segmenter;

import com.outsider.constants.nlp.PathConstans;
import com.outsider.zhifac.crf4j.extension.CrfppTrainer;
/**
 * crfpp得分：
 * 
 * ctb6：
 * 总精确率:4.420024034248117,总召回率:4.463402966448713,总f得分:4.44099658851161
 * 
 * pku:
 * 总精确率:4.435005985946301,总召回率:4.434995239677223,总f得分:4.4344368523197915

   ctb8:
   总精确率:4.435005985946301,总召回率:4.434995239677223,总f得分:4.4344368523197915
   
   ctb8+sku:
   总精确率:4.533188119773194,总召回率:4.588728141143836,总f得分:4.560181879960174
 * @author outsider
 *
 */
public class CrfppSegmenterTest {
	public static CrfppSegmenter segmenter;
	
	
	public static void loadModel(String modelFile) {
		segmenter = new CrfppSegmenter(modelFile, 0, 0, 1);
	}
	/**
	 * crfpp训练出来的各模型得分:
	 * crfpp_ctb6.m:
	 * 总精确率:4.420024034248117,总召回率:4.463402966448713,总f得分:4.44099658851161
	 * crfSeg_pku.m:
	 * 总精确率:4.435005985946301,总召回率:4.434995239677223,总f得分:4.4344368523197915
	 */
	
	public static void train(int maxIter) {
		String ctb8_sku = "./data/ctb8_sku_crf.utf8";
		String template = "./model/crfpp/segmentationTemplate";
		String[] options = new String[] {"-m", maxIter+"","-t"};
		CrfppTrainer.run(template, ctb8_sku, "./model/crfpp/crfSeg_ctb8_sku_train.m", options);
	}
	public static void main(String[] args) {
		//训练
		//train(1000);
		//使用
		loadModel(PathConstans.CRFPP_SEGMENTER);
		TestSeg.testSeg(segmenter);
		SegmenterTest.score(segmenter, "crfpp");
	}
	
}
