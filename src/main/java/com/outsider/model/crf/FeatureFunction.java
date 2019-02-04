package com.outsider.model.crf;

import java.io.Serializable;

import com.outsider.common.util.Storable;
import com.outsider.common.util.StorageUtils;

/**
 * 描述特征函数
 * 但是注意：不是只对应一个特征函数
 * 比如：U01:%x[0,0]
 * 		如果当前扫描的行是(分词举例): 我    S
 * 		那么产生的特征函数是
 * 		if(x=='我'&&y==0) return 1 else 0;
 * 		if(x=='我'&&y==1)....
 * 		if(x=='我'&&y==2)....
 * 		if(x=='我'&&y==3)....
 * 上面特征函数的区别仅仅是标签不同，
 * 所以下面的特征函数按照标签的id顺序保存一组权重
 * 如何存储存储特征函数并且快速索引，这里参照HanLP中用字典树存储，
 * 但是我这个项目里字典树只能存储字符，所以下面要覆盖toString方法
 * 为了使每组特征函数的产生的字符串不同方便用字典树存储，
 * 这里直接拼接观测的int值作为字符串的值
 * 
 * err：
 *  U:%x[-2,0]和U:%x[0,0]这2个如果仅仅依靠观测值是否相同会出问题。。
 *  为了区分，equals中还需要比较该特征函数产生于哪一个特征模板，也就是 Uxx
 *  
 *  
 *  由于没有覆盖hashCode，导致明明是相同的特征函数却认为是不同的
 * @author outsider
 *
 */
public class FeatureFunction implements Storable,Comparable<FeatureFunction>,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//数组索引对应于标签的id
	private double[] weight;
	/**
	 * 对应观测序列的索引或者id数组
	 * 是数组因为存在U01"%x[0,0]/%x[-1,0]这种情况有多个观测
	 */
	private int[] x;
	//特征函数的字符id，方便在字典树中存储
	private String strId;
	//保存来自于哪个特征模板，也就是前缀Uxx
	//private String featureTemplatePrefix;
	//特征模板的编号需要解析出来遍历时需要用到
	//可以替代前面的featureTemplatePrefix，后面才来考虑吧
	private short featureTemplateNum;
	/*public FeatureFunction(short templateNumber) {
		this.featureTemplateNum = templateNumber;
	}*/
	public short getFeatureTemplateNum() {
		return featureTemplateNum;
	}
	
	public String getStrId() {
		return strId;
	}
	public FeatureFunction(int[] x, String funcId, short templateNumber) {
		this.x = x;
		this.strId = funcId;
		this.featureTemplateNum = templateNumber;
		//产生字符id
	}
	public FeatureFunction(int[] x, short templateNumber) {
		this.featureTemplateNum = templateNumber;
		this.strId = generateFeatureFuncStrId(x, templateNumber);
	}
	public FeatureFunction(String funcId, short templateNumber) {
		this.strId = funcId;
		this.featureTemplateNum = templateNumber;
	}
	
	@Override
	public String toString() {
		return strId;
	}
	public double[] getWeight() {
		return weight;
	}
	public int[] getX() {
		return x;
	}
	public void setX(int[] x) {
		this.x = x;
	}
	public void setWeight(double[] weight) {
		this.weight = weight;
	}
	@Override
	public boolean equals(Object obj) {
		FeatureFunction f2 = (FeatureFunction) obj;
		if(this.strId.equals(f2.strId))
			return true;
		return false;
	}

	@Override
	public int compareTo(FeatureFunction o) {
		FeatureFunction f2 = o;
		if(this.strId.equals(f2.strId))
			return 0;
		return -1;
	}
	@Override
	public int hashCode() {
		int hash = 0;
		for(int i = 0; i < x.length;i++) {
			hash += x[i];
		}
		return hash + strId.hashCode();
	}
	
	/**
	 * 根据特征模板号和观测x生成特征函数的id
	 * @param x 观测
	 * @param templateNumber 特征模板号 
	 * @return str Id
	 */
	public static String generateFeatureFuncStrId(int[] x, short templateNumber) {
		StringBuilder sb = new StringBuilder();
		//需要加入前缀Uxx:的编号不然无法区分
		sb.append(templateNumber+":");
		for(int i = 0;i < x.length;i++) {
			sb.append(x[i]);
		}
		return sb.toString();
	}

	@Override
	public void save(String directory, String fileName) {
		StorageUtils.save(directory, fileName, this);
	}

	@Override
	public void open(String directory, String fileName) {
		StorageUtils.open(directory, fileName, this);
	}
	
}
