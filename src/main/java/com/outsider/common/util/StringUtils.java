package com.outsider.common.util;

import java.util.Arrays;

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
	
	
	/**
	 * 全角符号转换为半角符号
	 * @param fullWidthStr
	 * @return
	 */
	public static String fullWidthChar2HalfWidthChar(String fullWidthStr) {
		if (null == fullWidthStr || fullWidthStr.length() <= 0) {
            return "";
        }
        char[] charArray = fullWidthStr.toCharArray();
        //对全角字符转换的char数组遍历
        for (int i = 0; i < charArray.length; ++i) {
            int charIntValue = (int) charArray[i];
            //如果符合转换关系,将对应下标之间减掉偏移量65248;如果是空格的话,直接做转换
            if (charIntValue >= 65281 && charIntValue <= 65374) {
                charArray[i] = (char) (charIntValue - 65248);
            } else if (charIntValue == 12288) {
                charArray[i] = (char) 32;
            }
        }
        return new String(charArray);
	}
	
	public static void main(String[] args) {
		String[] s1 = new String[] {"1"};
		String[] s2 = new String[] {"2"};
		s1 = concat(s1, s2);
		System.out.println(Arrays.toString(s1));
	}
}
