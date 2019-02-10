package com.outsider.model.crf.unsupervised;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.outsider.common.dataStructure.Table;
import com.outsider.common.util.MathUtils;
import com.outsider.model.crf.CRF;
import com.outsider.model.crf.FeatureFunction;
import com.outsider.model.crf.FeatureTemplate;
import com.outsider.model.hmm.SequenceNode;
import com.outsider.model.metric.Metric;
import com.outsider.nlp.segmenter.SegmentationPredictor;
import com.outsider.nlp.segmenter.SegmentationUtils;
import com.outsider.nlp.segmenter.SegmenterTest;
/**
 * 2018.12.7 
 * 重新理清了一遍《统计学习方法》中的推导，准备实现书中提到的基于改进的迭代尺度算法的CRF
 * @author outsider
 * 终于明白了:
 * 1.先验期望中
 * 公式Σp(x,y)*f(x,y)的p(x,y)是笼统的，在crf++中不同的一元模板对应了不同的概率分布和特征函数，
 * 需要对应求模型期望，比如：
 * (1) p(x[t],y[t]) 
 * (2) p(x[t-1],x[t],x[t+1],y[t])
 * (3) p(x[t-1],y[t])
 * (4) p(y[t-1],y[t])
 * ....
 * (1)(2)(3)(4)对应了不同的概率分布，求特征函数关于p(x,y){这里指笼统的p(x,y)}概率分布的期望时
 * 应该区分开。
 * 
 * 2.注意模型期望中
 * Σp(x,y)*f(x,y) = Σp(x) p(y|x) *f(x,y),其中p(x)是先验期望。
 * 这里的p(x,y)不同于先验分布中的，这里的p(y,x)中的x指的是所有可能影响y的随机变量x，
 * 而p(y|x)就是模型需要计算的，针对unigram这里是p(yi|x)
 * 而针对bigram，这里模型需要计算的是p(y[i-1],y[i]|x)
 */
public class UnsupervisedCRF extends CRF implements SegmentationPredictor{
	/**
	 * 训练数据长度
	 */
	private int T = 0;
	private static final long serialVersionUID = 1L;
	public UnsupervisedCRF() {
		super();
		reInitParameters();
	}

	public UnsupervisedCRF(int observationNum, int stateNum) {
		super(observationNum, stateNum);
		reInitParameters();
	}
	@Override
	public void train(List<SequenceNode> data) {
		train(getDefaultTemplate(), 0, data);
	}
	
	public String[] seg(String text) {
		int[] x = SegmentationUtils.str2int(text);
		int[] predict = this.veterbi(x);
		return SegmentationUtils.decode(predict, text);
	}
	@Override
	public void train(Table table, int xColumnIndex, int yColumnIndex) {
	}
	
