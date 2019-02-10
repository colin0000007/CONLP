package com.outsider.nlp.nameEntityRecognition;

import java.util.ArrayList;
import java.util.List;

import com.outsider.common.util.IOUtils;
import com.outsider.common.util.StringUtils;
import com.outsider.model.metric.NERTaggerEvaluation;

public class NERTaggerTest {
	public static String MSRTestTagged = "./data/ner/testright.txt";
	public static String MSRTestText = "./data/ner/test.txt";
	public static List<Entity> entities;
	public static String text;
	
	public static NERTaggerEvaluation score(NER ner) {
		if(entities == null || text == null) {
			String[] datas = IOUtils.loadSegmentionCorpus(MSRTestTagged, "utf-8", " ");
			entities = NERUtils.extractEntityFromText(datas);
			text = IOUtils.readText(MSRTestText, "utf-8");
		}
		List<Entity> predict = ner.extractEntity(text);
		NERTaggerEvaluation evaluation = new NERTaggerEvaluation();
		evaluation.score(entities, predict);
		evaluation.printScore();
		return evaluation;
	}
	
	public static void main(String[] args) {
		
	}
}
