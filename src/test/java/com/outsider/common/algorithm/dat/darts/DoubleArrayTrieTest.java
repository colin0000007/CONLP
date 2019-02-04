package com.outsider.common.algorithm.dat.darts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.outsider.common.util.IOUtils;

public class DoubleArrayTrieTest {
	public static void main(String[] args) {
		/**
		 * 构建词典，必须按照字典序
		 */
		List<String> words = new ArrayList<String>();
		words = IOUtils.readTextAndReturnLines("D:\\nlp语料\\词性标注\\dictionary2014&1998.txt", "utf-8");
		for(int i = 0; i < words.size(); i++) {
			words.set(i, words.get(i).trim());
		}
		//186089
		Collections.sort(words);
		StringBuilder sb = new StringBuilder();
		for(String word : words) {
			sb.append(word+"\n");
		}
		IOUtils.writeTextData2File(sb.toString(), "D:\\\\nlp语料\\\\词性标注\\\\dic2014&1998.txt", "utf-8");
		System.out.println(words.get(0));
		System.out.println(words.get(words.size() - 1));
		System.out.println("字典词条：" + words.size());
		DoubleArrayTrie dat = new DoubleArrayTrie();
        System.out.println("是否错误: " + dat.build(words));
        //卧槽，必须按照字典序，不然报错
        System.out.println("前缀搜索测试:");
        String s = "上星期";
        List<Integer> intids = dat.commonPrefixSearch(s);
        System.out.println("搜索到前缀是"+"\""+s+"\""+"数量:"+intids.size());
        for(int i= 0; i < intids.size();i++) {
        	System.out.println(intids.get(i));
        }
        Set<Integer> ids = new HashSet<>();
        for(String word : words) {
        	ids.add(dat.exactMatchSearch(word));
        }
        
        //dat.save("C:\\Users\\outsider\\Desktop\\a");
        System.out.println("单词数量:"+words.size());
        System.out.println("id数量:"+ids.size());
        System.out.println("getSize():"+dat.getKeySize());
        System.out.println(dat.intIdOf(words.get(0)));
        System.out.println(dat.intIdOf(words.get(words.size() - 1)));
		/**
		 * 加载词典
		 */
		/*DoubleArrayTrie dat = new DoubleArrayTrie();
		dat.open("./model/dictionary/dic2014&1998dat");*/
	}
}
