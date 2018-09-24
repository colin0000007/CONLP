package com.outsider.nlp.postagger;

import java.util.List;

import com.outsider.model.hmm.FirstOrderGeneralHMM;
import com.outsider.model.hmm.SequenceNode;

public class FirstOrderHMMPOSTagger extends FirstOrderGeneralHMM implements POSTagger{

	public FirstOrderHMMPOSTagger() {
		super();
	}

	public FirstOrderHMMPOSTagger(int stateNum, int observationNum, double[] pi, double[][] transfer_probability1,
			double[][] emission_probability) {
		super(stateNum, observationNum, pi, transfer_probability1, emission_probability);
	}

	public FirstOrderHMMPOSTagger(int stateNum, int observationNum) {
		super(stateNum, observationNum);
	}
	
	@Override
	public int[] predict(int[] O, Object... otherParameters) {
		/**
		 * 获取接口POSTagger中传来的待预测的词序列
		 */
		String[] words = (String[]) otherParameters;
		//原始预测
		int[] rawPredict = super.predict(O, otherParameters);
		/**
		 * 使用正则处理，处理原则：
		 * 若词中含有数字，那么标注为m
		 * 若词中含有英文或其他外文(其他外文暂时不考虑)，标注为x
		 * 暂时不考虑：
		 * 可以后续添加URL等的标注
		 */
		String numberReg = "[0-9]+";//没有考虑全角的数字
		String otherLanguageReg = "[a-zA-Z]+";
		for(int i = 0; i < words.length; i++) {
			if(words[i].matches(numberReg)) {
				rawPredict[i] = natureName2Int.get("m");
			} else if(words[i].matches(otherLanguageReg)) {
				rawPredict[i] = natureName2Int.get("x");
			}
		}
		return rawPredict;
	}
	
	@Override
	protected void solve(List<SequenceNode> nodes) {
		//遍历序列开始训练
		pi[nodes.get(0).getState()]++;
		emissionProbability[nodes.get(0).getState()][nodes.get(0).getNodeIndex()]++;
		for(int i = 1;i < nodes.size(); i++) {
			SequenceNode node = nodes.get(i);
			//状态统计
			pi[node.getState()]++;
			//状态转移统计
			transferProbability1[nodes.get(i-1).getState()][node.getState()]++;
			//需要处理词典中没有的词的情况，也就是nodeIndex=-1
			if(node.getNodeIndex() != -1) {
				//状态下观测分布统计
				emissionProbability[node.getState()][node.getNodeIndex()]++;
			}
		}
	}
}
