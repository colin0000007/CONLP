package com.outsider.model.crf.unsupervised.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.outsider.common.util.MathUtils;
import com.outsider.model.crf.CRF;
import com.outsider.model.crf.FeatureFunction;
import com.outsider.model.crf.FeatureTemplate;
import com.outsider.model.hmm.SequenceNode;

/**
 * 无监督但是带有先验的CRF
 * 目前测试了第3个版本的梯度计算，即calcGradient3，并没有计算Unigram的梯度
 * 也就是说没有更新相应的参数，依然跑不出什么结果来，准备放弃了
 * 找一个版本看懂。
 * 
 * @author outsider
 */
public abstract class UnsupervisedCRF extends CRF{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//训练数据的序列长度
	private int sequenceLen;
	public UnsupervisedCRF() {
		super();
		reInitParameters();
	}

	public UnsupervisedCRF(int observationNum, int stateNum) {
		super(observationNum, stateNum);
		reInitParameters();
	}

	/**
	 * p(x,y)的统计先验分布
	 * 概率取对数
	 * 行是x，列是y
	 */
	private double[][] priorXYDistribution;
	/**
	 * p(x)的统计先验分布
	 * 概率取对数
	 */
	private double[] priorXDistribution;
	/**
	 * 当前位置下遍历对应特征函数的权重累积
	 * 不取对数
	 * @param x
	 * @param y_i_1
	 * @param y_i
	 * @param i
	 * @return
	 */
	public double computeWeight(int[] x, int y_i_1, int y_i, int i) {
		List<FeatureFunction> funcs = getUnigramFeatureFunction(x, i);
		double w1 = computeUnigramWeight(funcs, y_i);
		double w2 = computeBigramWeight(y_i_1, y_i);
		return w1 + w2;
	}
	
	public double computeLogWeight(int[] x, int y_i_1, int y_i, int i) {
		List<FeatureFunction> funcs = getUnigramFeatureFunction(x, i);
		double w1 = computeUnigramWeight(funcs, y_i);
		double w2 = computeBigramWeight(y_i_1, y_i);
		double w = w1 + w2;
		if(w <= 0) return INFINITY;
		return Math.log(w);
	}
	/**
	 * 前向算法：根据当前特征函数的权重值，计算α
	 * 由于α过大所以取对数
	 * @param nodes
	 * @return
	 */
	public double[][] calcAlpha(int[] x){
		double[][] alpha = new double[sequenceLen][stateNum];
		//alpha初值就等于exp(w*f(y_1,x));,取对数后就只是w*f(y_1,x)
		List<FeatureFunction> funcs = getUnigramFeatureFunction(x, 0);
		for(int i = 0; i < stateNum; i++) {
			alpha[0][i] = computeUnigramLogWeight(funcs, i);
		}
		double[] tmp = new double[stateNum];
		for(int t = 1;t < sequenceLen; t++) {
			for(int i = 0; i < stateNum; i++) {
				//alpha[t][i] = alpha[t-1][i] + computeWeight(x, y_i_1, y_i, i)
				//y_i_1是不确定的
				for(int j = 0; j < stateNum; j++) {
					tmp[j] = (alpha[t-1][j] + computeLogWeight(x, j, i, t));
				}
				alpha[t][i] = MathUtils.logSum(tmp);
			}
		}
		logger.info(Arrays.toString(alpha[sequenceLen-1]));
		return alpha;
	}
	/**
	 * 后向算法：根据当前特征函数的权重值，计算β
	 * 取对数
	 * @param nodes
	 * @return
	 */
	public double[][] calcBeta(int[] x){
		double[][] beta = new double[sequenceLen][stateNum];
		List<FeatureFunction> funcs = getUnigramFeatureFunction(x, sequenceLen-1);
		//beta初值
		for(int i = 0; i < stateNum; i++) {
			beta[sequenceLen-1][i] = computeUnigramLogWeight(funcs, i);
		}
		double[] tmp = new double[stateNum];
		for(int t = sequenceLen-2; t >= 0; t--) {
			for(int i = 0; i < stateNum; i++) {
				//y_i+1是不确定的
				for(int j = 0; j < stateNum; j++) {
					tmp[j] = beta[t+1][j] +computeLogWeight(x, i, j, t);
				}
				beta[t][i] = MathUtils.logSum(tmp);
			}
		}
		return beta;
	}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
	
