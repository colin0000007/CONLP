package com.outsider.nlp.segmenter;

import java.util.ArrayList;
import java.util.List;

import com.outsider.common.util.IOUtils;
import com.outsider.model.metric.SegmenterEvaluation;

public class SegmenterTest {
	public static String basePath = "./data/segmentation/";
	public static String msrTest = basePath+"msr_test_gold.utf8.txt";
	public static String pkutest = basePath+"pku_test_gold.utf8";
	public static String skuTest = basePath+"sku_test.txt";
	public static String ctb6Test = basePath+"ctb6.test.seg.txt";
	public static String asTest = basePath+"as_test_gold.utf8.txt";
	public static String[] encodings = new String[] {"utf-8","utf-8","utf-8","utf-8","utf-8"};
	public static String[] testSet = new String[] {msrTest,pkutest,skuTest,ctb6Test,asTest};
	public static String[] testSetNames = new String[] {"msrT","pkuT","skuT","ctb6T","asT"};
	public static String[] testSetSplitChats = new String[] {"  ","  ","  "," "," "};
	public static int T_MSR = 0;
	public static int T_PKU = 1;
	public static int T_SKU = 2;
	public static int T_CTB6 = 3;
	public static int T_AS = 4;
	public static List<String[]> test  = new ArrayList<>();
	static {
		for(int j = 0; j < 5; j++) {
			String[] testt = IOUtils.loadSegmentionCorpus(testSet[j], encodings[j], testSetSplitChats[j]);
			test.add(testt);
		}
	}
	
	/**
	 * 设置用于评测的语料
	 * 传入的int值只能从SegmenterTest类中的静态变量T_开头的取
	 * @param testDataId 可选的值有
	 * SegmenterTest.T_AS,SegmenterTest.T_CTB6,
	 * SegmenterTest.T_MSR,SegmenterTest.T_PKU,
	 * SegmenterTest.T_SKU
	 */
	public static void setTestData(int...testDataId) {
		test.clear();
		for(int id : testDataId) {
			String[] testt = IOUtils.loadSegmentionCorpus(testSet[id], encodings[id], testSetSplitChats[id]);
			test.add(testt);
		}
	}
	public static void score(SegmentationPredictor segmenter, String segmenterName) {
		//不同测测试语料
		double recall = 0;
		double precision = 0;
		double fValue = 0;
		SegmenterEvaluation evaluation = new SegmenterEvaluation();
		for(int j = 0; j < test.size(); j++) {
			String[] testText = test.get(j);
			StringBuilder sb = new StringBuilder();
			for(int k = 0; k < testText.length;k++) {
				sb.append(testText[k]);
			}
			String testData = sb.toString();
			String[] predict = segmenter.seg(testData);
			evaluation.score(testText, predict);
			evaluation.printScore();
			precision += evaluation.getPrecisionScore();
			recall += evaluation.getRecallScore();
			fValue += evaluation.getfMeasureScore();
		}
		System.out.println(segmenterName + ",总精确率:"+precision+",总召回率:"+recall+",总f得分:"+fValue);
		System.out.println();
	}
}
