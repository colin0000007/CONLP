package com.outsider.nlp.postagger;

import java.util.List;

import com.outsider.common.util.IOUtils;
import com.outsider.model.hmm.SequenceNode;
import com.outsider.model.metric.Metric;
import com.outsider.nlp.lexicalanalyzer.LexicalAnalysisResult;
import com.outsider.zhifac.crf4j.extension.CrfppModelTemplate;
import com.outsider.zhifac.crf4j.extension.CrfppUtils;

public class CrfppPostagger extends CrfppModelTemplate implements POSTaggingPredictor{
	
	
	@Override
	public String[] tag(String[] words) {
		return CrfppUtils.tag(tagger, words);
	}
	
	
	public static void main(String[] args) {
		CrfppPostagger postagger = new CrfppPostagger();
		String trainPath = "D:\\nlp语料\\词性标注\\词性标注_crf.txt";
		String templateFilePath = "./model/crfpp/postaggingTemplate";
		String modelFile = "./model/crfpostagger.m";
		String[] options = new String[] {"-f","2"};
		postagger.train(templateFilePath, trainPath, modelFile, options);
		//D:\nlp语料\词性标注
		String[] words = new String[] {"你","最近","过得","还好","吗","？"};
		String[] res = postagger.tag(words);
		for(int i = 0; i < res.length; i++) {
			System.out.print(words[i]+"/"+res[i]);
		}
		String[] testData = IOUtils.readTextAndReturnLinesOfArray("D:\\\\nlp语料\\\\词性标注\\\\词性标注@人民日报199801_crf.txt", "utf-8");
		int testLen = (int) (testData.length * 0.5);
		String[] words1 = new String[testLen];
		String[] natures = new String[testLen];
		for(int i = 0; i < testLen; i++) {
			String[] s = testData[i].split("\t");
			words1[i] = s[0];
			natures[i] = s[1];
		}
		String[] predict = postagger.tag(words1);
		float accuracy = Metric.accuracyScore(predict, natures);
		System.out.println("测试集合准确率:"+accuracy);
	}
}
