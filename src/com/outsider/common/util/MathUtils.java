package com.outsider.common.util;

public class MathUtils {
	
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
}
