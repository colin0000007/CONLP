package com.outsider.nlp.nameEntityRecognition;

import java.util.List;

import com.outsider.common.util.IOUtils;
import com.outsider.zhifac.crf4j.extension.CrfppTrainer;
import com.zhifac.crf4j.ModelImpl;
import com.zhifac.crf4j.Tagger;

public class CRFppNER extends CrfppTrainer implements NER{
	private Tagger tagger;
	public CRFppNER() {
	}
	
	public CRFppNER(String modelFile, int nbest, int vlevel, double costFactor) {
		ModelImpl model = new ModelImpl();
		model.open(modelFile, nbest, vlevel, costFactor);
		this.tagger = model.createTagger();
	}
	
	@Override
	public List<Entity> extractEntity(String text) {
		return NERUtils.nerTag(tagger, text);
	}

	@Override
	public List<Entity> getPersonNameEntity(String text) {
		List<Entity> ens = NERUtils.nerTag(tagger, text);
		return NERUtils.getSpecificEntityType(ens, EntityType.PERSON_NAME);
	}

	@Override
	public List<Entity> getLocationEntity(String text) {
		List<Entity> ens = NERUtils.nerTag(tagger, text);
		return NERUtils.getSpecificEntityType(ens, EntityType.LOCATION);
	}

	@Override
	public List<Entity> getOrganizationEntity(String text) {
		List<Entity> ens = NERUtils.nerTag(tagger, text);
		return NERUtils.getSpecificEntityType(ens, EntityType.ORGANIZATION);
	}

	public static void main(String[] args) {
		String modelFile = "./model/ner/crfppNER.m";
		String templateFile = "./model/ner/nerTemplate";
		String data = "D:\\nlp语料\\命名实体识别\\MSRA\\train1_crf.txt";
		CRFppNER crFppNER = new CRFppNER();
		//OOM错误
		String[] option = new String[] {"-f","2"};
		crFppNER.run(templateFile, data, modelFile, option);
		//main2();
	}
	
	public static void main2() {
		String path = "D:\\nlp语料\\命名实体识别\\MSRA\\train1_crf.txt";
		String path2 = "D:\\nlp语料\\命名实体识别\\MSRA\\train1_crf_part1.txt";
		String[] lines  = IOUtils.readTextAndReturnLinesOfArray(path, "utf-8");
		int len = (int) (lines.length * 0.5);
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < len; i++) {
			sb.append(lines[i]+"\n");
		}
		IOUtils.writeTextData2File(sb.toString(), path2, "utf-8");
	}
}
