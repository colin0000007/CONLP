package com.outsider.test;

import org.junit.Test;

public class Corpus {
	static String basePath = "./data/";
	static String msrTest = basePath+"msr_test_gold.utf8.txt";
	static String pkutest = basePath+"pku_test_gold.utf8";
	static String skuTest = basePath+"sku_test.txt";
	static String ctb6Test = basePath+"ctb6.test.seg.txt";
	static String asTest = basePath+"as_test_gold.utf8.txt";
	static String[] testSet = new String[] {msrTest,pkutest,skuTest,ctb6Test,asTest};
	static String[] testSetNames = new String[] {"msrT","pkuT","skuT","ctb6T","asT"};
	static String[] testSetSplitChats = new String[] {"  ","  ","  "," "," "};
	//统计一下汉语词语平均长度
	@Test
	public void avg() {
		
	}
}