	public double computeUnigramLogWeight(List<FeatureFunction> funcs, int state) {
		double w = computeUnigramWeight(funcs, state);
		if(w <= 0) return INFINITY;
		return Math.log(w);
	}
	
	@Override
	public void train(String template, int maxOffsetOfTemplate, List<SequenceNode> nodes) {
		//保存特征函数在训练序列中出现的位置，key时特征函数id
		//当前只保存了Unigram函数的出现位置，还没有保存Bigram出现的位置
		//二阶的Bigram暂时不做处理
		Map<String, PositionsOfFeatureFunctionShowing> positionsOfFuncShowing = new HashMap<>();
		//记录不同特征函数出现的次数
		this.sequenceLen = nodes.size();
		//先定义好整体训练框架
		//1. 统计先验分布p(x,y) 并产生特征函数
		TreeMap<String,FeatureFunction> funcs = new TreeMap<>();
		parseStr2FeatureTemplate(template, "\n");
		for(int i = 0; i < nodes.size() -1 ; i++ ) {
			SequenceNode node = nodes.get(i);
			//统计先验分布
			priorXDistribution[nodes.get(i).getNodeIndex()]++;
			priorXYDistribution[node.getNodeIndex()][node.getState()]++;
			//遍历模板产生特征函数
			//只针对Unigram
			generateAndReturnFeatureFunction(nodes, i, funcs, positionsOfFuncShowing);
			//针对Bigram 状态转移
			transferProbabilityWeights[node.getState()][nodes.get(i+1).getState()] = 0.01;
		}
		//处理最后一次循环
		priorXDistribution[nodes.get(sequenceLen-1).getNodeIndex()]++;
		priorXYDistribution[nodes.get(sequenceLen-1).getNodeIndex()][nodes.get(sequenceLen-1).getState()]++;
		logProbaOfprioriDistribution();
		generateAndReturnFeatureFunction(nodes, nodes.size()-1, funcs, positionsOfFuncShowing);
		//对特征函数计数
		
		//将产生好的特征函数创建为DAT
		buildFeatureFunctionDAT(funcs);
		//TODO 只取10万节点试验
		sequenceLen = 100000;
		int[] observations = new int[sequenceLen];
		for(int i = 0; i < sequenceLen; i++) {
			observations[i] = nodes.get(i).getNodeIndex();
		}
		logger.info("开始梯度下降....");
		//2. 执行梯度下降
			//梯度下降过程中涉及到期望计算，前向后向算法，遍历特征函数等等
		//batchGradientDecent(0, 0.00001, 0.001, 100, observations);
		batchGradientDecent2(0, 0.01, 0.001, 20, observations, positionsOfFuncShowing);
	}
	
	protected void generateAndReturnFeatureFunction(List<SequenceNode> nodes, int i, TreeMap<String,FeatureFunction> funcs
			,Map<String, PositionsOfFeatureFunctionShowing> positionsOfFuncShowing) {
		SequenceNode node = nodes.get(i);
		outer:for(FeatureTemplate featureTemplate : featureTemplates) {
			List<int[]> offsetList = featureTemplate.getOffsetList();
			//构建特征函数的观测x
			int[] x = new int[offsetList.size()];
			for(int j = 0; j < offsetList.size(); j++) {
				int[] offset = offsetList.get(j);
				//在开始或者结尾时可能出现offset超出范围的情况
				int row = i+offset[0];
				if(row < 0 || row >= nodes.size()) {
					continue outer;
				}
				SequenceNode offsetNode = nodes.get(row);
				x[j] = offsetNode.getNodeIndex();
			}
			//产生特征函数id，查找是否已经存在以后才new
			String funcId = FeatureFunction.generateFeatureFuncStrId(x, featureTemplate.getTemplateNumber());
			FeatureFunction old = funcs.get(funcId);
			if(old == null ) {
				//注意当前的状态y只是当前行的，与偏移到上一行无关
				FeatureFunction featureFunction = new FeatureFunction(funcId, featureTemplate.getTemplateNumber());
				featureFunction.setX(x);
				double[] weight = new double[stateNum];
				//初始权重为0.001
				weight[nodes.get(i).getState()] = 0.001;
				featureFunction.setWeight(weight);
				funcs.put(funcId, featureFunction);
				//产生PositionsOfFeatureFunctionShowing，保存位置
				PositionsOfFeatureFunctionShowing indexs = new PositionsOfFeatureFunctionShowing(stateNum);
				if(i < 100000)
					indexs.addIndex(i, nodes.get(i).getState());
				positionsOfFuncShowing.put(funcId, indexs);
			} else {
				//计数
				//old.getWeight()[nodes.get(i).getState()]++;
				//保存位置
				PositionsOfFeatureFunctionShowing indexs = positionsOfFuncShowing.get(funcId);
				if(i < 100000)
					indexs.addIndex(i, nodes.get(i).getState());
			}
		}
	}
	
