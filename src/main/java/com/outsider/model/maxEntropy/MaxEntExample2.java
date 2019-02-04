package com.outsider.model.maxEntropy;

import java.util.Arrays;

import com.outsider.common.dataStructure.Table;
import com.outsider.common.util.IOUtils;

/**
 * 最大熵模型示例2
 * minst手写识别体
 * @author outsider
 *
 */
public class MaxEntExample2 {
	public static void main(String[] args) {
		String train_path = "C:\\Users\\outsider\\Desktop\\train2_train.csv";
		String test_path = "C:\\Users\\outsider\\Desktop\\train2_test.csv";
		String data = IOUtils.readTextWithLineCheckBreak(train_path, "gbk");
		Table table = Table.generateTable(data, ",");
		String data2 = IOUtils.readTextWithLineCheckBreak(test_path, "gbk");
		Table table2 = Table.generateTable(data2, ",");
		String[][] test = new String[table2.getRowNum()][table2.getColumnNum()- 1];
		String[] yTrue = new String[table2.getRowNum()];
		for(int i = 0; i < table2.getRowNum(); i++) {
			String[] row = table2.getDataOfOneRow(i);
			yTrue[i] = row[0];
			System.arraycopy(row, 1, test[i], 0, row.length - 1);
		}
		//由于像素不同维度的值都是在0-255，造成了所有维度的特征集处理为0-255，为了区分，这里要先处理为每个维度的加个唯一标识
		int[] x = new int[784];
		for(int i = 0; i < x.length; i++) {
			x[i] = i+1;
		}
		//处理特征
		String[][] train = table.getTable();
		train = addFeatureIdentification(train);
		table.setTable(train);
		test = addFeatureIdentification(test);
		//row,col:42000,785
		int y = 0;
		MaxEnt maxEnt = new MaxEnt();
		int maxIter = 20;
		double epsilon = 1e-10;
		maxEnt.train(table, x, y, 20, epsilon);
		int[] yPre = maxEnt.predict(test);
		int[] yTrueInt = maxEnt.labels2Id(yTrue);
		float acc = maxEnt.accuracy(yTrueInt, yPre);
		System.out.println("acc:"+acc);
		/**
		 *  M:785
			epsilon:1.0E-10
			maxIter:20
			Amount of FeatureFunction:132820
			Amout of Label:10
			train data.shape:34999,784
			iter...0
			iter...1
			iter...2
			iter...3
			iter...4
			iter...5
			iter...6
			iter...7
			iter...8
			iter...9
			iter...10
			iter...11
			iter...12
			iter...13
			iter...14
			iter...15
			iter...16
			iter...17
			iter...18
			iter...19
			acc:0.8354286
		 */
	}
	
	
	//为每个维度的特征添加唯一标识符，因为像素值的取值范围不同，不加的话，维度基本上退化为一维
	public static String[][] addFeatureIdentification(String[][] samples){
		int dimension = samples[0].length;
		int n = samples.length;
		int j = 0;
		int start = 0;
		if (dimension == 785) start = 1;
		int count = 0;
		for(int i =0; i < n; i++) {
			count = 0;
			for(j = start; j < dimension; j++) {
				samples[i][j] = (count++)+"_"+samples[i][j];
			}
		}
		return samples;
	}
}