	@Override
	public String getDefaultTemplate() {
		return "# Unigram\n" +
	            "U0:%x[-1,0]\n" +
	            "U1:%x[0,0]\n" +
	            "U2:%x[1,0]\n" +
	            "U3:%x[-2,0]%x[-1,0]\n" +
	            "U4:%x[-1,0]%x[0,0]\n" +
	            "U5:%x[0,0]%x[1,0]\n" +
	            "U6:%x[1,0]%x[2,0]\n" +
	            "\n" +
	            "# Bigram\n" +
	            "B";
	}
	@Override
	public String generateModelTemplate() {
		return null;
	}
	@Override
	public void reInitParameters() {
		transferProbabilityWeights = new double[stateNum][stateNum];
		featureFunctionTrie = null;
	}
	@Override
	public void train(String template, int maxOffsetOfTemplate, List<SequenceNode> nodes) {
		//记录不同特征函数出现的次数
		this.T = nodes.size();
		//一个特征模板中的项(比如:U03:%x[-2,0]%x[-1,0])对应了一个概率分布，统计属于该分布的发生的频次
		//key:是特征模板的字符串id，value是对应的count
		Map<Short, Integer> featureTemplateCount = new HashMap<>();
		int bigramFeatureCount = 0;//bigram特征计算
		//先定义好整体训练框架
		//1. 统计先验分布p(x,y) 并产生特征函数
		TreeMap<String,FeatureFunction> funcs = new TreeMap<>();
		parseStr2FeatureTemplate(template, "\n");
		//初始化
		for(FeatureTemplate featureTemplate : featureTemplates) {
			Short fid = featureTemplate.getTemplateNumber();
			featureTemplateCount.put(fid, 0);
		}
		for(int i = 0; i < nodes.size() -1 ; i++ ) {
			SequenceNode node = nodes.get(i);
			//遍历模板产生特征函数
			//只针对Unigram
			generateFeatureFunction(nodes, i, funcs);
			//针对Bigram 状态转移
			transferProbabilityWeights[node.getState()][nodes.get(i+1).getState()]++;
		}
		transferProbabilityWeights[nodes.get(T-2).getState()][nodes.get(T-1).getState()]++;
		generateFeatureFunction(nodes, T - 1, funcs);
		//计算featureTemplateCount
		for(FeatureFunction featureFunction : funcs.values()) {
			Short templeId = featureFunction.getFeatureTemplateNum();
			int count = featureTemplateCount.get(templeId);
			for(double w : featureFunction.getWeight()) {
				count += w;
			}
			featureTemplateCount.replace(templeId, count);
		}
		for(double[] i : transferProbabilityWeights) {
			bigramFeatureCount += MathUtils.sum(i);
		}
		/**
		 *  put:1826448
			put:1826449
			put:1826448
			put:1826447
			put:1826448
			put:1826448
			put:1826447
		 */
		//对特征函数计数
		//将产生好的特征函数创建为DAT
		buildFeatureFunctionDAT(funcs);
		int[] x = new int[T];
		int[] y = new int[T];
		for(int i = 0; i < T; i++) {
			x[i] = nodes.get(i).getNodeIndex();
			y[i] = nodes.get(i).getState();
		}
		//初始化参数并计数总特征数
		//降低特征总数增加，减少迭代次数
		int allFeatureNum = 20;
		//改进的迭代尺度优化
		IIS(1E-4, -1, allFeatureNum, x, y, featureTemplateCount, bigramFeatureCount);
	}
	/**
	 * 初始化权重
	 * 对于之前统计中为0的权重初始化为负无穷，如果直接初始化为0，将导致问题，，因为有的权重可能为负数，这样为0的权重
	 * 反而更大，对解码产生影响
	 * @return 出现的特征总和
	 */
	private void initWeights() {
		List<FeatureFunction> funcs = featureFunctionTrie.getValues();
		for(FeatureFunction featureFunction : funcs) {
			for(int i = 0; i < stateNum; i++) {
				if(featureFunction.getWeight()[i] == 0)
					featureFunction.getWeight()[i] = INFINITY;
				else
					featureFunction.getWeight()[i] = 0;
			}
		}
		for(int i = 0; i < stateNum; i++) {
			for(int j = 0; j < stateNum; j++) {
				if(transferProbabilityWeights[i][j] == 0)
					transferProbabilityWeights[i][j] = INFINITY;
				else
					transferProbabilityWeights[i][j] = 0;
			}
		}
	}
	/**
	 * 由训练数据产生特征函数
	 * @param nodes
	 * @param i
	 * @param funcs
	 * @param unigramFeaturePositions
	 */
	protected void generateFeatureFunction(List<SequenceNode> nodes, int i, 
			TreeMap<String,FeatureFunction> funcs) {
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
			FeatureFunction oldFunc = funcs.get(funcId);
			int cy = nodes.get(i).getState();
			if(oldFunc == null ) {
				//注意当前的状态y只是当前行的，与偏移到上一行无关
				FeatureFunction featureFunction = new FeatureFunction(funcId, featureTemplate.getTemplateNumber());
				featureFunction.setX(x);
				double[] weight = new double[stateNum];
				weight[cy]++;
				featureFunction.setWeight(weight);
				funcs.put(funcId, featureFunction);
			} else {
				//计数
				oldFunc.getWeight()[cy]++;
			}
		}
	}
	
	/**
	 * IIS(改进迭代尺度)优化算法
	 * @param epsilon 精度，当所有参数和之前参数的差值都小于该精度，迭代终止
	 * @param maxIter 最大迭代次数,默认为100000
	 * @param allFeatureNum 所有特征总和，根据当前的训练数据可得，对应《统计学习方法》中的T(x,y)
	 * @param unigramFeaturePositions 二元特征函数在序列中出现的位置
	 * @param bigarmFeaturePosition 一元特征函数在序列中出现的位置，key是特征函数id，value是位置list
	 * @param x 观测序列
	 * @param y 状态序列
	 */
	public void IIS(double epsilon, int maxIter, int allFeatureNum, int[] x, int[] y, 
			Map<Short, Integer> featureTemplateCount,
			int bigramFeatureCount) {
		if(maxIter <= 0) 
			maxIter = 100000;
		//只计算一次先验期望
		double[][] priorExpectation = priorExpectation(x, y, featureTemplateCount, bigramFeatureCount);
		//初始化权重
		initWeights();
		for(int i = 0; i < maxIter; i++) {
			System.out.println("iter..."+i);
			//计算delta
			double[][] delta = calcDelta(x, y, allFeatureNum, priorExpectation, featureTemplateCount, bigramFeatureCount);
			//更新参数
			//unigram
			double[][] oldWeights = new double[featureFunctionTrie.getKeySize() + transferProbabilityWeights.length][stateNum];
			for(int j = 0; j < featureFunctionTrie.getKeySize(); j++) {
				FeatureFunction featureFunction = featureFunctionTrie.getValue(j);
				for(int k = 0; k < stateNum; k++) {
					oldWeights[j][k] = featureFunction.getWeight()[k];
					featureFunction.getWeight()[k] = featureFunction.getWeight()[k] + delta[j][k];
				}
			}
			//bigram
			int offset = delta.length - transferProbabilityWeights.length;
			for(int j = 0; j < stateNum; j++) {
				for(int k = 0; k < stateNum; k++) {
					oldWeights[j + offset][k] = transferProbabilityWeights[j][k];
					transferProbabilityWeights[j][k] = transferProbabilityWeights[j][k] + delta[j+offset][k];
				}
			}
			
			/*System.out.println("状态转移特征");
			System.out.println("\tB\tM\tE\tS");
			System.out.println("B\t"+Arrays.toString(transferProbabilityWeights[0]));
			System.out.println("M\t"+Arrays.toString(transferProbabilityWeights[1]));
			System.out.println("E\t"+Arrays.toString(transferProbabilityWeights[2]));
			System.out.println("S\t"+Arrays.toString(transferProbabilityWeights[3]));*/
			String[] test = new String[] {
			"原标题：日媒拍到了现场罕见一幕，据日本新闻网（NNN）9月8日报道，日前，日本海上自卫队现役最大战舰之一的直升机航母“加贺”号在南海航行时，遭多艘中国海军战舰抵近跟踪监视。 ",
			"HanLP是由一系列模型与算法组成的Java工具包，目标是普及自然语言处理在生产环境中的应用。",
			"高锰酸钾，强氧化剂，紫红色晶体，可溶于水，遇乙醇即被还原。常用作消毒剂、水净化剂、氧化剂、漂白剂、毒气吸收剂、二氧化碳精制剂等。",
			"《夜晚的骰子》通过描述浅草的舞女在暗夜中扔骰子的情景,寄托了作者对庶民生活区的情感",
			"这个像是真的[委屈]前面那个打扮太江户了，一点不上品...@hankcs",
			"鼎泰丰的小笼一点味道也没有...每样都淡淡的...淡淡的，哪有食堂2A的好次",
			"克里斯蒂娜·克罗尔说：不，我不是虎妈。我全家都热爱音乐，我也鼓励他们这么做。",
			"今日APPS：Sago Mini Toolbox培养孩子动手能力",
			"财政部副部长王保安调任国家统计局党组书记",
			"2.34米男子娶1.53米女粉丝 称夫妻生活没问题。",
			"你看过穆赫兰道吗",
			"乐视超级手机能否承载贾布斯的生态梦"
			};
			for(String te : test) {
				System.out.println(Arrays.toString(seg(te)));
			}
			//SegmenterTest.score(this, "crf");
			//检查是否达到精度要求
			boolean isConvergent = isConvergent(epsilon, oldWeights);
			int[] py = veterbi(x);
			float accuracy = Metric.accuracyScore(py, y);
			System.err.println(i);
			System.err.println("acc:"+accuracy);
			if(isConvergent) {
				System.out.println("达到精度要求...");
				break;
			}
		}
	}
	
	/**
	 * 检查权重参数是否收敛
	 * @param epsilong 精度要求
	 * @param oldWeights 旧权重
	 * @return
	 */
	public boolean isConvergent(double epsilong, double[][] oldWeights) {
		for(int i = 0; i < oldWeights.length;i++) {
			FeatureFunction featureFunction = featureFunctionTrie.getValue(i);
			for(int j = 0; j < stateNum; j++) {
				if(Math.abs(featureFunction.getWeight()[j] - oldWeights[i][j]) > epsilong) {
					return false;
				}
			}
		}
		int offset = oldWeights.length - transferProbabilityWeights.length;
		for(int i = 0; i < transferProbabilityWeights.length; i++) {
			for(int j = 0; j < stateNum; j++) {
				if(Math.abs(transferProbabilityWeights[i][j] - oldWeights[offset + i][j]) > epsilong) {
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * 计算更新参数用到的参数增量delta
	 * δ  = (1/T) * (E[先验分布] / E[模型参数])
	 */
	public double[][] calcDelta(int[] x, int[] y, int allFeatureNum,
			double[][] priorExpectation,
			Map<Short, Integer> featureTemplateCount,
			int bigramFeatureCount) {
		double[][] delta = new double[featureFunctionTrie.getKeySize() + transferProbabilityWeights.length][stateNum];
		//计算M(x)矩阵
		double[][][] m_matrix = calcMmartix(x);
		//前向
		double[][] alpha = forward(x, m_matrix);
		//后向
		double[][] beta = backward(x, m_matrix);
		//模型期望
		double[][] expectation = modelExpectation(x, alpha, beta, m_matrix, featureTemplateCount, bigramFeatureCount);
		
		for(int i = 0; i < expectation.length; i++) {
			for(int j = 0; j < stateNum; j++) {
				delta[i][j] = (priorExpectation[i][j] - expectation[i][j]) / allFeatureNum;
			}
		}
		return delta;
	}
	@Override
	protected double computeBigramWeight(int y_i_1,int y_i) {
		return transferProbabilityWeights[y_i_1][y_i] == INFINITY ? 0 : transferProbabilityWeights[y_i_1][y_i];
	}
	/**
	 * 之前问题就出在这里，因为延用了监督crf中的computeUnigramWeight，不占权重的特征函初始化为INFINITY
	 * 若不作判断，在累积的权重的时候加上INFINITY会导致精度问题？
	 * 由于权重最后并不是作为一个log值存储，按道理应该将不占权重的特征函数的权重值初始化为0
	 * 考虑存储为log值，这样精度应该更好
	 */
	@Override
	public double computeUnigramWeight(List<FeatureFunction> funcs, int state) {
		double weight = 0;
		for(int i = 0; i < funcs.size(); i++) {
			if(funcs.get(i).getWeight()[state] != INFINITY)
				weight += funcs.get(i).getWeight()[state];
		}
		return weight;
	}
	
	/**
	 * 计算取对数后的M(y[i-1],y[i] | x)矩阵
	 * 注意这里没有取exp，所以相当于是取了log后的M矩阵
	 * @param x
	 * @return 所有M矩阵，一个三维数组，M[i][y_i_1][y_i]
	 */
	public double[][][] calcMmartix(int[] x){
		double[][][] m_matrix = new double[T][stateNum][stateNum];
		//1.初始的时候没有y_i_1，没有转移特征矩阵退化为向量
		List<FeatureFunction> funcs = getUnigramFeatureFunction(x, 0);
		for(int i = 0; i < stateNum; i++) {
			m_matrix[0][0][i] = computeUnigramWeight(funcs, i);
		}
		//1到t的位置
		for(int t = 1; t < T; t++) {
			List<FeatureFunction> funcs2 = getUnigramFeatureFunction(x, t);
			for(int y_i_1 = 0; y_i_1 < stateNum; y_i_1++) {
				for(int y_i = 0; y_i < stateNum; y_i++) {
					m_matrix[t][y_i_1][y_i] = computeUnigramWeight(funcs2, y_i) + 
							computeBigramWeight(y_i_1, y_i);
				}
			}
		}
		return m_matrix;
	}
	
	/**
	 * 值为取log后的值
	 * 前向算法
	 * @param x 观测序列x
	 * @param M(y[i-1],y[i])矩阵
	 */
	public double[][] forward(int[] x, double[][][] m_matrix) {
		double[][] alpha = new double[T][stateNum];
		//初始t = 0
		for(int i = 0; i < stateNum; i++) {
			//Log( α[0][i] ) = Log( M(Y0|x) )
			alpha[0][i] = m_matrix[0][0][i];
		}
		double[] tmp = new double[stateNum];
		for(int t = 1; t < T; t++) {
			for(int y_i = 0; y_i < stateNum; y_i++) {
				for(int y_i_1 = 0; y_i_1 < stateNum; y_i_1++) {
					tmp[y_i_1] = (alpha[t-1][y_i_1] + m_matrix[t][y_i_1][y_i]);
				}
				alpha[t][y_i] = logSumExp(tmp);
			}
		}
		return alpha;
	}
	
	/**
	 * 后向算法
	 * @param x 观测序列x
	 * @param M(y[i-1],y[i])矩阵
	 */
	public double[][] backward(int[] x, double[][][] m_matrix) {
		double[][] beta = new double[T][stateNum];
		List<FeatureFunction> funcs = getUnigramFeatureFunction(x, T - 1);
		//初始没有y[i+1]
		for(int i = 0; i < stateNum; i++) {
			beta[T - 1][i] = computeUnigramWeight(funcs, i);
		}
		double[] tmp = new double[stateNum];
		for(int t = T - 2; t >= 0; t--) {
			for(int y_i = 0; y_i < stateNum; y_i++) {
				for(int y_ip1 = 0; y_ip1 < stateNum; y_ip1++) {
					tmp[y_ip1] = (beta[t + 1][y_ip1] + m_matrix[t + 1][y_i][y_ip1]);
				}
				beta[t][y_i] = logSumExp(tmp);
			}
		}
		return beta;
	}
	
	
	/**
	 * 修正后的求模型期望方法
	 * 返回log值
	 * 只有p(x)先验分布的特征期望
	 * 特征函数关于p(x,y)的期望
	 * p(x,y) = p_(x) * p(y|x)
	 * p_(x)是经验分布
	 * @param x
	 * @param alpha
	 * @param beta
	 * @param unigramFeaturePositions 见IIS说明
	 * @param bigarmFeaturePosition  见IIS说明
	 * @param m_matrix M(y[i-1],y[i]|x)矩阵
	 */
	public double[][] modelExpectation(int[] x, 
			double[][] alpha, double[][] beta, 
			double[][][] m_matrix,
			Map<Short, Integer> featureTemplateCount,
			int bigramFeatureCount) {
		double logZ = logSumExp(alpha[T-1]);
		//double logZ_beta = logSumExp(beta[0]); 和用alpha计算的logZ是一样的值
		double[][] expectations = new double[featureFunctionTrie.getKeySize() + transferProbabilityWeights.length][stateNum];
		//unigram
		for(int t = 0; t < x.length; t++) {
			outer:for(FeatureTemplate template : featureTemplates) {
				List<int[]> offsetList = template.getOffsetList();
				int featureCount = featureTemplateCount.get(template.getTemplateNumber());
				int[] xx = new int[offsetList.size()];
				for(int i = 0; i < offsetList.size(); i++) {
					int[] offset = offsetList.get(i);
					int p = t + offset[0];
					if(p < 0 || p >= x.length)
						continue outer;
					xx[i] = x[p];
				}
				String funcId = FeatureFunction.generateFeatureFuncStrId(xx, template.getTemplateNumber());
				FeatureFunction featureFunction = featureFunctionTrie.getValue(funcId);
				int intId = featureFunctionTrie.intIdOf(funcId);
				double[] w = featureFunction.getWeight();
				for(int y = 0; y < stateNum; y++) {
					//权重初始化为INFINITY的特征函数代表权重为0
					if(w[y] != INFINITY) {
						//模型估计 p(y|x) = 
						double log_pyx = alpha[t][y] + beta[t][y] - logZ;
						expectations[intId][y] += (Math.exp(log_pyx -Math.log(featureCount)));
					} else { 
						expectations[intId][y] = INFINITY;
					}
				}
			}
		}
		int offset = expectations.length - transferProbabilityWeights.length;
		//bigram
		for(int t = 1; t < x.length; t++) {
			for(int y_i_1 = 0; y_i_1 < stateNum; y_i_1++) {
				for(int y_i = 0; y_i < stateNum; y_i++) {
					//模型估计 p(y[t-1],y[t]|x)
					if(transferProbabilityWeights[y_i_1][y_i] != INFINITY) {
						double log_py = alpha[t-1][y_i_1] + m_matrix[t][y_i_1][y_i] + beta[t][y_i] - logZ;
						expectations[offset+y_i_1][y_i] += (Math.exp(log_py - Math.log(bigramFeatureCount) )); 
						//System.out.println("bigram["+y_i_1+","+y_i+"]"+"_modelExpectation:"+(Math.exp(log_py - Math.log(bigramFeatureCount) )));
						//System.out.println("bigram["+y_i_1+","+y_i+"]"+"_logModelExpecta:"+(log_py - Math.log(bigramFeatureCount)));
					} else {
						expectations[offset+y_i_1][y_i] = INFINITY;
					}
				}
			}
		}
		//取对数
		for(int i = 0; i < expectations.length; i++) {
			for(int j = 0; j < stateNum; j++) {
				if(expectations[i][j] != INFINITY)
					expectations[i][j] = Math.log(expectations[i][j]);
			}
		}
		return expectations;
	}
	
	/**
	 * 修正后的求模型的先验期望
	 * 返回期望的log值，若该期望值未0.值为infinity
	 * 特征关于先验分布p(x,y)的期望
	 * 终于明白了:
	 * 公式中Σp(x,y)*f(x,y)的p(x,y)是笼统的，在crf++中不同的一元模板对应了不同的概率分布和特征函数，
	 * 需要对应求模型期望，比如：
	 * (1) p(x[t],y[t]) 
	 * (2) p(x[t-1],x[t],x[t+1],y[t])
	 * (3) p(x[t-1],y[t])
	 * (4) p(y[t-1],y[t])
	 * ....
	 * (1)(2)(3)(4)对应了不同的概率分布，求特征函数关于p(x,y){这里指笼统的p(x,y)}概率分布的期望时
	 * 应该区分开。
	 * @param x
	 * @param y
	 * @param featureTemplateCount
	 * @param bigramFeatureCount
	 * @return
	 */
	public double[][] priorExpectation(int[] x, int[] y,
			Map<Short, Integer> featureTemplateCount,
			int bigramFeatureCount){
		//所有特征函数的先验模型期望，包括那些未出现的特征
		//将特征函数根据Strid映射为int值作为数组下标
		//数组的最后放的是bigram特征函数的期望
		double[][] expectations = new double[featureFunctionTrie.getKeySize() + transferProbabilityWeights.length][stateNum];
		//unigram
		List<FeatureFunction> featureFunctions = featureFunctionTrie.getValues();
		for(FeatureFunction featureFunction : featureFunctions) {
			int count = featureTemplateCount.get(featureFunction.getFeatureTemplateNum());
			//获取
			double[] expectaionArr = expectations[featureFunctionTrie.intIdOf(featureFunction.getStrId())];
			double[] w = featureFunction.getWeight();
			for(int i = 0; i < stateNum; i++) {
				if(w[i] == 0) {
					expectaionArr[i] = INFINITY;
				} else {
					expectaionArr[i] = Math.log(w[i]) - Math.log(count);
				}
			}
		}
		//bigram
		int offset = expectations.length - transferProbabilityWeights.length;
		for(int i = 0; i < stateNum; i++) {
			for(int j = 0; j < stateNum; j++) {
				if(transferProbabilityWeights[i][j] == 0) {
					expectations[offset + i][j] = INFINITY;
				} else {
					expectations[offset + i][j] = Math.log(transferProbabilityWeights[i][j]) - Math.log(bigramFeatureCount);
				}
			}
		}
		return expectations;
	}
	/**
	 * logSumExp计算技巧
	 * @param arr
	 * @return
	 */
	public double logSumExp(double[] arr) {
		if(arr.length == 0) return INFINITY;
		double max = MathUtils.max(arr);
		double res = 0;
		for(int i = 0; i < arr.length; i++) {
			res += Math.exp(arr[i] - max);
		}
		return max + Math.log(res);
	}

	@Override
	public List<String[]> seg(String[] texts) {
		return null;
	}
}