	public void train(List<SequenceNode> nodes) {
		train(getDefaultTemplate(), 2, nodes);
	}
	
	/**
	 * 获取CRF旧的权重参数
	 * @return
	 */
	public List<Double> getCRFeatureFunctionWeights() {
		List<Double> weights = new ArrayList<>();
		for(FeatureFunction featureFunction: featureFunctionTrie.getValues()) {
			for(int i = 0; i < stateNum; i++) {
				if(featureFunction.getWeight()[i] != 0) {
					weights.add(featureFunction.getWeight()[i]);
				}
			}
		}
		for(int i = 0; i < stateNum; i++) {
			for(int j = 0; j < stateNum; j++) {
				weights.add(transferProbabilityWeights[i][j]);
			}
		}
		return weights;
	}
	
	public void updateCRFeatureFunctionWeights(List<Double> weights) {
		int count = 0;
		for(FeatureFunction featureFunction: featureFunctionTrie.getValues()) {
			for(int i = 0; i < stateNum; i++) {
				if(featureFunction.getWeight()[i] != 0) {
					featureFunction.getWeight()[i] = weights.get(count++);
				}
			}
		}
		for(int i = 0; i < stateNum; i++) {
			for(int j = 0; j < stateNum; j++) {
				transferProbabilityWeights[i][j] = weights.get(count++);
			}
		}
		logger.info("updateCRFeatureFunctionWeights:");
		logger.info("count == weights.size:"+(count == weights.size()));
	}
	

	/**
	 * CRF批量梯度下降求解
	 * @param lambda 正则系数
	 * @param alpha 学习率
	 * @param epsilon 精度
	 * @param maxIter 最大迭代次数，传入<=0 的数则认为不设置最大迭代次数
	 * @param observations 观测序列
	 */
	public void batchGradientDecent(double lambda, double alpha, double epsilon, int maxIter,
			int[] observations) {
		int iter = 1;
		//如果传入的最大迭代次数是<=0说明不想设置这个参数，
		//那么将最大迭代次数设置为默认无限大
		if(maxIter <= 0) {
			maxIter = Integer.MAX_VALUE;
		}
		while(iter <= maxIter) {
			//更新参数
			List<Double> oldParameter = getCRFeatureFunctionWeights();
			//1.计算alpha
			logger.info("计算alpha...");
			double[][] crfAlpha = calcAlpha(observations);
			logger.info("计算结束...");
			//2.计算beta
			logger.info("计算beta...");
			double[][] crfBeta = calcBeta(observations);
			logger.info("计算结束...");
			//3.计算梯度
			logger.info("计算梯度...");
			double gradient = calcGradient2(crfAlpha, crfBeta, observations);
			logger.info("计算结束...");
			//4.更新参数
			for(int i = 0; i < oldParameter.size(); i++) {
				double tmp = oldParameter.get(i) - alpha * gradient;
				oldParameter.set(i, tmp);
			}
			updateCRFeatureFunctionWeights(oldParameter);
			logger.info("转移特征参数:");
			for(int i = 0; i < stateNum; i++) {
				logger.info(Arrays.toString(transferProbabilityWeights[i]));
			}
			//之前收敛条件搞错了，收敛直接判断所有梯度小于一个值就认为基本到达最小点
			//判断是否收敛
			logger.info("梯度值:"+gradient);
			if(Math.abs(gradient) <= epsilon) {
				//如果收敛跳出循环，结束训练
				return;
			}
			logger.info("iter:"+iter);
			iter++;
		}
		//达到最大迭代次数还是没有返回参数说明参数还没有求解出来
		System.err.println("达到最大迭代次数,参数求解失败!");
		System.err.println("solve parameters failed!");
	}
	public void updateUnigramWeights(Map<String, double[]> gradietns, double alpha) {
		Set<Entry<String, double[]>> entrys = gradietns.entrySet();
		for(Entry<String, double[]> entry : entrys) {
			FeatureFunction func = featureFunctionTrie.getValue(entry.getKey());
			double[] gradient = entry.getValue();
			double[] weights = func.getWeight();
			for(int i = 0; i < stateNum; i++) {
				if(weights[i] != 0) {
					weights[i] = weights[i] - alpha * gradient[i];
					/*if(gradient[i] == 0) {
						System.out.println("梯度出现了0，是否不该被更新的参数被更新了???");
					}*/
				}
			}
		}
	}
	
