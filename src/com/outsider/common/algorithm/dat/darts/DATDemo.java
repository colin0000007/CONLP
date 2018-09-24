package com.outsider.common.algorithm.dat.darts;

import java.io.IOException;

public class DATDemo {
	/**
	 * 仅供测试使用
	 * @param args
	 * @throws IOException
	 */
	@Deprecated
	public static void main(String[] args) throws IOException {
		/**
		 * 构建词典，必须按照字典序
		 */
		/*List<String> words = new ArrayList<>();
		words = IOUtils.readTextAndReturnLines("D:\\nlp语料\\词性标注\\dictionary2014&1998.txt", "utf-8");
		ChineseDictionarySortUtil.sortEncodingUtf8(words);
		System.out.println("字典词条：" + words.size());
		DoubleArrayTrie dat = new DoubleArrayTrie();
        System.out.println("是否错误: " + dat.build(words));
        //卧槽，必须按照字典序，不然报错
        Set<Integer> ids = new HashSet<>();
        for(String word : words) {
        	ids.add(dat.exactMatchSearch(word));
        }
        dat.save("./model/dictionary/dic2014&1998dat");
        System.out.println(dat.exactMatchSearch("我"));
        System.out.println("单词数量:"+words.size());
        System.out.println("id数量:"+ids.size());*/
		/**
		 * 加载词典
		 */
		/*DoubleArrayTrie dat = new DoubleArrayTrie();
		dat.open("./model/dictionary/dic2014&1998dat");*/
	}
}
