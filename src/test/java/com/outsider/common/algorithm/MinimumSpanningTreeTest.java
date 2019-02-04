package com.outsider.common.algorithm;

public class MinimumSpanningTreeTest {
	
	public static void main(String[] args) {
		float[][] graph = new float[6][6];
		graph[0][1] = 6;
		graph[0][2] = 1;
		graph[0][3] = 5;
		graph[1][0] = 6;
		graph[2][0] = 1;
		graph[3][0] = 5;
		
		graph[1][2] = 5;
		graph[1][4] = 3;
		graph[2][1] = 5;
		graph[4][1] = 3;
		
		graph[2][3] = 5;
		graph[2][4] = 6;
		graph[2][5] = 4;
		graph[3][2] = 5;
		graph[4][2] = 6;
		graph[5][2] = 4;
		
		graph[3][5] = 2;
		graph[5][3] = 2;
		
		graph[4][5] = 6;
		graph[5][4] = 6;
		
		for(int i = 0; i < graph.length; i++) {
			for(int j = 0; j < graph.length; j++) {
				if(graph[i][j] == 0) {
					graph[i][j] = Float.MAX_VALUE;
				}
			}
		}
		
		int[] mst = MinimumSpanningTree.primAlgorithm(graph);
		System.out.println("最小生成树边集:");
		for(int i = 0; i < mst.length; i++) {
			if(mst[i] != - 1) {
				System.out.println((mst[i]+1)+"<--->"+(i+1));
			}
		}
	}
}
