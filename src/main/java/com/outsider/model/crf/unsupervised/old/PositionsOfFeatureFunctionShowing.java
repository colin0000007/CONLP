package com.outsider.model.crf.unsupervised.old;
/**
 * 某个特征函数出现在训练序列的哪些位置
 * 保存这些位置为计算梯度时使用
 * @author outsider
 *	
 */

import java.util.ArrayList;
import java.util.List;

public class PositionsOfFeatureFunctionShowing {
	private List<List<Integer>> positions;
	public PositionsOfFeatureFunctionShowing(int stateNum) {
		positions = new ArrayList<>(stateNum);
		for(int i = 0; i < stateNum; i++) {
			positions.add(new ArrayList<>());
		}
	}
	
	public void addIndex(int index, int state) {
		positions.get(state).add(index);
	}
	
	public boolean isAppearing(int index, int state) {
		if(positions.get(state).contains(index))
			return true;
		return false;
	}
	
	public List<Integer> getPositionsOfFeatureFunction(int state){
		return positions.get(state);
	}
}
