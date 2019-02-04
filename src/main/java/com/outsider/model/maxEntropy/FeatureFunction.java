package com.outsider.model.maxEntropy;

/**
 * 最大熵模型的特征函数
 * @author outsider
 */
public class FeatureFunction {
	public int x;
	public int y;
	//权重
	public double weight;
	//特征函数id
	public String funcId;
	
	public FeatureFunction(int x, int y) {
		this.x = x;
		this.y = y;
		funcId = generateFuncId(x, y);
	}
	public FeatureFunction(int x, int y, double weight) {
		this(x, y);
		this.weight = weight;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public double getWeight() {
		return weight;
	}
	@Override
	public String toString() {
		return funcId;
	}
	public static String generateFuncId(int x, int y) {
		return x+"_"+y;
	}
}
