package com.outsider.model.metric;

import java.util.List;

import com.outsider.common.util.IOUtils;
import com.outsider.nlp.nameEntityRecognition.Entity;

/**
 * 命名实体识别评估
 * @author outsider
 *
 */
public class NERTaggerEvaluation {
	private float precision;
	private float recall;
	private float fMeasure;
	private float errorRate;
	public void score(List<Entity> right, List<Entity> predict) {
		int rightCount = 0;
		for(Entity entity : predict) {
			if(right.contains(entity))
				rightCount++;
		}
		this.recall = (float) (rightCount * 1.0 / right.size());
		this.precision = (float) (rightCount * 1.0 / predict.size());
		this.fMeasure = (2 * precision * recall) / (precision + recall);
		this.errorRate = (float) ((predict.size() - rightCount * 1.0) / right.size());
	}
	
	
	
	public float getPrecision() {
		return precision;
	}



	public void setPrecision(float precision) {
		this.precision = precision;
	}



	public float getRecall() {
		return recall;
	}



	public void setRecall(float recall) {
		this.recall = recall;
	}



	public float getfMeasure() {
		return fMeasure;
	}



	public void setfMeasure(float fMeasure) {
		this.fMeasure = fMeasure;
	}



	public float getErrorRate() {
		return errorRate;
	}



	public void setErrorRate(float errorRate) {
		this.errorRate = errorRate;
	}
	
	public void printScore() {
		System.out.println("precision:"+precision+",recall:"+recall+",F-score:"+fMeasure+",errorRate:"+errorRate);
	}


	public static void main(String[] args) {
		// nr人名 ns地名 nt机构名
		String path = "./data/ner/testright1.txt";
		String[] entiys = IOUtils.loadSegmentionCorpus(path, "utf-8", " ");
		System.out.println(entiys[0]);
		System.out.println(entiys[1]);
	}
}
