package com.outsider.common.util;

public class MathUtils {
	
	public static final double INFINITY = (double) -Math.pow(2, 31);;

	public static double max(double[] arr) {
		double max = arr[0];
		for(int i = 1; i < arr.length;i++) {
			max = arr[i] > max ? arr[i] : max;
		}
		return max;
	}
	
	public static double sum(double[] arr) {
		double sum = 0;
		for(int i = 0; i < arr.length;i++) {
			sum += arr[i];
		}
		return sum;
	}
	
	/**
	 * logSum¼ÆËã¼¼ÇÉ
	 * @param logProbaArr
	 * @return
	 */
	public static double logSum(double[] logProbaArr) {
		if(logProbaArr.length == 0) {
			return INFINITY;
		}
		double max = MathUtils.max(logProbaArr);
		double result = 0;
		for(int i = 0; i < logProbaArr.length; i++) {
			result += Math.exp(logProbaArr[i] - max);
		}
		return max + Math.log(result);
	}
}
