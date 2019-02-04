package com.outsider.model.crf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.outsider.common.util.MathUtils;
import com.outsider.model.hmm.SequenceNode;

/**
 * 监督学习的CRF
 * @author outsider
 *
 */
public abstract class SupervisedCRF extends CRF{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SupervisedCRF() {
		super();
	}

	public SupervisedCRF(int observationNum, int stateNum) {
		super(observationNum, stateNum);
	}

	/**
	 * 无监督学习需要归一化概率
	 */
	public void logProb() {
		//转移概率归一化
		for(int i = 0; i < stateNum; i++) {
			double logSum = MathUtils.sum(transferProbabilityWeights[i]);
			logSum = Math.log(logSum);
			for(int j = 0; j  < stateNum; j++) {
				if(transferProbabilityWeights[i][j] != 0) {
					transferProbabilityWeights[i][j] = 
					Math.log(transferProbabilityWeights[i][j]) - logSum;
				} else {
					transferProbabilityWeights[i][j]  = INFINITY;
				}
			}
		}
		List<FeatureFunction> functions = featureFunctionTrie.getValues();
		//遍历特征函数，找出哪些是同一个特征模板产生的特征函数，归一化一个状态下的观测概率
		int funcAmount = 0;
		Map<Short, List<FeatureFunction>> funcsGroupByTemple = new HashMap<>();
		//将特征函数分为不同模板下的特征函数
		for(FeatureTemplate template : featureTemplates) {
			funcsGroupByTemple.put(template.getTemplateNumber(), new ArrayList<>());
		}
		for(FeatureFunction func : functions) {
			funcsGroupByTemple.get(func.getFeatureTemplateNum()).add(func);
		}
		//归一化
		Set<Entry<Short, List<FeatureFunction>>> entrys = funcsGroupByTemple.entrySet();
		for(Entry<Short, List<FeatureFunction>> entry : entrys) {
			List<FeatureFunction> funcs = entry.getValue();
			//将同一模板下的不同状态的特征函数归一化
			//也就是归一化不同状态下的观测分布
			//求加和
			double[] logSum = new double[stateNum];
			for(int i = 0; i < funcs.size(); i++) {
				for(int j = 0; j < stateNum;j++) {
					logSum[j] += funcs.get(i).getWeight()[j];
				}
			}
			for(int i = 0; i < stateNum;i++) {
				logSum[i] = Math.log(logSum[i]); 
			}
			//归一化
			for(int i = 0; i < funcs.size(); i++) {
				FeatureFunction f = funcs.get(i);
				double[] weight = f.getWeight();
				for(int j = 0; j < stateNum; j++) {
					if(weight[j] != 0) {
						weight[j] = Math.log(weight[j]) - logSum[j];
					} else {
						weight[j] = INFINITY;
					}
				}
			}
			funcAmount += funcs.size();
			logger.info("特征编号为U"+entry.getKey()+"产生特征函数:"+funcs.size()+"个");
		}
		logger.info("总的Unigram特征函数个数:"+funcAmount);
	}
	
	@Override
	public void train(List<SequenceNode> nodes) {
		this.train(getDefaultTemplate(), 2, nodes);
		
	}
	
	@Override
	public String getDefaultTemplate() {
		/*return "# Unigram\n" +
		        "U0:%x[-1,0]\n" +
		        "U1:%x[0,0]\n" +
		        "U2:%x[1,0]\n" +
		        "U3:%x[-2,0]%x[-1,0]\n" +
		        "U4:%x[-1,0]%x[0,0]\n" +
		        "U5:%x[0,0]%x[1,0]\n" +
		        "U6:%x[1,0]%x[2,0]\n" +
		        "\n" +
		        "# Bigram\n" +
		        "B";*/
		return super.getDefaultTemplate();
	}
	
	/**
	 * 监督学习CRF生成特征函数时需要统计，学习权重参数，而非监督CRF不需要
	 */
	@Override
	protected void generateFeatureFunction(List<SequenceNode> nodes, int i, TreeMap<String, FeatureFunction> funcs) {
		SequenceNode node = nodes.get(i);
		outer:for(FeatureTemplate featureTemplate : featureTemplates) {
			List<int[]> offsetList = featureTemplate.getOffsetList();
			//构建特征函数的观测x
			int[] x = new int[offsetList.size()];
			for(int j = 0; j < offsetList.size(); j++) {
				int[] offset = offsetList.get(j);
				//在开始或者结尾时可能出现offset超出范围的情况
				//如果这里直接跳过应该会有些影响，如果需要保留下来
				//则出现这种情况 了/_b+1,需要对_b+1超出范围的分配一个整型id
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
			if(old != null ) {
				//如果特征函数已经存在，需要对其权重++
				old.getWeight()[node.getState()]++;
			} else {
				//注意当前的状态y只是当前行的，与偏移到上一行无关
				FeatureFunction featureFunction = new FeatureFunction(funcId, featureTemplate.getTemplateNumber());
				featureFunction.setX(x);
				double[] weight = new double[stateNum];
				weight[node.getState()]++;
				featureFunction.setWeight(weight);
				funcs.put(funcId, featureFunction);
			}
		}
	}
	
	
	@Override
	public void train(String template, int maxOffsetOfTemplate, List<SequenceNode> nodes) {
		//this.maxOffsetOfTemplate = maxOffsetOfTemplate;
		//为nodes增加偏移节点
		//addOffsetNodes2trainData(nodes);
		logger.info("监督学习CRF训练开始...");
		long start = System.currentTimeMillis();
		//先定义好整体训练框架
		//1. 统计先验分布p(x,y) 并产生特征函数
		TreeMap<String,FeatureFunction> funcs = new TreeMap<>();
		parseStr2FeatureTemplate(template, "\n");
		for(int i = 0; i < nodes.size() -1 ; i++ ) {
			SequenceNode node = nodes.get(i);
			//遍历模板产生特征函数
			//只针对Unigram
			generateFeatureFunction(nodes, i, funcs);
			//针对Bigram 状态转移
			transferProbabilityWeights[node.getState()][nodes.get(i+1).getState()]++;
		}
		//最后一次循环产生特征函数
		generateFeatureFunction(nodes, nodes.size() -1, funcs);
		//将产生好的特征函数创建为DAT
		buildFeatureFunctionDAT(funcs);
		long end = System.currentTimeMillis();
		//概率归一化
		logger.info("概率归一化开始...");
		logProb();
		logger.info("概率归一化结束...");
		logger.info("监督学习CRF训练结束...耗时:"+(end - start) / 1000.0 +"秒");
	}
}
