package com.outsider.model.evaluation;

import java.util.List;

import com.outsider.model.hmm.SequenceNode;

public class SegmentionEvaluation {
	
	public static float precision2(int[] predict, List<SequenceNode> nodes) {
		int count = 0;
		for(int i = 0; i < predict.length; i++) {
			if(predict[i] == nodes.get(i).getState()) {
				count++;
			}
		}
		return (float) (count*1.0 / predict.length);
	}
	
	public static float precision(int[] predict, int[] trueState) {
		int count = 0;
		for(int i = 0; i < predict.length; i++) {
			if(predict[i] == trueState[i]) {
				count++;
			}
		}
		return (float) (count*1.0 / predict.length);
	}
	
	
}
