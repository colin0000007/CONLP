package com.outsider.model.maxEntropy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.outsider.common.dataStructure.Table;
/**
 * 简单实现一个用于普通数据的最大熵模型，非序列数据。
 * 不能定义特征模板来获取上下文特征。
 * 只能使用离散特征。
 * 最大熵天然的适合多分类，所以多分类也没问题。
 * 计算过程没有取对数，但是浮点运算都是double，精度相对float要高。
 * 优化方法采用IIS
 * 另外特征函数的存储和索引只是简单的用list和map，如果数据量较大特征函数较多会比较费内存，采用DoubleArrayTire存储特征函数比较合适。（双数组字典树）
 * 需要注意，如果数据集存在两个不同维度的特征的取值范围是一样的，比如图片中不同维度的像素值。这种情况下需要为每个维度加上一个维度标识符
 * 必然不同维度但取值范围一样的多个维度将退化为单个维度
 * @author outsider
 */
public class MaxEnt {
	//x特征和y标签编码为int类型
	private Map<String, Integer> x2id;
	//intId变为特征字符
	private String[] id2x;
	private Map<String, Integer> y2id;
	private String[] id2y;
	//特征函数
	public List<FeatureFunction> funcs;
	//特征函数id到int的映射
	private Map<String, Integer> funcId2Int; 
	/**
	 * 训练接口
	 * @param table 二维表数据格式
	 * @param xColumnIndexes 使用哪些列作为特征列
	 * @param yColumnIndex 标签列索引
	 * @param maxIter 最大迭代次数
	 * @param epsilon 参数收敛精度
	 */
	public void train(Table table, int[] xColumnIndexes, int yColumnIndex, int maxIter, double epsilon) {
		List<String[]> xs = new ArrayList<>();
		String[] y = new String[table.getRowNum()];
		for(int i = 0; i < table.getRowNum(); i++) {
			String[] x = new String[xColumnIndexes.length];
			for(int j = 0; j < x.length; j++) {
				x[j] = table.get(i, xColumnIndexes[j]);
			}
			xs.add(x);
			y[i] = table.get(i, yColumnIndex);
		}
		//对标签和特征集编码
		encode(xs, y);
		//生成特征函数
		int M = generateFeatureFunction(xs, y);
		//这里的M取特征的维度
		M = table.getColumnNum();
		//按照公式M应该是所有特征函数出现的次数
		System.out.println("M:"+M);
		System.out.println("epsilon:"+epsilon);
		System.out.println("maxIter:"+maxIter);
		System.out.println("Amount of FeatureFunction:"+id2x.length);
		System.out.println("Amout of Label:"+id2y.length);
		System.out.println("train data.shape:"+xs.size()+","+xs.get(0).length);
		//优化
		//IIS(maxIter, epsilon, table, xColumnIndex, yColumnIndex);
		IIS(maxIter, epsilon, xs , y, M);
	}
	
	/**
	 * 获取y标签名字
	 * 用于预测结果
	 * @param index
	 * @return
	 */
	public String yName(int index) {
		return id2y[index];
	}
	
	/**
	 * 对离散的x特征和y标签进行int编码，方便处理
	 * @param table
	 * @param xColumnIndexes
	 * @param yColumnIndex
	 */
	public void encode(List<String[]> xs, String[] y) {
		Set<String> xSet = new HashSet<>();
		Set<String> ySet = new HashSet<>();
		for(String[] xArr : xs) {
			for(String x : xArr) {
				xSet.add(x);
			}
		}
		for(String yy : y) {
			ySet.add(yy);
		}
		id2x = new String[xSet.size()];
		id2y = new String[ySet.size()];
		x2id = new HashMap<>();
		y2id = new HashMap<>();
		int count = 0;
		for(String s : xSet) {
			id2x[count] = s;
			x2id.put(s, count);
			count++;
		}
		count = 0;
		for(String s : ySet) {
			id2y[count] = s;
			y2id.put(s, count);
			count++;
		}
	}
	
