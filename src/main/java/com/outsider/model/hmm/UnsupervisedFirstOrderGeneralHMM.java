package com.outsider.model.hmm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.outsider.common.logger.CONLPLogger;
import com.outsider.common.util.MathUtils;
/**
 * 
 * 无监督学习的HMM实现
 * 少量数据建议串行
 * 大量数据，几十万，百万甚至更高的数据强烈建议并行训练,性能是串行的好4倍以上
 * @author outsider
 */
public class UnsupervisedFirstOrderGeneralHMM extends FirstOrderGeneralHMM{
	/**
	 * 训练数据长度
	 */
	private double precision = 1e-7;
	private int sequenceLen;
	public Logger logger = CONLPLogger.getLoggerOfAClass(UnsupervisedFirstOrderGeneralHMM.class);
	public UnsupervisedFirstOrderGeneralHMM() {
		super();
	}
	public UnsupervisedFirstOrderGeneralHMM(int stateNum, int observationNum, double[] pi,
			double[][] transferProbability1, double[][] emissionProbability) {
		super(stateNum, observationNum, pi, transferProbability1, emissionProbability);
	}
	public UnsupervisedFirstOrderGeneralHMM(int stateNum, int observationNum) {
		super(stateNum, observationNum);
	}
	/**
	 * λ是HMM参数的总称
	 */
	@Override
	protected void solve(List<SequenceNode> nodes) {
		baumWelch(nodes, -1, 1e-4);
	}
	/**
	 * 训练方法
	 * @param nodes 训练序列数据
	 * @param maxIter 最大迭代次数
	 * @param precision 精度
	 */
	public void train(List<SequenceNode> nodes, int maxIter, double precision) {
		this.sequenceLen = nodes.size();
		baumWelch(nodes, maxIter, precision);
	}
	
	@Override
	public void train(List<SequenceNode> nodes) {
		this.sequenceLen = nodes.size();
		solve(nodes);
		//不做概率归一化
	}
	/**
	 * 并行训练
	 * @param nodes 训练数据结点list
	 * @param maxIter 最大迭代次数
	 * @param precision 精度
	 */
	public void parallelTrain(List<SequenceNode> nodes, int maxIter, double precision) {
		this.sequenceLen = nodes.size();
		parallelBaumWelch(nodes, maxIter, precision);
	}
	
	/**
	 * baumWelch算法迭代求解
	 * 迭代时存在这样的现象：新参数和上一次的参数差反而会变大，但是到后面这个误差值几乎会收敛
	 * 所以迭代终止的条件有2个：
	 * 1.达到最大迭代次数
	 * 2.参数A，B，pi中的值相比上一次的最大误差小于某个精度值则认为收敛
	 * 3.若1中给的精度值太大，则可能导致无法收敛，所以增加了一个条件，如果当前迭代的误差和上一次迭代的误差小于某个值（这里给定1e-7），
	 * 可以认为收敛了。
	 * @param nodes 观测序列
	 * @param maxIter 最大迭代次数，如果传入<=0的数则默认为Integer.MAX_VALUE,相当于不收敛就不跳出循环
	 * @param precision 参数误差的精度小于precision就认为收敛
	 */
	protected void baumWelch(List<SequenceNode> nodes, int maxIter, double precision) {
		int iter = 0;
		double oldMaxError = 0;
		if(maxIter <= 0) {
			maxIter = Integer.MAX_VALUE;
		}
		//初始化各种参数
		double[][] alpha = new double[sequenceLen][stateNum];
		double[][] beta = new double[sequenceLen][stateNum];
		double[][] gamma  = new double[sequenceLen][stateNum];
		double[][][] ksi = new double[sequenceLen][stateNum][stateNum];
		while(iter < maxIter) {
			logger.info("\niter"+iter+"...");
			long start = System.currentTimeMillis();
			//计算各种参数，为更新模型参数做准备，对应EM中的E步
			calcAlpha(nodes, alpha);
			calcBeta(nodes, beta);
			calcGamma(nodes, alpha, beta, gamma);
			calcKsi(nodes, alpha, beta, ksi);
			//更新参数，对应EM中的M步
			double[][] oldA = generateOldA();
			//double[][] oldB = generateOldB();
			//double[] oldPi = pi.clone();
			updateLambda(nodes, gamma, ksi);
			//double maxError = calcError(oldA, oldPi, oldB);
			double maxError = calcError(oldA, null, null);
			logger.info("max_error:"+maxError);
			if(maxError < precision || (Math.abs(maxError-oldMaxError)) < this.precision) {
				logger.info("参数已收敛....");
				break;
			}
			oldMaxError = maxError;
			iter++;
			long end = System.currentTimeMillis();
			logger.info("本次迭代结束,耗时:"+(end - start)+"毫秒");
		}
		logger.info("最终参数:");
		logger.info("pi:"+Arrays.toString(pi));
		logger.info("A:");
		for(int i = 0; i < transferProbability1.length; i++) {
			logger.info(Arrays.toString(transferProbability1[i]));
		}
	}
	
