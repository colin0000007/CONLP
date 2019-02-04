package com.outsider.model.metric;

import java.util.ArrayList;
import java.util.List;

/**
 * 分词评估
 * @author outsider
 * 比如 
 * 	黄金标准:计算机 总是 有问题
 *	分词结果: 计算机 总 是 有问题
 *	对黄金标准中和分词器中的的每一个词做标注
 *	黄金标准:(0,2),(3,4),(5,7)
 *	分词器:(0,2),(3),(4),(5,7)
 *	对比发现分词器中标注正确的由2个，而标注错误的可以认为是1个，也可以认为是2个
 *	在下面的评估算法中认为如果预测标注错误的将一个词标记成n个，那么错误标注也是n个，而不是1个
 *	如果预测结果错误的将多个词粘在一起，那么标注错错误认为是1个
 *
 */
public class SegmenterEvaluation {
	/**
	 * 召回率：查全率，越大越好
	 */
	private double recallScore;
	/**
	 * 精度：准确率，越大越好
	 */
	private double precisionScore;
	/**
	 * f得分：综合评估，越大越好
	 */
	private double fMeasureScore;
	/**
	 * 错误率：越小越好
	 */
	private double errorRate;
	public void score(String[] right, String[] predict) {
		int rightCount = rightCount(right, predict);
		this.recallScore = rightCount*1.0 / right.length;
		this.precisionScore = rightCount*1.0 / predict.length;
		this.fMeasureScore = (2*precisionScore*recallScore) / (precisionScore + recallScore);
		this.errorRate = (predict.length -rightCount*1.0) / right.length;
	}
	
	public void printScore() {
		System.out.println("召回率:"+this.recallScore+",精准率:"+this.precisionScore+",F值:"+this.fMeasureScore+",错误率:"+this.errorRate);
	}
	
	/**
	 * 统计分词结果中正确的个数
	 * @param right 正确的分词结果
	 * @param predict 预测的分词结果
	 * @return
	 */
	public int rightCount(String[] right, String[] predict) {
		List<WordNode> rightNodes = buildNodes(right);
		List<WordNode> predictNodes = buildNodes(predict);
		//统计标注正确的个数
		int count = 0;
		for(int i = 0; i < predictNodes.size(); i++) {
			if(rightNodes.contains(predictNodes.get(i))) {
				count++;
			}
		}
		return count;
	}
	
	public List<WordNode> buildNodes(String[] words){
		List<WordNode> nodes = new ArrayList<>();
		int last = 0;
		for(int i = 0; i < words.length; i++) {
			WordNode node = new WordNode(last, last + words[i].length() - 1);
			nodes.add(node);
			last = node.end + 1;
			
		}
		return nodes;
	}
	
	
	/**
	 * 描述一个词在一个分词结果集中的位置
	 * @author outsider
	 *
	 */
	public static class WordNode{
		//开始位置和结尾位置都包括
		public int start;//词的开始位置
		public int end;//词的结尾位置
		
		public WordNode(int start, int end) {
			super();
			this.start = start;
			this.end = end;
		}

		@Override
		public boolean equals(Object obj) {
			WordNode node = (WordNode) obj;
			if(this.start == node.start && this.end == node.end)
				return true;
			return false;
		}
		
		@Override
		public String toString() {
			return "("+start+","+end+")";
		}
	}


	public double getRecallScore() {
		return recallScore;
	}
	public double getPrecisionScore() {
		return precisionScore;
	}
	public double getfMeasureScore() {
		return fMeasureScore;
	}
	public double getErrorRate() {
		return errorRate;
	}
	
}
