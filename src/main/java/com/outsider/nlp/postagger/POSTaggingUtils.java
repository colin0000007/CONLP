package com.outsider.nlp.postagger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.outsider.common.algorithm.dat.darts.DoubleArrayTrie;
import com.outsider.common.util.IOUtils;
import com.outsider.common.util.StorageUtils;
import com.outsider.constants.nlp.PathConstans;
import com.outsider.model.hmm.SequenceNode;

public class POSTaggingUtils {
	/**
	 * 将单词转换为整型id
	 * @param words 单词数组
	 * @param dictionary 字典树
	 * @return 整型id数组
	 */
	public static int[] words2intId(String[] words, DoubleArrayTrie dictionary) {
		int[] intId = new int[words.length];
		for(int i = 0; i < intId.length; i++) {
			intId[i] = dictionary.intIdOf(words[i]);
		}
		return intId;
	}
	
	/**
	 * 词性转换为intId
	 * @param natures 词性数组
	 * @param mapping 词性和int的映射对象
	 * @return intId数组
	 */
	public static int[] natures2intId(String[] natures, WordNatureMapping mapping) {
		int[] res = new int[natures.length];
		for(int i = 0; i < res.length; i++) {
			Integer va = mapping.natureName2int(natures[i]);
			if(va == null) {
				System.out.println(natures[i]);
				continue;
			}
			res[i] = mapping.natureName2int(natures[i]);
		}
		return res;
	}
	
	/**
	 * 构建字典树
	 * @param words
	 * @return
	 */
	public static DoubleArrayTrie generateDictionary(List<String> words) {
		Collections.sort(words);
		DoubleArrayTrie dat = new DoubleArrayTrie<>();
		dat.build(words);
		return dat;
	}
	
	public static DoubleArrayTrie getDefaultDictionary() {
		DoubleArrayTrie dat = new DoubleArrayTrie<>();
		try {
			dat.open(PathConstans.DIC_4_POSTAGGER);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dat;
	}
	
	public static List<SequenceNode> generateNodesWithCRFormatData(String path, String encoding, int wordColumnIndex, int natureColumnIndex
			,DoubleArrayTrie dictionary ,WordNatureMapping mapping){
		List<SequenceNode> nodes = new ArrayList<>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String line = null;
		try {
			while((line = reader.readLine()) != null) {
				String[] s = line.split("\t");
				int nodeIndex = dictionary.intIdOf(s[wordColumnIndex]);
				int state = mapping.natureName2int(s[natureColumnIndex]);
				SequenceNode node = new SequenceNode(nodeIndex, state);
				nodes.add(node);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nodes;
	}
	
	public static void main(String[] args) {
		//fileSize:5161300
		//size:645162
		List<String> words = IOUtils.readTextAndReturnLines("D:\\nlp语料\\词性标注\\dictionary2014&1998.txt", "utf-8");
		DoubleArrayTrie dat = generateDictionary(words);
		try {
			dat.save(PathConstans.DIC_4_POSTAGGER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
