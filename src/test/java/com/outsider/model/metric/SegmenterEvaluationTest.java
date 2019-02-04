package com.outsider.model.metric;

public class SegmenterEvaluationTest {
	public static void main(String[] args) {
		String[] right = new String[] {"计算机","总是","出问题"};
		String[] predict = new String[] {"计算机","总","是","出问题"};
		SegmenterEvaluation evaluation = new SegmenterEvaluation();
		evaluation.score(right, predict);
		evaluation.printScore();
	}
}
