package com.outsider.nlp.dictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.outsider.common.util.IOUtils;

/**
 * 词性标注词典
 * 未使用
 * @author outsider
 *
 */
public class POSTaggerDictionary extends Dictionary{
	//词性数量
	private int wordNatureNum;
	//词性名字到int的映射
	private Map<String,Integer> natureName2Int;
	//int到词性的映射
	private String[] int2NatureName;
	public POSTaggerDictionary() {
	}
	
	public int getWordNatureNum() {
		return wordNatureNum;
	}


	public Map<String, Integer> getNatureName2Int() {
		return natureName2Int;
	}


	public String[] getInt2NatureName() {
		return int2NatureName;
	}


	/*
	 * 构建词性映射
	 */
	public void buildWordNature(String[] natures) {
		this.wordNatureNum = natures.length;
		natureName2Int = new HashMap<>();
		int2NatureName = new String[natures.length];
		for(int i = 0; i < natures.length; i++) {
			natureName2Int.put(natures[i], i);
			int2NatureName[i] = natures[i];
		}
	}
	/**
	 * 获取词性名到int的映射
	 * @return
	 */
	/*public Map<String,Integer> getNatureName2IntMapping() {
		return natureName2Int;
	}*/
	/**
	 * 获取int到词性名的映射
	 * @return
	 */
	/*public String[] getInt2NatureNameMapping() {
		return int2NatureName;
	}*/
	public static void main(String[] args) {
		List<String> words = IOUtils.readTextAndReturnLines("D:\\nlp语料\\词性标注\\dictionary2014&1998.txt", "utf-8");
		String[] natures = IOUtils.readTextAndReturnLinesOfArray("D:\\nlp语料\\词性标注\\wordNature.txt", "utf-8");
		//构建测试
		POSTaggerDictionary dictionary = new POSTaggerDictionary();
		//dictionary.build(words, true, null);
		//dictionary.buildWordNature(natures);
		//dictionary.save("D:\\nlp语料\\词性标注",null);
		dictionary.open("D:\\nlp语料\\词性标注", null);
		int a  = dictionary.getDictionary().intIdOf("你好");
		System.out.println(a);
		//出问题了，在子类中获取字段不能获取到父类的字段，需要更新
	}
}
