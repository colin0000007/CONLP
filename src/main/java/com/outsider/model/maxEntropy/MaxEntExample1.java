package com.outsider.model.maxEntropy;

import com.outsider.common.dataStructure.Table;
import com.outsider.common.util.IOUtils;

/**
 * 最大熵模型示例1
 * @author outsider
 *
 */
public class MaxEntExample1 {
	public static void main(String[] args) {
		String data = IOUtils.readTextWithLineCheckBreak("C:\\eclipse\\ArtificialIntelligenceSpace\\CONLPV2\\src\\main\\java\\com\\outsider\\model\\maxEntropy\\data.txt", "utf-8");
		Table table = Table.generateTable(data, " ");
		MaxEnt ent = new MaxEnt();
		int[] xColumn = new int[] {1,2,3};
		int yColumn = 0;
		int maxIter = -1;
		double epsilon = 0.001;
		//训练
		ent.train(table, xColumn, yColumn, maxIter, epsilon);
		//测试
		double[] prob = ent.predict(new String[] {"Sunny","Humid"});
		//结果，一组概率
		for(int i = 0; i < prob.length; i++) {
			System.out.println(ent.yName(i)+":"+prob[i]);
		}
	}
}
