package com.outsider.nlp.postagger;

import java.util.Arrays;
import java.util.List;

import com.outsider.common.util.IOUtils;
import com.outsider.constants.nlp.PathConstans;
import com.outsider.model.hmm.SequenceNode;
import com.outsider.model.metric.Metric;
import com.outsider.nlp.segmenter.CrfppSegmenter;

public class FirstOrderHMMPOSTaggerTest {
	public static void main(String[] args) {
		train();
		use();
		
	}
	
	public static void use() {
		CrfppSegmenter segmenter = new CrfppSegmenter();
		segmenter.open(PathConstans.CRFPP_SEGMENTER);
		String[] words = segmenter.seg("曾经有一份真挚的爱情摆在我面前");
		System.out.println(Arrays.toString(words));
		POSTagger tagger = new FirstOrderHMMPOSTagger();
		tagger.open(PathConstans.FIRST_ORDER_HMM_POSTAGGER, null);
		String[] tags = tagger.tag(words);
		System.out.println(Arrays.toString(tags));
		
	}
	
	public static void train() {
		FirstOrderHMMPOSTagger tagger = new FirstOrderHMMPOSTagger(POSTaggingUtils.getDefaultDictionary(), WordNatureMapping.getCoarseWordNatureMapping());
		List<SequenceNode> nodes = POSTaggingUtils.generateNodesWithCRFormatData("D:\\\\nlp语料\\\\词性标注\\\\词性标注2014_crf_coarseNature.txt", "utf-8", 0, 1
				,tagger.getDictionary(), tagger.getWordNatureMapping());
		System.out.println("结点生成完成...");
		tagger.train(nodes);
		String[] words = new String[] {"你","最近","过得","还好","吗","？"};
		String[] res = tagger.tag(words);
		for(int i = 0; i < res.length; i++) {
			System.out.print(words[i]+"/"+res[i]);
		}
		System.out.println();
		String[] testData = IOUtils.readTextAndReturnLinesOfArray("D:\\\\nlp语料\\\\词性标注\\\\词性标注@人民日报199801_crf.txt", "utf-8");
		int testLen = (int) (testData.length);
		String[] words1 = new String[testLen];
		String[] natures = new String[testLen];
		for(int i = 0; i < testLen; i++) {
			String[] s = testData[i].split("\t");
			words1[i] = s[0];
			natures[i] = s[1].substring(0, 1).toLowerCase();
		}
		String[] predict = tagger.tag(words1);
		for(int i = 0; i < predict.length; i++) {
			predict[i] = predict[i].substring(0, 1).toLowerCase();
		}
		float accuracy = Metric.accuracyScore(predict, natures);
		System.out.println("测试集合准确率:"+accuracy);
		tagger.save(PathConstans.FIRST_ORDER_HMM_POSTAGGER, null);
		//细粒度的词性:FOHMM:0.6641114
		//粗粒度词性:FOHMM:0.89177626
		//SOHMM:0.89011705
		//虽然用细粒度词性训练但是如果结果评测只看粗粒度词性是否正确，那么正确率也还行，测试0.8906333，也就是说虽然细粒度的词性不能完全正确的预测到，但大致粗粒度的词性还是正确的
		// 你/rr最近/t过得/m还好/m吗/y？/w
	}
}