	/**
	 * BaumWelch并行训练
	 * @param nodes
	 * @param maxIter
	 * @param precision
	 */
	protected void parallelBaumWelch(List<SequenceNode> nodes, int maxIter, double precision) {
		int iter = 0;
		double oldMaxError = 0;
		if(maxIter <= 0) {
			maxIter = Integer.MAX_VALUE;
		}
		//初始化各种参数
		double[][] alpha = new double[sequenceLen][stateNum];
		double[][] beta = new double[sequenceLen][stateNum];
		double[][] gamma  = new double[sequenceLen][stateNum];
		double[][][] ksi = new double[sequenceLen][stateNum][stateNum];
		while(iter < maxIter) {
			logger.info("\niter"+iter+"...");
			long start = System.currentTimeMillis();
			//计算各种参数，为更新模型参数做准备，对应EM中的E步
			//alpha和beta的计算可以并行
			Thread alphathe =  new Thread(new AlphaCalculatorThread(this, nodes, alpha));
			Thread betathe = new Thread(new BetaCalculatorThread(this, nodes, beta));
			alphathe.start();
			betathe.start();
			//主线程等待alpha和beta计算完成才能往下走
			try {
				alphathe.join();
				betathe.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//gamma和ksi的计算可以并行，但是必须等待alpha和beta的计算完成
			Thread gammathe = new Thread(new GammaCalculatorThread(this, nodes, alpha, beta, gamma));
			Thread ksithe = new Thread(new KsiCalculatorThread(this, nodes, alpha, beta, ksi));
			gammathe.start();
			ksithe.start();
			try {
				gammathe.join();
				ksithe.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//更新参数，对应EM中的M步
			double[][] oldA = generateOldA();
			//double[][] oldB = generateOldB();
			//double[] oldPi = pi.clone();
			parallelUpdateLambda(nodes, gamma, ksi);
			//double maxError = calcError(oldA, oldPi, oldB);
			double maxError = calcError(oldA, null, null);
			logger.info("max_error:"+maxError);
			if(maxError < precision || (Math.abs(maxError-oldMaxError)) < this.precision) {
				logger.info("参数已收敛....");
				break;
			}
			oldMaxError = maxError;
			iter++;
			long end = System.currentTimeMillis();
			logger.info("本次迭代结束,耗时:"+(end - start)+"毫秒");
		}
		logger.info("最终参数:");
		logger.info("pi:"+Arrays.toString(pi));
		logger.info("A:");
		for(int i = 0; i < transferProbability1.length; i++) {
			logger.info(Arrays.toString(transferProbability1[i]));
		}
	}
	/**
	 * 保存旧的参数A
	 * @return
	 */
	protected double[][] generateOldA() {
		double[][] oldA = new double[stateNum][stateNum];
		for(int i = 0; i < stateNum; i++) {
			for(int j = 0; j < stateNum; j++) {
				oldA[i][j] = transferProbability1[i][j];
			}
		}
		return oldA;
	}
	/**
	 * 保存旧的参数B
	 * @return
	 */
	protected double[][] generateOldB() {
		double[][] oldB = new double[stateNum][observationNum];
		for(int i = 0; i < stateNum; i++) {
			for(int j = 0; j < observationNum; j++) {
				oldB[i][j] = emissionProbability[i][j];
			}
		}
		return oldB;
	}
	/**
	 * 暂时只计算参数A的误差
	 * 发现计算B和pi会发现参数误差越来越大的现象，基本不能收敛
	 * @param old
	 * @return
	 */
	protected double calcError(double[][] oldA, double[] oldPi, double[][] oldB) {
		double maxError = 0;
		for(int i =0 ; i < stateNum; i++) {
			/*double tmp1 = Math.abs(pi[i] - oldPi[i]);
			maxError = tmp1 > maxError ? tmp1 : maxError;*/
			for(int j =0; j < stateNum; j++) {
				double tmp = Math.abs(oldA[i][j] - transferProbability1[i][j]);
				maxError = tmp > maxError ? tmp : maxError;
			}
			/*for(int k =0; k < observationNum; k++) {
				double tmp2 = Math.abs(emissionProbability[i][k] - oldB[i][k]);
				maxError = tmp2 > maxError ? tmp2 : maxError;
			}*/
		}
		return maxError;
	}
	/**
	 * 概率初始化为0
	 */
	@Override
	public void reInitParameters() {
		//初始概率随机初始化
		super.reInitParameters();
		//概率初始化为0
		for(int i = 0; i < stateNum; i++) {
			pi[i] = INFINITY;
			for(int j = 0; j < stateNum; j++) {
				transferProbability1[i][j] = INFINITY;
			}
			for(int k = 0; k < observationNum; k++) {
				emissionProbability[i][k] = INFINITY;
			}
		}
	}
	/**
	 * 随机初始化参数PI
	 */
	public void randomInitPi() {
		for(int i = 0; i < stateNum; i++) {
			pi[i] = Math.random() * 100;
		}
		//log归一化
		double sum = Math.log(MathUtils.sum(pi));
		for(int i =0; i < stateNum; i++) {
			if(pi[i] == 0) {
				pi[i] = INFINITY;
				continue;
			}
			pi[i] = Math.log(pi[i]) - sum;
		}
	}
	/**
	 * 随机初始化参数A
	 */
	public void randomInitA() {
		for(int i = 0; i < stateNum; i++) {
			for(int j = 0; j < stateNum; j++) {
				transferProbability1[i][j] = Math.random()*100;;
			}
			double sum = Math.log(MathUtils.sum(transferProbability1[i]));
			for(int k = 0; k < stateNum; k++) {
				if(transferProbability1[i][k] == 0) {
					transferProbability1[i][k] = INFINITY;
					continue;
				}
				transferProbability1[i][k]  = Math.log(transferProbability1[i][k]) - sum;
			}
		}
	}
	/**
	 * 随机初始化参数B
	 */
	public void randomInitB() {
		for(int i = 0; i < stateNum; i++) {
			for(int j = 0; j < observationNum; j++) {
				emissionProbability[i][j] = Math.random()*100;;
			}
			double sum = Math.log(MathUtils.sum(emissionProbability[i]));
			for(int k = 0; k < observationNum; k++) {
				if(emissionProbability[i][k] == 0) {
					emissionProbability[i][k] = INFINITY;
					continue;
				}
				emissionProbability[i][k]  = Math.log(emissionProbability[i][k]) - sum;
			}
		}
	}
	
	/**
	 * 随机初始化所有参数
	 */
	public void randomInitAllParameters() {
		randomInitA();
		randomInitB();
		randomInitPi();
	}
	
	/**
	 * 前向算法，根据当前参数λ计算α
	 * α是一个序列长度*状态长度的矩阵
	 * 已检测，应该没有问题
	 */
	protected void calcAlpha(List<SequenceNode> nodes, double[][] alpha) {
		logger.info("计算alpha...");
		long start = System.currentTimeMillis();
		//double[][] alpha = new double[sequenceLen][stateNum];
		//alpha t=0初始值
		for(int i = 0; i < stateNum; i++) {
			alpha[0][i] = pi[i] + emissionProbability[i][nodes.get(0).getNodeIndex()];
		}
		double[] logProbaArr = new double[stateNum];
		for(int t = 1; t < sequenceLen; t++) {
			for(int i = 0; i < stateNum; i++) {
				for(int j = 0; j < stateNum; j++) {
					logProbaArr[j]	= (alpha[t -1][j] + transferProbability1[j][i]);
				}
				alpha[t][i] = logSum(logProbaArr) + emissionProbability[i][nodes.get(t).getNodeIndex()];
			}
		}
		long end = System.currentTimeMillis();
		logger.info("计算结束...耗时:"+ (end - start) +"毫秒");
		//return alpha;
	}
	/**
	 * 后向算法，根据当前参数λ计算β
	 * 
	 * @param nodes
	 */
	protected void calcBeta(List<SequenceNode> nodes, double[][] beta) {
		logger.info("计算beta...");
		long start = System.currentTimeMillis();
		//double[][] beta = new double[sequenceLen][stateNum];
		//初始概率beta[T][i] = 1
		for(int i = 0; i < stateNum; i++) {
			beta[sequenceLen-1][i] = 1;
		}
		double[] logProbaArr = new double[stateNum];
		for(int t = sequenceLen -2; t >= 0; t--) {
			for(int i = 0; i < stateNum; i++) {
				for(int j = 0; j < stateNum; j++) {
					logProbaArr[j] = transferProbability1[i][j] + 
							emissionProbability[j][nodes.get(t+1).getNodeIndex()] +
							beta[t + 1][j];
				}
				beta[t][i] = logSum(logProbaArr);
			}
		}
		long end = System.currentTimeMillis();
		logger.info("计算结束...耗时:"+ (end - start) +"毫秒");
		//return beta;
	}
	
	/**
	 * 根据当前参数λ计算ξ
	 * @param nodes 观测结点
	 * @param alpha 前向概率
	 * @param beta 后向概率
	 */
	protected void calcKsi(List<SequenceNode> nodes, double[][] alpha, double[][] beta, double[][][] ksi) {
		logger.info("计算ksi...");
		long start = System.currentTimeMillis();
		//double[][][] ksi = new double[sequenceLen][stateNum][stateNum];
		double[] logProbaArr = new double[stateNum * stateNum];
		for(int t = 0; t < sequenceLen -1; t++) {
			int k = 0;
			for(int i = 0; i < stateNum; i++) {
				for(int j = 0; j < stateNum; j++) {
					ksi[t][i][j] = alpha[t][i] + transferProbability1[i][j] +
							emissionProbability[j][nodes.get(t+1).getNodeIndex()]+beta[t+1][j];
					logProbaArr[k++] = ksi[t][i][j];
				}
			}
			double logSum = logSum(logProbaArr);//分母
			for(int i = 0; i < stateNum; i++) {
				for(int j = 0; j < stateNum; j++) {
					ksi[t][i][j] -= logSum;//分子除分母
				}
			}
		}
		long end = System.currentTimeMillis();
		logger.info("计算结束...耗时:"+ (end - start) +"毫秒");
		//return ksi;
	}
	
	/**
	 * 根据当前参数λ，计算γ
	 * @param nodes
	 */
	protected void calcGamma(List<SequenceNode> nodes, double[][] alpha, double[][] beta, double[][] gamma) {
		logger.info("计算gamma...");
		long start = System.currentTimeMillis();
		//double[][] gamma  = new double[sequenceLen][stateNum];
		for(int t = 0; t < sequenceLen; t++) {
			//分母需要求LogSum
			for(int i = 0; i < stateNum; i++) {
				gamma[t][i] = alpha[t][i] + beta[t][i];
			}
			double logSum = logSum(gamma[t]);//分母部分
			for(int j = 0; j < stateNum; j++) {
				gamma[t][j] = gamma[t][j] - logSum;
			}
		}
		long end = System.currentTimeMillis();
		logger.info("计算结束...耗时:"+ (end - start) +"毫秒");
		//return gamma;
	}
	
	/**
	 * 更新参数
	 */
	protected void updateLambda(List<SequenceNode> nodes ,double[][] gamma, double[][][] ksi) {
		//顺序可以颠倒
		updatePi(gamma);
		updateA(ksi, gamma);
		updateB(nodes, gamma);
	}
	
	/**
	 * 更新参数pi
	 * @param gamma
	 */
	public void updatePi(double[][] gamma) {
		//更新HMM中的参数pi
		for(int i = 0; i < stateNum; i++) {
			pi[i] = gamma[0][i];
		}
	}
	/**
	 * 更新参数A
	 * @param ksi
	 * @param gamma
	 */
	protected void updateA(double[][][] ksi, double[][] gamma) {
		logger.info("更新参数转移概率A...");
		////由于在更新A都要用到对不同状态的前T-1的gamma值求和，所以这里先算
		double[] gammaSum = new double[stateNum];
		double[] tmp = new double[sequenceLen -1];
		for(int i = 0; i < stateNum; i++) {
			for(int t = 0; t < sequenceLen -1; t++) {
				tmp[t] = gamma[t][i];
			}
			gammaSum[i]  = logSum(tmp);
		}
		long start1 = System.currentTimeMillis();
		//更新HMM中的参数A
		double[] ksiLogProbArr = new double[sequenceLen - 1];
		for(int i = 0; i < stateNum; i++) {
			for(int j = 0; j < stateNum; j++) {
				for(int t = 0; t < sequenceLen -1; t++) {
					ksiLogProbArr[t] = ksi[t][i][j];
				}
				transferProbability1[i][j] = logSum(ksiLogProbArr) - gammaSum[i];
			}
		}
		long end1 = System.currentTimeMillis();
		logger.info("更新完毕...耗时:"+(end1 - start1)+"毫秒");
	}
	/**
	 * 更新参数B
	 * @param nodes
	 * @param gamma
	 */
	protected void updateB(List<SequenceNode> nodes, double[][] gamma) {
		//下面需要用到gamma求和为了减少重复计算，这里直接先计算
		//由于在更新B时都要用到对不同状态的所有gamma值求和，所以这里先算
		double[] gammaSum2 = new double[stateNum];
		double[] tmp2 = new double[sequenceLen];
		for(int i = 0; i < stateNum; i++) {
			for(int t = 0; t < sequenceLen; t++) {
				tmp2[t] = gamma[t][i];
			}
			gammaSum2[i]  = logSum(tmp2);
		}
		logger.info("更新状态下分布概率B...");
		long start2 = System.currentTimeMillis();
		ArrayList<Double> valid = new ArrayList<Double>();
		for(int i = 0; i < stateNum; i++) {
			for(int k = 0; k < observationNum; k++) {
				valid.clear();//由于这里没有初始化造成了计算出错的问题
				for(int t = 0; t < sequenceLen; t++) {
					if(nodes.get(t).getNodeIndex() == k) {
						valid.add(gamma[t][i]);
					}
				}
				//B[i][k]，i状态下k的分布为概率0，
				if(valid.size() == 0) {
					emissionProbability[i][k] = INFINITY;
					continue;
				}
				//对分子求logSum
				double[] validArr = new double[valid.size()];
				for(int q = 0; q < valid.size(); q++) {
					validArr[q] = valid.get(q);
				}
				double validSum = logSum(validArr);
				//分母的logSum已经在上面做了
				emissionProbability[i][k] = validSum - gammaSum2[i];
			}
		}
		long end2 = System.currentTimeMillis();
		logger.info("更新完毕...耗时:"+(end2 - start2)+"毫秒");
	}
	
	/**
	 * 参数B的更新时最耗时的，可以采用并行的方式
	 * @param nodes
	 * @param gamma
	 */
	protected void parallelUpdateB(List<SequenceNode> nodes, double[][] gamma){
		//下面需要用到gamma求和为了减少重复计算，这里直接先计算
		//由于在更新B时都要用到对不同状态的所有gamma值求和，所以这里先算
		double[] gammaSum2 = new double[stateNum];
		double[] tmp2 = new double[sequenceLen];
		for(int i = 0; i < stateNum; i++) {
			for(int t = 0; t < sequenceLen; t++) {
				tmp2[t] = gamma[t][i];
			}
			gammaSum2[i]  = logSum(tmp2);
		}
		logger.info("更新状态下分布概率B...");
		long start2 = System.currentTimeMillis();
		List<Thread> threads = new ArrayList<>();
		for(int i = 0; i < stateNum; i++) {
			Thread thread = new Thread(new BUpdaterThreadInDifferentState(this, nodes, gamma, i, gammaSum2));
			//并行
			thread.start();
			threads.add(thread);
		}
		for(int i = 0; i < stateNum; i++) {
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long end2 = System.currentTimeMillis();
		logger.info("更新完毕...耗时:"+(end2 - start2)+"毫秒");
	}
	/**
	 * parallelUpdateB并行计算部分
	 * @param nodes
	 * @param gamma
	 * @param i
	 * @param gammaSum2
	 */
	protected void updateBinSpecificState(List<SequenceNode> nodes, double[][] gamma, int i, double[] gammaSum2) {
		List<Double> valid = new ArrayList<>();
		for(int k = 0; k < observationNum; k++) {
			valid.clear();//由于这里没有初始化造成了计算出错的问题
			for(int t = 0; t < sequenceLen; t++) {
				if(nodes.get(t).getNodeIndex() == k) {
					valid.add(gamma[t][i]);
				}
			}
			//B[i][k]，i状态下k的分布为概率0，
			if(valid.size() == 0) {
				emissionProbability[i][k] = INFINITY;
				continue;
			}
			//对分子求logSum
			double[] validArr = new double[valid.size()];
			for(int q = 0; q < valid.size(); q++) {
				validArr[q] = valid.get(q);
			}
			double validSum = logSum(validArr);
			//分母的logSum已经在上面做了
			emissionProbability[i][k] = validSum - gammaSum2[i];
		}
	}
	/**
	 * 并行更新参数HMM参数
	 * @param nodes
	 * @param gamma
	 * @param ksi
	 */
	protected void parallelUpdateLambda(List<SequenceNode> nodes ,double[][] gamma, double[][][] ksi) {
		updatePi(gamma);
		Thread thread = new Thread(new AUpdaterThread(this, gamma, ksi));
		thread.start();
		//updateA(ksi, gamma);
		parallelUpdateB(nodes, gamma);
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * logSum计算技巧
	 * @param tmp
	 * @return
	 */
	public double logSum(double[] logProbaArr) {
		if(logProbaArr.length == 0) {
			return INFINITY;
		}
		double max = MathUtils.max(logProbaArr);
		double result = 0;
		for(int i = 0; i < logProbaArr.length; i++) {
			result += Math.exp(logProbaArr[i] - max);
		}
		return max + Math.log(result);
	}
	/**
	 * 设置先验概率pi
	 * 必须传入取对数后的概率
	 * @param pi
	 */
	public void setPriorPi(double[] pi){
		this.pi = pi;
	}
	/**
	 * 设置先验转移概率A
	 * 必须传入取对数的概率
	 * @param trtransferProbability1
	 */
	public void setPriorTransferProbability1(double[][] trtransferProbability1){
		this.transferProbability1 = trtransferProbability1;
	}
	/**
	 * 设置先验状态下的观测分布概率，B
	 * 必须传入取对数的概率
	 * @param emissionProbability
	 */
	public void setPriorEmissionProbability(double[][] emissionProbability) {
		this.emissionProbability = emissionProbability;
	}
}		

