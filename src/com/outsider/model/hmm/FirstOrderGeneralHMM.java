package com.outsider.model.hmm;

import java.io.File;
import java.util.List;
import java.util.Observable;

import com.outsider.common.config.Config;
import com.outsider.common.util.IOUtils;
import com.outsider.common.util.MathUtils;

/**
 * 通用监督学习HMM算法的实现
 * @author outsider
 * @version v1.0
 * @Date 2018.9.6
 *
 */
public class FirstOrderGeneralHMM extends AbstractHMM{
	/**初始状态概率**/
	protected double[] pi;
	/**转移概率**/
	protected double[][] transferProbability1;
	/**发射概率**/
	//TODO 暂时没用稀疏存储矩阵
	protected double[][] emissionProbability;
	/**定义无穷大**/
	public static final double INFINITY = (double) -Math.pow(2, 31);
	/**
	 * 构造函数
	 * @param stateNum 状态值集合大小
	 * @param observationNum 观测值集合大小
	 */
	public FirstOrderGeneralHMM(int stateNum, int observationNum) {
		super(stateNum, observationNum);
		//初始化pi，transferProbability1，emissionProbability
		this.pi = new double[stateNum];
		this.transferProbability1 = new double[stateNum][stateNum];
		this.emissionProbability = new double[stateNum][observationNum];
	}
	
	public FirstOrderGeneralHMM() {
		super();
	}

	/**
	 * 直接构造已经训练好的模型
	 * @param stateNum 状态集合大小
	 * @param observationNum 观测集合大小
	 * @param pi 初始状态概率
	 * @param transferProbability1 转移概率矩阵
	 * @param emissionProbability 发射概率矩阵
	 */
	public FirstOrderGeneralHMM(int stateNum, int observationNum, double[] pi, double[][] transferProbability1, double[][] emissionProbability) {
		super(stateNum, observationNum);
		this.pi = pi;
		this.transferProbability1 = transferProbability1;
		this.emissionProbability = emissionProbability;
	}
	
	/**
	 * 统计求解参数
	 * @param nodes 序列节点
	 */
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
			//状态下观测分布统计
			emissionProbability[node.getState()][node.getNodeIndex()]++;
		}
	}
	/**
	 * 概率取对数
	 */
	protected void logProbability() {
		double piSum  = 0;
		double[] aSum = new double[stateNum];
		double[] bSum = new double[stateNum];
		piSum = MathUtils.sum(pi);
		for(int i = 0; i < stateNum; i++) {
			//piSum += pi[i];
			/*for(int j = 0; j < stateNum; j++) {
				aSum[i] += transferProbability1[i][j];
			}*/
			//取代原来的求和，精简代码
			aSum[i] = MathUtils.sum(transferProbability1[i]);
			/*for(int k = 0; k < observationNum; k++) {
				bSum[i] += emissionProbability[i][k];
			}*/
			bSum[i] = MathUtils.sum(emissionProbability[i]);
		}
		for(int i = 0; i < stateNum; i++) {
			if(pi[i] != 0) {
				pi[i] = (double) (Math.log(pi[i]) - Math.log(piSum));
			} else {
				pi[i] = INFINITY;
			}
			for(int j = 0; j < stateNum; j++) {
				if(transferProbability1[i][j] !=0) {
					transferProbability1[i][j] = (double) (Math.log(transferProbability1[i][j]) - Math.log(aSum[i]));
				} else {
					transferProbability1[i][j] = INFINITY;
				}
			}
			for(int k = 0; k < observationNum; k++) {
				if(emissionProbability[i][k] !=0) {
					emissionProbability[i][k] = (double) (Math.log(emissionProbability[i][k]) - Math.log(bSum[i]));
				} else {
					emissionProbability[i][k] = INFINITY;
				}
			}
		}
	}
	
	/**
	 * 维特比解码
	 * @param O 观测序列,输入的是经过编码处理的，而不是原始数据，
	 * 比如，如果序列是字符串，那么输入必须是一系列的字符的编码而不是字符本身
	 * @return 返回预测结果，
	 */
	public int[] verterbi(int[] O) {
		double[][] deltas = new double[O.length][this.stateNum];
		//保存deltas[t][i]的值是由上一个哪个状态产生的
		int[][] states = new int[O.length][this.stateNum];
		//初始化deltas[0][]
		for(int i = 0;i < this.stateNum; i++) {
			//TODO
			if(O[0]!=-1)
				deltas[0][i] = pi[i] + emissionProbability[i][O[0]];
			else
				deltas[0][i] = pi[i];
		}
		//计算deltas
		for(int t = 1; t < O.length; t++) {
			for(int i = 0; i < this.stateNum; i++) {
				deltas[t][i] = deltas[t-1][0]+transferProbability1[0][i];
				for(int j = 1; j < this.stateNum; j++) {
					double tmp = deltas[t-1][j]+transferProbability1[j][i];
					if (tmp > deltas[t][i]) {
						deltas[t][i] = tmp;
						states[t][i] = j;
					}
				}
				/**
				 * 这里因为输入词的不存在忽略了O[t]，有什么影响还有待考虑
				 */
				if(O[t]!=-1)
					deltas[t][i] += emissionProbability[i][O[t]];
			}
		}
		//回溯找到最优路径
		int[] predict = new int[O.length];
		double max = deltas[O.length-1][0];
		for(int i = 1; i < this.stateNum; i++) {
			if(deltas[O.length-1][i] > max) {
				max = deltas[O.length-1][i];
				predict[O.length-1] = i;				
			}
		}
		for(int i = O.length-2;i >= 0;i-- ) {
			predict[i] = states[i+1][predict[i+1]];
		}
		return predict;
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
		return this;
	}

	@Override
	public void initParameter() {
		this.pi = new double[stateNum];
		this.transferProbability1 = new double[stateNum][stateNum];
		this.emissionProbability = new double[stateNum][observationNum];
	}
}
