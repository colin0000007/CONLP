package com.outsider.nlp.postagger;

import java.util.List;
import java.util.Map;

import com.outsider.common.algorithm.dat.darts.DoubleArrayTrie;
import com.outsider.model.Model;
import com.outsider.model.data.POSTaggerDataConverter;
import com.outsider.model.hmm.SequenceNode;
/**
 * 
 * @author outsider
 *
 */
public interface POSTagger extends Model<List<SequenceNode>, Object, int[], int[]>{
	DoubleArrayTrie dictionary = PKUCorpusDictionary.getDictionary();
	POSTaggerDataConverter dataConverter = new POSTaggerDataConverter(dictionary);
	static Map<String,Integer> natureName2Int = WordNatureMapping.getNatureName2IntMapping(null);
	static String[] int2NatureName = WordNatureMapping.getInt2NatureNameMapping(null);
	/*
	 * 词性标注时若遇见数字直接标注，不需要进行序列标注，若遇见英语直接标注
	 */
	default void train(String data, String splitChar) {
		List<SequenceNode> nodes = dataConverter.rawData2ConvertedData(data, splitChar);
		this.train(nodes);
	}
	/**
	 * 将词转换为整型ID
	 * @param words
	 * @return
	 */
	default int[] observation2Int(String[] words) {
		int[] intId = new int[words.length];
		for(int i = 0; i < intId.length; i++) {
			intId[i] = dictionary.intIdOf(words[i]);
		}
		return intId;
	}
	
	/*default String[] wordNatureIntId2Str(int[] wordNatureIntIds) {
		String[] 
	}*/
	/**
	 * 预测结果直接返回数组，不便于后期处理，元素例如： 你/z
	 * @param words 需要预测的单词序列
	 * @return
	 */
	default String[] predictAndReturnStr(String[] words) {
		int[] intIds = observation2Int(words);
		/**
		 * 这里传入words是为了需要修正里面出现的数字类型和英语类型的标注问题
		 * 原始train方法将在Tagger中重写
		 */
		int[] result = this.predict(intIds, words);
		for(int i = 0; i < words.length; i++) {
			try {
				String s = int2NatureName[result[i]];
			} catch (Exception e) {
				System.out.println("result[i]:"+result[i]);
				System.out.println(int2NatureName.length);
				System.exit(0);
			}
			words[i] = words[i]+"/"+ int2NatureName[result[i]];
		}
		return words;
	}
	/**
	 * 直接返回预测节点数组，方便后期处理
	 * @param words
	 * @return
	 */
	default TaggingResultNode[] predictAndReturnNodes(String[] words) {
		int[] intIds = observation2Int(words);
		/**
		 * 这里传入words是为了需要修正里面出现的数字类型和英语类型的标注问题
		 * 原始train方法将在Tagger中重写
		 */
		int[] result = this.predict(intIds, words);
		TaggingResultNode[] nodes = new TaggingResultNode[intIds.length];
		for(int i = 0; i < words.length; i++) {
			nodes[i] = new TaggingResultNode(words[i], int2NatureName[result[i]]);
		}
		return nodes;
	}
}