	public void batchGradientDecent2(double lambda, double alpha, double epsilon, int maxIter,
			int[] observations, Map<String, PositionsOfFeatureFunctionShowing> positionsOfFuncShowing) {
		int iter = 1;
		//如果传入的最大迭代次数是<=0说明不想设置这个参数，
		//那么将最大迭代次数设置为默认无限大
		if(maxIter <= 0) {
			maxIter = Integer.MAX_VALUE;
		}
		while(iter <= maxIter) {
			//1.计算alpha
			logger.info("计算alpha...");
			double[][] crfAlpha = calcAlpha(observations);
			logger.info("计算结束...");
			//2.计算beta
			logger.info("计算beta...");
			double[][] crfBeta = calcBeta(observations);
			logger.info("计算结束...");
			//3.计算梯度
			logger.info("计算梯度...");
			Map<String, double[]> gradients = calcGradient3(crfAlpha, crfBeta, observations, positionsOfFuncShowing);
			logger.info("计算结束...");
			//4.更新参数
			updateUnigramWeights(gradients, alpha);
			logger.info("转移特征参数:");
			for(int i = 0; i < stateNum; i++) {
				logger.info(Arrays.toString(transferProbabilityWeights[i]));
			}
			//之前收敛条件搞错了，收敛直接判断所有梯度小于一个值就认为基本到达最小点
			//判断是否收敛
			//logger.info("梯度值:"+gradient);
			//判断是否收敛
			Collection<double[]> values = gradients.values();
			Iterator<double[]> iterator = values.iterator();
			boolean isConvergent = true;
			int count = 0;
			outer:while(iterator.hasNext()) {
				double[] cgradient = iterator.next();
				if(count == 0) {
					logger.info("Unigram前4个梯度值:"+Arrays.toString(cgradient));
				}
				for(int i = 0; i < stateNum; i++) {
					//只要有一个梯度是大于epsilon的，那么就没有收敛
					if(cgradient[i] != 0 && Math.abs(cgradient[i]) > epsilon) {
						isConvergent = false;
						break outer;
					}
				}
			}
			//如果收敛就退出
			if(isConvergent) {
				return;
			}
			logger.info("iter:"+iter);
			iter++;
		}
		//达到最大迭代次数还是没有返回参数说明参数还没有求解出来
		System.err.println("达到最大迭代次数,参数求解失败!");
		System.err.println("solve parameters failed!");
	}
	//先验分布概率取对数
	private void logProbaOfprioriDistribution() {
		//无论是x的分布还是x，y的分布求和一定是序列的长度，所以这里不用再求一次
		//x先验分布取对数
		double sum =Math.log( sequenceLen );
		for(int i =0; i < observationNum; i++) {
			if(priorXDistribution[i] == 0) {
				priorXDistribution[i] = INFINITY;
			} else {
				priorXDistribution[i] = Math.log(priorXDistribution[i]) - sum;
			}
		}
		//xy分布先验取对数
		for(int i = 0; i < observationNum; i++) {
			for(int j = 0; j < stateNum; j++) {
				if(priorXYDistribution[i][j] == 0) {
					priorXYDistribution[i][j] = INFINITY;
				} else {
					priorXYDistribution[i][j] = Math.log(priorXYDistribution[i][j]) - sum;
				}
			}
		}
	}
	
