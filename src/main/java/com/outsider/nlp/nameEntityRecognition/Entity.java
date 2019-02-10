package com.outsider.nlp.nameEntityRecognition;

public class Entity {
	//实体类别
	private char entityType;
	//实体内容
	private String entity;
	//开始位置
	private int start;
	//结束位置
	private int end;
	public char getEntityType() {
		return entityType;
	}
	public void setEntityType(char entityType) {
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
	
	@Override
	public boolean equals(Object obj) {
		Entity e2 = (Entity) obj;
		if(this.entity.equals(e2.entity) && this.start == e2.start && this.end == e2.end
				&& this.entityType == e2.entityType) {
			return true;
		}
		return false;
	}
	
}
