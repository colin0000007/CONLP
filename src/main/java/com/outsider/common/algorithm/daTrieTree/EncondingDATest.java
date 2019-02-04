package com.outsider.common.algorithm.daTrieTree;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.outsider.common.util.IOUtils;

public class EncondingDATest extends DATrieTree implements EncodingDATrieTreeInterface{
	private Map<Integer, Integer> unicode2smallCode;
	private List<Character> smallCode2unicode;
	
	public EncondingDATest() {
		super();
	}
	@Override
	public int code(char c) {
		return unicode2smallCode.get((int)c);
	}
	@Override
	public void build(List<String> tokens) {
		init(tokens);
		super.build(tokens);
	}
	private void init(List<String> tokens) {
		Object[] objs = encodeFromZero(tokens);
		unicode2smallCode = (Map<Integer, Integer>) objs[0];
		smallCode2unicode = (List<Character>) objs[1];
	}
	public static void main(String[] args) {
		//测试
		DATrieTree da = new EncondingDATest();
		List<String> words = IOUtils.readTextAndReturnLines("D:\\nlp语料\\中文词库\\data\\四十万汉语大词库.txt", "utf-8");
		long start = System.currentTimeMillis();
		System.out.println("词条数:"+words.size());
		//words = words.subList(0, 5000);
		da.build(words);
		long end = System.currentTimeMillis();
		System.out.println("构建用时:"+(end - start) / 1000.0 + "秒！");
		words.forEach((e)->{
			if(da.exist(e)) {
				count++;
			} else {
				System.out.println(e);
			}
		});
		if(count!=words.size()) {
			System.out.println("存在训练词在词典种无法找到！");
			System.out.println("能找到:"+count+";总共:"+words.size());
		}
		System.out.println("idOf:"+da.idOf("哀伤"));
		count = 0;
		//测试id是否会重复
		Set<String> set = new TreeSet<>();
		words.forEach((e)->{
			set.add(da.idOf(e));
		});
		System.out.println("空间使用率:"+da.spaceUsingRate());
		System.out.println(set.size());
		System.out.println(words.size());
		//可不可以尝试开启n个线程来构建？n表示第一层的节点，因该要好些
	}
}
