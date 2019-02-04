package com.outsider.model.crf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.outsider.common.algorithm.dat.darts.DoubleArrayTrie;
import com.outsider.common.dataStructure.Table;
import com.outsider.common.logger.CONLPLogger;
import com.outsider.common.util.Storable;
import com.outsider.common.util.StorageUtils;
import com.outsider.model.SequenceModel;
import com.outsider.model.hmm.SequenceNode;

/**
 * CRF条件随机场基本模型的实现
 * @author outsider
 * 
 */
public abstract class CRF implements SequenceModel, Storable, Serializable{
	public Logger logger = CONLPLogger.getLoggerOfAClass(CRF.class);
	/**
	 * 模板中的最大行偏移量
	 * 保存时为了需要分配遍历观测时超出索引范围的分配id，需要确定最大偏移多少
	 * 例如: _B+4
	 * 当前默认模板是2
	 * 暂时不用这种方式，发现非常占用内存
	 */
	@Deprecated
	protected int maxOffsetOfTemplate = 0;
	
	/**
	 * Bigram模板中转移矩阵权重
	 * 转移特征函数的id选取行列位置的字符串拼接
	 */
	protected double[][] transferProbabilityWeights;
	/**
	 * 进展:大致搞明白了CRF++中的特征模板的意思
	 */
	//考虑到如何索引到指定的函数用DAT存储
	//适用于Unigram
	protected DoubleArrayTrie<FeatureFunction> featureFunctionTrie;
	/**
	 * 保存特征模板
	 */
	protected List<FeatureTemplate> featureTemplates;
	
	/**
	 * 观测数（不重复）
	 */
	protected int observationNum;
	/**
	 * 状态数
	 */
	protected int stateNum;
	//无穷大
	public static final double INFINITY =-1e31;
	public CRF() {
	}
	
	public double[][] getTransferProbabilityWeights() {
		return transferProbabilityWeights;
	}
	
	public CRF(int observationNum, int stateNum) {
		this.observationNum = observationNum;
		this.stateNum = stateNum;
		reInitParameters();
	}
	public void setStateNumAndObservationNum(int stateNum, int observationNum) {
		this.observationNum = observationNum;
		this.stateNum = stateNum;
		reInitParameters();
	}
	public int getObservationNum() {
		return observationNum;
	}
	public int getStateNum() {
		return stateNum;
	}
	/**
	 * 构建特征函数字典树
	 */
	protected void buildFeatureFunctionDAT(TreeMap<String,FeatureFunction> funcs) {
		logger.info("开始构建特征函数DAT...");
		featureFunctionTrie = new DoubleArrayTrie<>();
		featureFunctionTrie.build(funcs);
		logger.info("构建完成...");
	}
	
