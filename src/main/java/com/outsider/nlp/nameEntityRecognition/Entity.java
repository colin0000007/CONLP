package com.outsider.nlp.nameEntityRecognition;

public class Entity {
	//实体类别
	private String entityType;
	//实体内容
	private String entity;
	//开始位置
	private int start;
	//结束位置
	private int end;
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
	@Override
	public String toString() {
		return entity + ","+entityType +"("+start+","+end+")";
	}
	
}
