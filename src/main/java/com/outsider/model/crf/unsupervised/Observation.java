package com.outsider.model.crf.unsupervised;

public class Observation {
	private int[] x;
	//特征函数的字符id，方便在字典树中存储
	private String strId;
	private short featureTemplateNum;
	public int[] getX() {
		return x;
	}
	public void setX(int[] x) {
		this.x = x;
	}
	public String getStrId() {
		return strId;
	}
	public void setStrId(String strId) {
		this.strId = strId;
	}
	public short getFeatureTemplateNum() {
		return featureTemplateNum;
	}
	public void setFeatureTemplateNum(short featureTemplateNum) {
		this.featureTemplateNum = featureTemplateNum;
	}
	
}