	public double calcGradient2(double[][] alpha, double[][] beta, int[] observations) {
		double gradient  = 0;
		//1.计算梯度中的第一部分
		//保存计算Σp(y|x)*f(x,y)的计算结果
		double[] pyGivenXMultiFuncNum = new double[sequenceLen + 1];
		double[] funcExceptionsUnderPXY = new double[sequenceLen];
		double[] tmp = new double[stateNum];
		double[] tmp5 = new double[stateNum];
		double zx = MathUtils.logSum(alpha[sequenceLen - 1]);
		List<FeatureFunction> startfuncs = getUnigramFeatureFunction(observations, 0);
		//初始的i = 1时，yi_1 = -1;
		for(int i = 0; i < stateNum; i++) {
			double logCount = Math.log(countFeatureFunction(startfuncs, i));
			double logWeight = 0;
			
			//梯度第1部分
			tmp[i] = logCount
					+ 0 + computeUnigramLogWeight(startfuncs, i)
					+ beta[0][i] - zx;
			//梯度第2部分
			tmp5[i] = priorXYDistribution[observations[0]][i] + logCount;
		}
		funcExceptionsUnderPXY[0] = MathUtils.logSum(tmp5);
		pyGivenXMultiFuncNum[0] = MathUtils.logSum(tmp);
		//结尾i = T，不存在yT,只有YT_1时合法的
		List<FeatureFunction> endFuncs = getUnigramFeatureFunction(observations, sequenceLen - 1);
		for(int i = 0; i < stateNum; i++) {
			tmp[i] = Math.log(countFeatureFunction(endFuncs, i))
					+ alpha[sequenceLen -1][i] + computeUnigramLogWeight(endFuncs, i)
					+ 0 - zx;
		}
		pyGivenXMultiFuncNum[sequenceLen] = MathUtils.logSum(tmp);
		//计算中间部分
		for(int i = 1; i < sequenceLen; i++) {
			List<FeatureFunction> funcs = getUnigramFeatureFunction(observations, i);
			double[] tmp2 = new double[stateNum];
			for(int yi = 0; yi < stateNum; yi++) {
				//+1是因为转移特征函数，需要取对数
				double[] tmp1 = new double[stateNum];
				double funcNum = Math.log(countFeatureFunction(funcs, yi) + 1);
				//梯度第2部分
				tmp5[yi] = funcNum + priorXYDistribution[observations[i]][yi];
				//梯度第2部分结束
				for(int yi_1 = 0; yi_1 < stateNum; yi_1++) {
					tmp1[yi_1] = funcNum + alpha[i - 1][yi_1] + computeLogWeight(observations, yi_1, yi, i)
					+ beta[i][yi] - zx;
				}
				tmp2[yi] = MathUtils.logSum(tmp1);
			}
			funcExceptionsUnderPXY[i] = MathUtils.logSum(tmp5);
			pyGivenXMultiFuncNum[i] = MathUtils.logSum(tmp2);
		}
		//加上先验概率
		double[] tmp4 = new double[sequenceLen];
		double a = MathUtils.logSum(pyGivenXMultiFuncNum);
		for(int i = 0; i < sequenceLen; i++) {
			tmp4[i] = priorXDistribution[observations[i]] + a;
		}
		double gradietnPart1 = MathUtils.logSum(tmp4);
		gradietnPart1 = Math.exp(gradietnPart1);//取exp复原
		double gradientPart2 = MathUtils.logSum(funcExceptionsUnderPXY);
		gradientPart2 = Math.exp(gradientPart2);
		//2.计算梯度的第2部分
		gradient = gradietnPart1 - gradientPart2;
		return gradient;
	}
	
