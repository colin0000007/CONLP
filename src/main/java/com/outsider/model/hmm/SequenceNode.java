package com.outsider.model.hmm;

/**
 * 描述序列中的每一个节点:数据+状态
 * 
 * @author outsider
 * 
 */
public class SequenceNode{
	/**在emission_probability矩阵中当前序列节点的索引位置或者说标记位置**/
	private int nodeIndex;
	/**当前节点的状态
	 * 设置为对象类型，当state没有的时候可以减少内存的占用
	 * **/
	private int state;
	
	public int getNodeIndex() {
		return nodeIndex;
	}
	public void setNodeIndex(int nodeIndex) {
		this.nodeIndex = nodeIndex;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public SequenceNode(int nodeIndex, int state) {
		super();
		this.nodeIndex = nodeIndex;
		this.state = state;
	}
	
	public SequenceNode(int nodeIndex) {
		super();
		this.nodeIndex = nodeIndex;
	}
	public SequenceNode() {}
}