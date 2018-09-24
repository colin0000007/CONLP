package com.outsider.common.util;

import java.util.Arrays;

import org.junit.Test;

public class StringUtils {
	/**
	 * 连接2个字符串数组
	 * @param arr1
	 * @param arr2
	 * @return
	 */
	public static String[] concat(String[] arr1, String[] arr2) {
        int strLen1 = arr1.length;// 保存第一个数组长度
        int strLen2 = arr2.length;// 保存第二个数组长度
        arr1 = Arrays.copyOf(arr1, strLen1 + strLen2);// 扩容
        System.arraycopy(arr2, 0, arr1, strLen1, strLen2);// 将第二个数组与第一个数组合并
        // System.out.println(Arrays.toString(arr1));// 输出数组
        return arr1;
	}
}