	/**
	 * 搞错了，每个特征函数的梯度并不一样
	 * 暂时只更新Unigram特征函数的参数测试
	 * @param alpha
	 * @param beta
	 * @param observations
	 * @return
	 */
	public Map<String, double[]> calcGradient3(double[][] alpha, double[][] beta, int[] observations, Map<String, PositionsOfFeatureFunctionShowing> positions) {
		//分2步进行
		//1.对于梯度的第一部分 p(Yi=yi,Yi-1=yi-1|x)是通用的，先对此进行计算
		
		//3.对于梯度分第二部分，如果将p(x,y)当作先验信息就很好计算，直接用到第一部分统计的特征函数在训练序列中
		//出现的个数
		return calcUnigramGradient(alpha, beta, observations, positions);
	}
	public double[][] calcBigramGradient(double[][] alpha, double[][] beta, int[] observations){
		
		return null;
	}
	public Map<String, double[]> calcUnigramGradient(double[][] alpha, double[][] beta, int[] observations, Map<String, PositionsOfFeatureFunctionShowing> positions){
		double zx = MathUtils.logSum(alpha[sequenceLen - 1]);
		Map<String, double[]> gradients = new HashMap<>();
		for(FeatureFunction function : featureFunctionTrie.getValues()) {
			PositionsOfFeatureFunctionShowing indexs = positions.get(function.getStrId());
			double[] gradient = new double[stateNum];
			gradients.put(function.getStrId(), gradient);
			for(int i = 0; i < stateNum; i++) {
				//权重初始不为0，说明存在该特征函数
				if(function.getWeight()[i] != 0) {
					List<Integer> indexss = indexs.getPositionsOfFeatureFunction(i);
					//计算p(x)*p(yi|x)
					//保存梯度第一部分
					double[] tmp = new double[indexss.size()];
					//保存梯度第而部分p(x,y)
					double[] tmp2 = new double[indexss.size()];
					for(int j = 0; j < tmp.length; j++) {
						tmp[j] = (alpha[indexss.get(j)][i] + beta[indexss.get(j)][i] 
								+priorXDistribution[observations[indexss.get(j)]]- zx);
						tmp2[j] = priorXYDistribution[observations[indexss.get(j)]][i];
					}
					//之前取了对数，需要取exp回来
					double part1Exp = Math.exp(MathUtils.logSum(tmp));
					double part2Exp = Math.exp(MathUtils.logSum(tmp2));
					gradient[i] = part1Exp - part2Exp;
				}
			}//-0.004027248159597632
			//System.out.println("gradient:"+Arrays.toString(gradient));
		}
		return gradients;
	}   
	/**
	 * 数一数指定的特征函数在训练集中出现的次数
	 * 这个工作在产生特征函数的时候已经做好了
	 * 不需要再重复做了,这里直接使用生成特征函数的统计
	 * @param observations
	 * @param y
	 */
	/*public Map<String, int[]> countFeatureFunction(int[] observations, int[] y) {
		Map<String, int[]> funcCounts = new HashMap<>();
		for(FeatureFunction featureFunction : featureFunctionTrie.getValues()) {
			int[] count = new int[stateNum];
			double[] weight = featureFunction.getWeight();
			for(int i = 0; i < stateNum; i++) {
				count[i] = (int) weight[i];
			}
			funcCounts.put(featureFunction.getStrId(), count);
		}
		return funcCounts;
	}*/
	
	/**
	 * 计算某个状态下的特征函数个数
	 * 如果权重为0则认为该特征函数不存在
	 * @param funcs
	 * @param y
	 * @return
	 */
	public int countFeatureFunction(List<FeatureFunction> funcs , int y) {
		int count = 0;
		for(FeatureFunction featureFunction : funcs) {
			if(featureFunction.getWeight()[y] != 0) {
				count++;
			}
		}
		return count;
	}
	
	@Override
	public void reInitParameters() {
		transferProbabilityWeights = new double[stateNum][stateNum];
		featureFunctionTrie = null;
		priorXDistribution = new double[observationNum];
		priorXYDistribution = new double[observationNum][stateNum];
	}
}
