package com.outsider.common.util.nlp;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChineseDictionarySortUtil {
	public static void sortEncodingUtf8(List<String> words) {
		Collections.sort(words, new ChineseComparator("utf-8"));
	}
	public static void sortEncodingGBK(List<String> words) {
		Collections.sort(words, new ChineseComparator("GB2312"));
	}
	/**
	 * 词比较器
	 * @author outsider
	 *
	 */
	public static class ChineseComparator implements Comparator<String>{
		private String encoding ;
		public ChineseComparator(String encoding) {
			this.encoding = encoding;
		}
		@Override
		public int compare(String o1, String o2) {
			try {
			   // 取得比较对象的汉字编码，并将其转换成字符串
			   String s1 = new String(o1.toString().getBytes(encoding), "ISO-8859-1");
			   String s2 = new String(o2.toString().getBytes(encoding), "ISO-8859-1");
			   // 运用String类的 compareTo（）方法对两对象进行比较
			   return s1.compareTo(s2);
			  } catch (Exception e) {
			   e.printStackTrace();
			  }
			  return 0;
		}
	}
}
