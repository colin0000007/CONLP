package com.outsider.test;

import java.util.ArrayList;
import java.util.List;

public class ArrayTest {
	public static void main(String[] args) {
		List<String> strList = new ArrayList<>();
		for(int i = 0; i < 5000000; i++) {
			strList.add(Math.random()*100+"");
		}
		toArray1(strList);
		toArray2(strList);
		//十万这个级别循环复制更差
		//上百万级别，循环复制要好一些
		//测试结果 一千万这个级别，循环复制反而更快
		
	}
	
	public static void toArray1(List<String> strList) {
		long start = System.currentTimeMillis();
		String[] arr = new String[strList.size()];
		strList.toArray(arr);
		long end = System.currentTimeMillis();
		System.out.println("使用List自带的复制数组，耗时:"+(end - start)+"毫秒");
	}
	public static void toArray2(List<String> strList) {
		long start = System.currentTimeMillis();
		String[] arr = new String[strList.size()];
		for(int i = 0; i < strList.size(); i++) {
			arr[i] = strList.get(i);
		}
		long end = System.currentTimeMillis();
		System.out.println("循环复制数组，耗时:"+(end - start)+"毫秒");
	}
}
