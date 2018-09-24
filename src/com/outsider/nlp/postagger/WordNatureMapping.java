package com.outsider.nlp.postagger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.outsider.common.algorithm.dat.darts.DoubleArrayTrie;
import com.outsider.common.util.IOUtils;
import com.outsider.common.util.nlp.ChineseDictionarySortUtil;
import com.outsider.constants.nlp.PathConstans;

/**
 * 忽视了一个很重要的东西
 * HMM中状态和观测的集合最后只能映射为一个从0到n这样一个值，
 * 因为这样才能好索引转移概率和状态向量等等
 * @author outsider
 *
 */
public class WordNatureMapping {
	private static Map<String,Integer> natureName2Int;
	private static String[] int2NatureName;
	public static int wordNatureNum  = 106;
	public static Map<String,Integer> getNatureName2IntMapping(String path) {
		if(natureName2Int == null) {
			//打开默认路径
			if(path == null)
				natureName2Int = (Map<String, Integer>) IOUtils.readObject(PathConstans.WORD_NATURE_2_INT_MAPPING);
			else
				natureName2Int = (Map<String, Integer>) IOUtils.readObject(path);
		}
		return natureName2Int;
	}
	
	public static String[] getInt2NatureNameMapping(String path) {
		if(int2NatureName == null) {
			//打开默认路径
			if(path == null)
				int2NatureName =  (String[]) IOUtils.readObject(PathConstans.INT_2_WORD_NATURE_MAPPING);
			else
				int2NatureName =  (String[]) IOUtils.readObject(path);
		}
		return int2NatureName;
	}
	/**
	 * 构造2个词性和int的相互映射代码
	 */
	/*public static void main(String[] args) {
		List<String> natures = IOUtils.readTextAndReturnLines("D:\\nlp语料\\词性标注\\wordNature.txt","utf-8");
		ChineseDictionarySortUtil.sortEncodingUtf8(natures);
		Map<String,Integer> natureName2Int = new HashMap<>();
		String[] int2NatureName = new String[natures.size()];
		int count = 0;
		for(String nature : natures ) {
			natureName2Int.put(nature, count);
			int2NatureName[count] = nature;
			count++;
		}
		IOUtils.writeObject2File("./model/dictionary/natureName2Int", natureName2Int);
		IOUtils.writeObject2File("./model/dictionary/int2NatureName", int2NatureName);
	}*/
}