	/**
	 * 生成特征函数
	 * @param table
	 * @param xColumnIndexes
	 * @param yColumnIndex
	 * @return 返回IIS优化中要用到的常数M
	 */
	public int generateFeatureFunction(List<String[]> xs, String[] ys) {
		funcs = new ArrayList<>();
		funcId2Int = new HashMap<>();
		int rowNum = xs.size();
		int M = 0;
		int count = 0;
		for(int i = 0; i < rowNum; i++) {
			String[] row = xs.get(i);
			String label = ys[i];
			int labelInt = y2id.get(label);
			for(String x : row) {
				int xInt = x2id.get(x);
				String funcId = FeatureFunction.generateFuncId(xInt, labelInt);
				Integer funcIndex = funcId2Int.get(funcId);
				FeatureFunction func;
				if(funcIndex == null) {
					func = new FeatureFunction(xInt, labelInt, 1);
					funcs.add(func);
					funcId2Int.put(funcId, count++);
					List<String> s = new ArrayList<>();
					M++;
				} else {
					func = funcs.get(funcIndex);
					M++;
					func.setWeight(func.getWeight() + 1);
				}
			}
		}
		return M;
	}
	/**
	 * 初始化特征权重
	 */
	public void initParameter() {
		for(FeatureFunction featureFunction : funcs) {
			featureFunction.setWeight(0);
		}
	}
	/**
	 * IIS优化
	 * @param maxIter 最大迭代次数
	 * @param epsilon 参数精度，和上一次的权重相比
	 * @param xs x样本集合
	 * @param y y标签
	 * @param M 优化用到的一个常数，该常数类似于梯度下降中的学习效率，可以决定步伐，越大优化越慢
	 */
	public void IIS(int maxIter, double epsilon, List<String[]> xs, String[] y, int M) {
		int rowNum = xs.size();
		if(maxIter <= 0) {
			maxIter = 100000;
		}
		int count = 0;
		double[] priorExpectations = priorExpectation(rowNum);
		initParameter();
		double[] oldWeight = new double[funcs.size()];
		for(int i = 0; i < maxIter; i++) {
			System.out.println("iter..."+i);
			double[] modelExpectation = modelExpectation(xs);
			count = 0;
			//更新参数
			for(FeatureFunction featureFunction : funcs) {
				double w = featureFunction.getWeight() + (1.0 / M) * Math.log(priorExpectations[count]/modelExpectation[count]);
				featureFunction.setWeight(w);
				count++;
			}
			//检查是否收敛
			boolean isC = isconvergent(oldWeight, epsilon);
			if(isC) {
				System.out.println("已收敛！");
				break;
			}
			//保存旧的权重
			count = 0;
			for(FeatureFunction featureFunction : funcs) {
				oldWeight[count++] = featureFunction.getWeight();
			}
		}
	}
	/**
	 * 是否收敛
	 * @param oldWeight
	 * @param epsilon
	 * @return
	 */
	public boolean isconvergent(double[] oldWeight, double epsilon) {
		int count = 0;
		for(FeatureFunction featureFunction : funcs) {
			if(Math.abs(featureFunction.getWeight() - oldWeight[count++]) >= epsilon)
				return false;
		}
		return true;
	}
	/**
	 * 计算先验期望
	 * @param rowNum
	 * @return
	 */
	public double[] priorExpectation(int rowNum) {
		double[] priorExpectations = new double[funcs.size()];
		int count = 0;
		for(FeatureFunction featureFunction : funcs) {
			priorExpectations[count++] = featureFunction.getWeight() / rowNum;
		}
		return priorExpectations;
	}
	/**
	 * 计算模型期望
	 * @param xs
	 * @return
	 */
	public double[] modelExpectation( List<String[]> xs) {
		int rowNum = xs.size();
		double[] expectations = new double[funcs.size()];
		for(int i = 0; i < rowNum; i++) {
			//获取当前样本的所有x特征
			String[] xss = xs.get(i);
			//计算p(y|x)
			double[] pyGivenx = predict(xss);
			for(String x : xss) {
				int xId = x2id.get(x);
				for(int j = 0; j < id2y.length; j++) {
					String funcId = FeatureFunction.generateFuncId(xId, j);
					Integer funcIndex = funcId2Int.get(funcId);
					if(funcIndex != null) {
						expectations[funcIndex] += pyGivenx[j] * (1.0 / rowNum);
					}
				}
			}
		}
		return expectations;
	}
	public double[] predict(String[] x) {
		int[] xid = new int[x.length];
		//有可能出现未知的特征，直接跳过
		for(int i = 0; i < x.length; i++) {
			Integer xIntid = x2id.get(x[i]);
			if(xIntid != null)
				xid[i] = xIntid;
		}
		return predict(xid);
	}
	
	/**
	 * 计算p(y|x)
	 * 由模型参数来预测不同标签的概率，返回一组概率值
	 * @param x
	 * @return
	 */
	public double[] predict(int[] x) {
		double[] prob = new double[id2y.length];
		//归一化因子
		double z = 0;
		for(int y = 0; y < id2y.length; y++) {
			List<FeatureFunction> cfuncs = getFeatureFunction(x, y);
			prob[y] = Math.exp(computeWeight(cfuncs));
			z += prob[y];
		}
		//归一化
		for(int i = 0; i < prob.length; i++) {
			prob[i] = prob[i] / z;
		}
		return prob;
	}
	/**
	 * 权重累加
	 * @param funcs
	 * @return
	 */
	public double computeWeight(List<FeatureFunction> funcs) {
		//特征函数权重累加
		double w = 0;
		for(FeatureFunction featureFunction : funcs) {
			w += featureFunction.getWeight();
		}
		return w;
	}
	/**
	 * 根据x和y获取相应的特征函数
	 * @param x
	 * @param y
	 * @return
	 */
	public List<FeatureFunction> getFeatureFunction(int[] x, int y){
		List<FeatureFunction> rfuncs = new ArrayList<>();
		for(int xx : x) {
			String funcId = FeatureFunction.generateFuncId(xx, y);
			//可能出现x不再特征函数中，这时候这里直接选择忽略
			Integer funcIndex = funcId2Int.get(funcId);
			if(funcIndex != null) {
				rfuncs.add(funcs.get(funcIndex));
			}
		}
		return rfuncs;
	}
	public float accuracy(int[] yTrue, int[] yPredict) {
		int count = 0;
		for(int i = 0; i < yTrue.length; i++) {
			if(yPredict[i] == yTrue[i]) count++;
		}
		return (float) (count*1.0 / yTrue.length);
	}
	
	public int[] labels2Id(String[] labels) {
		int[] yids = new int[labels.length];
		for(int i = 0; i < yids.length; i++) {
			yids[i] = y2id.get(labels[i]);
		}
		return yids;
	}
	public int[] predict(String[][] nSamples) {
		int[] res = new int[nSamples.length];
		for(int i = 0; i < res.length; i++) {
			double[] prob = predict(nSamples[i]);
			res[i] = maxProbabilityClass(prob);
		}
		return res;
	}
	//找最大概率的类别
	public int maxProbabilityClass(double[] prob) {
		double maxP = 0;
		int maxIndex = 0;
		for(int i = 0; i < prob.length; i++) {
			if(prob[i] > maxP) {
				maxP = prob[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
}
