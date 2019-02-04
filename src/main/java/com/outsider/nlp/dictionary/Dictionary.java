package com.outsider.nlp.dictionary;

import java.util.Collections;
import java.util.List;

import com.outsider.common.algorithm.dat.darts.DoubleArrayTrie;
import com.outsider.common.util.Storable;
import com.outsider.common.util.StorageUtils;
/**
 * 词典描述
 * @author outsider
 *
 */
public class Dictionary implements Storable{
	//词数量
	protected int wordNum;
	//双数组存储树
	protected DoubleArrayTrie dat;
	public Dictionary() {
	}
	/**
	 * 构造词典
	 * @param words 词数组
	 * @param isSorted 是否已经进行词排序
	 * @param encoding 编码 默认时utf-8
	 */
	public void build(List<String> words, boolean isSorted) {
		this.wordNum = words.size();
		if(!isSorted) {
			Collections.sort(words);
		}
		dat = new DoubleArrayTrie();
		dat.build(words);
	}
	
	public int getWordNum() {
		return wordNum;
	}
	
	public DoubleArrayTrie getDictionary() {
		return dat;
	}
	@Override
	public void save(String directory, String fileName) {
		StorageUtils.save(directory, fileName, this);
	}
	@Override
	public void open(String directory, String fileName) {
		StorageUtils.open(directory, fileName, this);
	}
	
}
