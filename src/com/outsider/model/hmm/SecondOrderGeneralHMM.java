package com.outsider.model.hmm;

import java.io.File;
import java.util.List;

import com.outsider.common.config.Config;
import com.outsider.common.util.IOUtils;
import com.outsider.common.util.MathUtils;

public class SecondOrderGeneralHMM extends FirstOrderGeneralHMM{
	private double[][][] transferProbability2;
	public SecondOrderGeneralHMM(int stateNum, int observationNum) {
		super(stateNum, observationNum);
		this.transferProbability2 = new double[stateNum][stateNum][stateNum];
	}
	
	public SecondOrderGeneralHMM(int stateNum, int observationNum, double[] pi, double[][] transferProbability1,
			double[][] emissionProbability) {
		super(stateNum, observationNum, pi, transferProbability1, emissionProbability);
	}
	
	public SecondOrderGeneralHMM() {
		super();
	}

	@Override
	public void train(List<SequenceNode> sequenceNodes, Object... otherParameters) {
		//二阶HMM需要用到一阶的参数，需要训练出来
		super.solve(sequenceNodes);//如果这里调用父类的train将会导致重复的概率对数化
		//统计二阶HMM的转移概率矩阵参数
		for(int i = 2; i < sequenceNodes.size();i++) {
			transferProbability2[sequenceNodes.get(i-2).getState()]
					[sequenceNodes.get(i-1).getState()][sequenceNodes.get(i).getState()]++;
			
		}
		logProbability();
	}

	/**
	 * 二阶维特比算法
	 */
	@Override
	public int[] verterbi(int[] O) {
		//dp计算δ
		double[][][] deltas = new double[O.length-1][stateNum][stateNum];
		//保存当前状态的最优值由上一个哪个状态产生
		int[][][] states = new int[O.length-1][stateNum][stateNum];
		////前t=2时δ的计算不能使用通用的递推式，单独计算delta_t2,deltas[0][i][j]代表delta_t2
		for(int i = 0; i < stateNum; i++) {
			for(int j = 0;j < stateNum; j++) {
				deltas[0][i][j] = pi[i]+emissionProbability[i][O[0]]
						+ transferProbability1[i][j] + emissionProbability[j][O[1]];
			}
		}
		//计算剩余的delta_t , t >= 3,下面的初始t=1就代表真正的t=3
		for(int t = 1; t < O.length-1; t++) {
			for(int j = 0; j < stateNum; j++) {
				for(int k = 0; k < stateNum;k++) {
					deltas[t][j][k] = deltas[t-1][0][j]+transferProbability2[0][j][k];
					for(int i = 1; i < stateNum; i++) {
						double tmp = deltas[t-1][i][j]+transferProbability2[i][j][k];
						if(tmp > deltas[t][j][k]) {
							deltas[t][j][k] = tmp;
							states[t][j][k] = i;
						}
					}
					//需要特别注意下面的O[t+1]中的索引不是t，因为这里的t和实际的t有一个1的偏移量
					//之前的bug就出在这里，导致效果奇差
					deltas[t][j][k] += emissionProbability[k][O[t+1]]; 
				}
			}
		}
		//先找出最优路径
		int[] predict = new int[O.length];
		double max = deltas[O.length-2][0][0];
		for(int i = 0; i < stateNum; i++) {
			for(int j = 0; j < stateNum;j++) {
				if(deltas[O.length - 2][i][j] > max) {
					max = deltas[O.length - 2][i][j];
					predict[O.length-2] = i;
					predict[O.length-1] = j;
				}
			}
		}
		//回溯
		for(int i = O.length - 3; i >= 0;i--) {
			predict[i] = states[i+1][predict[i+1]][predict[i+2]];
		}
		return predict;
	}
	

	@Override
	protected void logProbability() {
		super.logProbability();
		//需要对新的转移概率矩阵进行归一化
		//求和
		double[][] sum = new double[this.stateNum][this.stateNum];
		for(int i = 0; i < this.stateNum;i++) {
			for(int j = 0; j < this.stateNum;j++) {
				/*for(int k = 0; k < this.stateNum;k++) {
					sum[i][j] += transferProbability2[i][j][k];
				}*/
				//精简代码，取代原来的循环求和
				sum[i][j] = MathUtils.sum(transferProbability2[i][j]);
			}
		}
		//取对数
		for(int i = 0; i < this.stateNum;i++) {
			for(int j = 0; j < this.stateNum;j++) {
				for(int k = 0; k < this.stateNum;k++) {
					if(transferProbability2[i][j][k] == 0) {
						transferProbability2[i][j][k] = INFINITY;
					} else {
						transferProbability2[i][j][k] = Math.log(transferProbability2[i][j][k]) - Math.log(sum[i][j]);
					}
				}
			}
		}
	}
	
	@Override
	public void saveModel(String directory) {
		if(directory == null || directory.trim().equals("")) {
			directory = Config.MODEL_BASE_PATH+this.getClass().getSimpleName()+"/";
			File file = new File(directory);
			if(!file.exists()) {
				file.mkdir();
			}
		}
		IOUtils.writeObject2File(directory+"pi", pi);
		IOUtils.writeObject2File(directory+"transferProbability1", transferProbability1);
		IOUtils.writeObject2File(directory+"emissionProbability", emissionProbability);
		IOUtils.writeObject2File(directory+"stateNum", stateNum);
		IOUtils.writeObject2File(directory+"observationNum", observationNum);
		IOUtils.writeObject2File(directory+"transferProbability2", transferProbability2);
	}
	
	@Override
	public Object loadModel(String directory) {
		if(directory == null || directory.trim().equals("")) {
			directory = Config.MODEL_BASE_PATH+this.getClass().getSimpleName()+"/";
		}
		pi = (double[]) IOUtils.readObject(directory+"pi");
		transferProbability1 = (double[][]) IOUtils.readObject(directory+"transferProbability1");
		emissionProbability = (double[][]) IOUtils.readObject(directory+"emissionProbability");
		stateNum = (int) IOUtils.readObject(directory+"stateNum");
		observationNum = (int) IOUtils.readObject(directory+"observationNum");
		transferProbability2 = (double[][][]) IOUtils.readObject(directory+"transferProbability2");
		return this;
	}
	
	@Override
	public void initParameter() {
		super.initParameter();
		this.transferProbability2 = new double[stateNum][stateNum][stateNum];
	}
}
