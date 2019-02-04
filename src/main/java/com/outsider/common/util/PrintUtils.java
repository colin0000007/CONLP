package com.outsider.common.util;

public class PrintUtils {
	
	/**
	 * 基于bmes状态的分词打印机
	 * @param predict 预测结果
	 * @param sequence 原始数据
	 */
	public static void segmenterPrinter(int[] predict,String sequence) {
		String data = sequence.trim();
		for(int i = 0; i < predict.length;i++) {
			if(predict[i] == 2 || predict[i] == 3) {
				System.out.print(data.charAt(i)+"|");
			} else {
				System.out.print(data.charAt(i));
			}
		}
	}
	
	public static void segmenterPrinter2(int[] predict,String sentence) {
		for(int i = 0; i < predict.length;i++) {
			if(predict[i] == 0 || predict[i] == 1) {
				int a = i;
				int b = i;
				while(predict[i] != 2) {
					i++;
					b++;
					if(i == predict.length) {
						b--;
						break;
					}
				}
				for(int j = a; j <= b;j++) {
					System.out.print(sentence.charAt(j));
				}
				System.out.print("|");
			} else if(predict[i] == 2 || predict[i]==3) {
				System.out.print(sentence.charAt(i)+"|");
			}
		}
	}
	
}
