package com.outsider.model.metric;

import java.util.List;

import com.outsider.model.hmm.SequenceNode;

public class Metric {
	/**
	 * 准确度评估
	 * @param predict
	 * @param nodes
	 * @return
	 */
	public static float precision(int[] predict, List<SequenceNode> nodes) {
		int count = 0;
		for(int i = 0; i < predict.length; i++) {
			if(predict[i] == nodes.get(i).getState()) {
				count++;
			}
		}
		return (float) (count*1.0 / predict.length);
	}
	
	/**
	 * 准确度评估
	 * @param predictValue
	 * @param trueValue
	 * @return
	 */
	public static float accuracyScore(int[] predictValue, int[] trueValue) {
		int count = 0;
		for(int i = 0; i < predictValue.length; i++) {
			if(predictValue[i] == trueValue[i]) {
				count++;
			}
		}
		return (float) (count*1.0 / predictValue.length);
	}
	
	public static <T> float accuracyScore(T[] predictValue, T[] trueValue) {
		int count = 0;
		for(int i = 0; i < predictValue.length; i++) {
			if(predictValue[i].equals(trueValue[i])) {
				count++;
			}
		}
		return (float) (count*1.0 / predictValue.length);
	}
	
}
