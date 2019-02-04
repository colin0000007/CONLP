package com.outsider.common.algorithm;


/**
 * 最小生成树
 * @author outsider
 * 
 */

public class MinimumSpanningTree {
	/**
	 * 算法实现
	 * @param adjacentMatrix 邻接矩阵，要求不存在的边权重置为Float.MAX_VALUE
	 * @return 一个数组，下标表示终点，对应的值表示起点，这些起点和终点构成的边集就是最小生成树的边集，若不存在起点那么数组中的
	 * 值为-1
	 */
	public static int[] primAlgorithm(float[][] adjacentMatrix){
		int vn = adjacentMatrix.length;
		float[] cost = new float[vn];//记录已经加入到mst中的点到其他点之间的据花费，会被更新
		int[] mst = new int[vn];//记录cost中点的起点
		boolean[] V = new boolean[vn];//记录节点是否已经在mst中
		//默认选择节点0作为起点
		//初始化
		for(int i = 0; i < vn; i++) {
			if(adjacentMatrix[0][i] == Float.MAX_VALUE) {
				cost[i] = Float.MAX_VALUE;
				mst[i] = -1;
			} else {
				cost[i] = adjacentMatrix[0][i];
				mst[i] = 0;
			}
		}
		V[0] = true;
		//vn - 1次循环，每次找出一个点到mst中
		for(int i = 0; i < vn - 1; i++) {
			//找出权重最小的点加入到v集中
			int min = -1;
			float minCost = Float.MAX_VALUE;
			for(int j = 1; j < vn; j++) {
				if(!V[j]) {
					if(cost[j] < minCost) {
						minCost = cost[j];
						min = j;
					}
				}
			}
			//加入V集
			V[min] = true;
			//更新cost，新加入到V集中的点可能存在更短的路径达到未加入到v集中的哪些点
			for(int j = 1; j < vn; j++) {
				if(!V[j] && adjacentMatrix[min][j] < cost[j]) {
					cost[j] = adjacentMatrix[min][j];
					mst[j] = min;
				}
			}
		}
		return mst;
	}
	
}