	/**
	 * 子方法，为train服务
	 * 根据当前遍历到的序列位置产生特征函数
	 * 注意此方法中只是产生特征函数，而不做权重的统计
	 * 监督学习CRF中的需要做权重的统计
	 * @param nodes 带标签的观测序列
	 * @param i 序列位置
	 * @param funcs 已经产生的特征函数map
	 */
	protected void generateFeatureFunction(List<SequenceNode> nodes, int i, TreeMap<String,FeatureFunction> funcs) {
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
				//初始权重为
				weight[nodes.get(i).getState()]++;
				featureFunction.setWeight(weight);
				funcs.put(funcId, featureFunction);
			} else {
				//计数
				old.getWeight()[nodes.get(i).getState()]++;
			}
		}
	}
	
	/**
	 * 解析字符串的特征模板为一个个FeatureTemplate
	 * @param template 模板
	 * @param splitChar 每一行的分隔符
	 */
	public void parseStr2FeatureTemplate(String template, String splitChar) {
		String[] lines = template.split(splitChar);
		this.featureTemplates = new ArrayList<>();
		for(String line : lines) {
			//暂时没有考虑兼容性的问题
			//比如这种模板，B01:%x[0,0]/%x[-1,0]
			FeatureTemplate ft = FeatureTemplate.createFeatureTemplate(line);
			if(ft != null)
				this.featureTemplates.add(ft);
		}
	}
	/**
	 * 训练数据为Table，指定x的列索引，y的列索引
	 * @param table
	 * @param xColumnIndex
	 * @param yColumnIndex
	 */
	public abstract void train(Table table, int xColumnIndex, int yColumnIndex);
	
	@Override
	public int[] predict(int[] x) {
		return veterbi(x);
	}
	
	/**
	 * 已知特征函数，计算某个状态(标签)的得分
	 * @param observations 输入观测x
	 * @param currentIndex 输入当前的序列位置
	 * @param state 输入需要计算的状态y
	 * @return 返回权重的累积相加
	 */
	public double computeUnigramWeight(List<FeatureFunction> funcs, int state) {
		double weight = 0;
		for(int i = 0; i < funcs.size(); i++) {
			//if(funcs.get(i).getWeight()[state] != INFINITY)
			weight += funcs.get(i).getWeight()[state];
		}
		return weight;
	}
	
	/**
	 * 获取当前观测下的特征函数
	 * 之前根据观测取特征函数是直接用观测来比对，效率非常慢，这里直接构造函数id没在trie中取出特征函数
	 * @param observations 观测
	 * @param currentIndex 当前的观测索引
	 * @return
	 */
	protected List<FeatureFunction> getUnigramFeatureFunction(int[] observations, int currentIndex){
		//Unigram  特征函数：wk*f(x,y_i)
		List<FeatureFunction> funcs = featureFunctionTrie.getValues();
		List<FeatureFunction> r_funcs = new ArrayList<>();
		outer:for(FeatureTemplate featureTemplate : featureTemplates) {
			List<int[]> offsetList = featureTemplate.getOffsetList();
			int x[] = new int[offsetList.size()];
			for(int i = 0; i < offsetList.size(); i++) {
				int[] offset = offsetList.get(i);
				int raw = currentIndex + offset[0];
				//可能越界
				if(raw < 0 || raw >= observations.length) {
					continue outer;
				}
				x[i] = observations[raw];
			}
			String funcId = FeatureFunction.generateFeatureFuncStrId(x, featureTemplate.getTemplateNumber());
			FeatureFunction featureFunction = featureFunctionTrie.getValue(funcId);
			if(featureFunction != null) r_funcs.add(featureFunction);
		}
		return r_funcs;
	}
	/**
	 * Unigram的特征函数遍历
	 * 实际并没有遍历直接取出来
	 * @param y_i_1 y_(i-1)
	 * @param y_i y_(i)
	 * @return
	 */
	protected double computeBigramWeight(int y_i_1,int y_i) {
		//Bigram 特征函数：wk*f(x,y_(i-1),y_i)
		//if(transferProbabilityWeights[y_i_1][y_i] == INFINITY) return 0;
		return transferProbabilityWeights[y_i_1][y_i]/* == INFINITY ? 0 : transferProbabilityWeights[y_i_1][y_i]*/;
	}
	/**
	 * 解码算法维特比
	 * @param observations 观测序列
	 * @return 最佳预测序列
	 */
	public int[] veterbi(int[] observations) {
		int xLen = observations.length;
		//保存当前最佳状态由前一个状态中哪一个状态得到的
		int[][] psi = new int[xLen][stateNum];
		double[][] deltas = new double[xLen][stateNum];
		//初始化delta,第一次计算wk*f(x,y_0) 此时没有Bigram
		List<FeatureFunction> funcs = getUnigramFeatureFunction(observations, 0 );
		for(int i = 0; i  < stateNum; i++) {
			double weight = computeUnigramWeight(funcs, i);
			deltas[0][i] = weight;
		}
		//DP计算所有delta
		for(int t = 1; t < xLen; t++) {
			//获取当前位置的特征函数list
			List<FeatureFunction> curFuncs = getUnigramFeatureFunction(observations, t );
			for(int i = 0; i < stateNum; i++) {
				//找到最好的前驱状态
				deltas[t][i] = deltas[t-1][0] + computeBigramWeight(0, i);
				for(int j = 1; j < stateNum; j++) {
					double tmp = deltas[t-1][j] + computeBigramWeight(j, i);
					if(tmp > deltas[t][i]) {
						deltas[t][i] = tmp;
						psi[t][i] = j;//保存当前最佳状态由哪一个前驱状态产生
					}
				}
				//加上当前Unigram的累积权重值, 遍历特征函数特别耗时
				deltas[t][i] += computeUnigramWeight(curFuncs, i);
			}
		}
		//找到最后一个观测的最佳状态，回溯
		int[] best = new int[xLen];//保存最优预测序列
		double max = deltas[xLen-1][0];
		for(int i = 1; i < stateNum; i++) {
			if(deltas[xLen-1][i] > max) {
				max = deltas[xLen-1][i];
				best[xLen-1] = i;
			}
		}
		//回溯
		for(int i = xLen - 2; i >=0; i--) {
			best[i] = psi[i+1][best[i+1]];
		}
		return best;
	}
	
	/**
	 * 获取默认的特征
	 * @return
	 */
	public String getDefaultTemplate() {
		return "U00:%x[-2,0]\n" + 
				"U01:%x[-1,0]\n" + 
				"U02:%x[0,0]\n" + 
				"U03:%x[1,0]\n" + 
				"U04:%x[2,0]\n" +
				"U05:%x[-2,0]/%x[-1,0]/%x[0,0]\n" + 
				"U06:%x[-1,0]/%x[0,0]/%x[1,0]\n" + 
				"U07:%x[0,0]/%x[1,0]/%x[2,0]\n" + 
				"U08:%x[-1,0]/%x[0,0]\n" + 
				"U09:%x[0,0]/%x[1,0]\n" + 
				"#Bigram\n"+
				"B";
	}
	
	public DoubleArrayTrie<FeatureFunction> getFeatureFunctionTrie() {
		return featureFunctionTrie;
	}
	
	@Override
	public void reInitParameters() {
		transferProbabilityWeights = new double[stateNum][stateNum];
		featureFunctionTrie = null;
	}
	
	/**
	 * 生成CRF模型模板文件
	 * @return
	 */
	public abstract String generateModelTemplate();
	/**
	 * 指定模板来训练
	 * @param template 模板
	 * @param maxOffsetOfTemplate 模板的最大行偏移量
	 * @param nodes 训练数据
	 */
	public abstract void train(String template, int maxOffsetOfTemplate ,List<SequenceNode> nodes);
	/**
	 * 为训练集增加偏移节点
	 * id默认为超出例如 -1 则id为-1
	 * 往后超出，例如 1 则id为observationNum,例如2则id为observationNum+1
	 * @param nodes
	 * @deprecated
	 */
	/*protected void addOffsetNodes2trainData(List<SequenceNode> nodes) {
		for(int i = 0; i < maxOffsetOfTemplate; i++) {
			nodes.add(0, new SequenceNode(-(i+1)));
			nodes.add(new SequenceNode(this.observationNum+i));
		}
	}*/
	
	
	@Override
	public void open(String directory, String fileName) {
		DataInputStream  in = null;
		try {
			if(fileName == null || fileName.trim().equals("")) {
				fileName = this.getClass().getSimpleName();
			}
			in = new DataInputStream(new BufferedInputStream(
					new  FileInputStream(directory+"/"+fileName)));
			this.featureFunctionTrie = new DoubleArrayTrie<>();
			this.featureFunctionTrie.open(directory+"/"+fileName+"_featureFunctionTire");
			this.stateNum = in.readInt();
			this.observationNum = in.readInt();
			this.transferProbabilityWeights = new double[stateNum][stateNum];
			for(int i = 0; i < stateNum; i++) {
				for(int j = 0; j < stateNum; j++) {
					transferProbabilityWeights[i][j] = in.readDouble();
				}
			}
			this.featureTemplates = new ArrayList<>();
			StorageUtils.open(directory, fileName+"_featureTemplates", this.featureTemplates);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void save(String directory, String fileName) {
		System.out.println("crf保存...");
		DataOutputStream  out = null;
		try {
			if(fileName == null || fileName.trim().equals("")) {
				fileName = this.getClass().getSimpleName();
			}
			out = new DataOutputStream(new BufferedOutputStream(
					new  FileOutputStream(directory+"/"+fileName)));
			this.featureFunctionTrie.save(directory+"/"+fileName+"_featureFunctionTire");
			out.writeInt(this.stateNum);
			out.writeInt(this.observationNum);
			for(int i = 0; i < stateNum; i++) {
				for(int j = 0; j < stateNum; j++) {
					out.writeDouble(transferProbabilityWeights[i][j]);
				}
			}
			StorageUtils.save(directory, fileName+"_featureTemplates", this.featureTemplates);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
