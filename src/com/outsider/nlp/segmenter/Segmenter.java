package com.outsider.nlp.segmenter;

import java.util.ArrayList;
import java.util.List;

import com.outsider.common.util.StringUtils;
import com.outsider.model.Model;
import com.outsider.model.data.DataConverter;
import com.outsider.model.data.SegmenterDataConverter;
import com.outsider.model.hmm.SequenceNode;

/**
 * 统一的分词器接口，为了方便以后写crf分词扩展
 * 以及对外提供统一的接口方便调用
 * @author outsider
 *
 */
public interface Segmenter extends Model<List<SequenceNode>, Object, int[], int[]>{
	final DataConverter<String[], List<SequenceNode>> converter = SegmenterDataConverter.getInstance();
	/**
	 * 将多个句子转换为观测集合中的索引，就是把字符转换为数字
	 * @param sentences 多个句子集合
	 * @return
	 */
	default List<int[]> sentence2ObservationIndex(String[] sentences) {
		List<int[]> res = new ArrayList<>();
		for(int i = 0; i < sentences.length;i++) {
			int[] O = new int[sentences[i].length()];
			for(int j = 0; j < sentences[i].length();j++) {
				O[j] = sentences[i].charAt(j);
			}
			res.add(O);
		}
		return res;
	}
	/**
	 * 预测句子的状态序列
	 * @param sentence 句子
	 * @return 状态序列
	 */
	default int[] predict(String sentence) {
		int[] O = sentence2ObservationIndex(new String[] {sentence}).get(0);
		return predict(O);
	}
	/**
	 * 输入句子直接返回分词结果
	 * @param sentence
	 * @return
	 */
	default String[] predictAndReturnTerms(String sentence) {
		int[] predict = predict(sentence);
		return decode(predict, sentence);
	}
	/**
	 * 输入多组句子
	 * @param sentences
	 * @return 返回预测结果0123 bmes
	 */
	default List<int[]> predict(String[] sentences){
		List<int[]> predictions = new ArrayList<>();
		List<int[]> observations = sentence2ObservationIndex(sentences);
		for(int i = 0; i< observations.size(); i++) {
			predictions.add(predict(observations.get(i)));
		}
		return predictions;
	}
	/**
	 * 输入多组句子返回结果
	 * @param sentences
	 * @return
	 */
	default List<String[]> predictAndReturnTerms(String[] sentences){
		List<int[]> predictions = predict(sentences);
		List<String[]> res = new ArrayList<>();
		for(int i =0 ; i < predictions.size(); i++) {
			res.add(decode(predictions.get(i), sentences[i]));
		}
		return res;
	}
	
	/**
	 * 
	 * @param terms
	 */
	default void train(String[] terms) {
		List<SequenceNode> nodes = converter.rawData2ConvertedData(terms);
		train(nodes);
	}
	/**
	 * 直接传入训练语料
	 * @param corpus 语料
	 * @param splitChar 分割字符
	 */
	default void train(String corpus, String splitChar) {
		List<SequenceNode> nodes = converter.rawData2ConvertedData(corpus.split(splitChar));
		train(nodes);
	}
	
	/**
	 * 传入多分语料
	 * @param corpuses 多份语料
	 * @param splitChars 分割字符
	 */
	default void train(String[] corpuses, String[] splitChars) {
		String[] s = new String[0];
		for(int i = 0; i < corpuses.length; i++) {
			String[] co = corpuses[i].split(splitChars[i]);
			StringUtils.concat(s, co);
		}
		List<SequenceNode> nodes = converter.rawData2ConvertedData(s);
		train(nodes);//底层训练方法需要实现
	}
	/*default List<SequenceNode> corpus2SequenceNodes(Object object, int segmentionMode, String splitChar){
		if(CorpusMode.SEGMENTION_ARTICLE == segmentionMode) {
			String article = (String) object;
			return converter.rawData2ConvertedData(article.split(splitChar));
		} else if (CorpusMode.SEGMENTION_SENTENCES == segmentionMode) {
			String[] sentences = (String[]) object;
			//出问题了，无论这样抓换后还是为被连接成一个序列，核心是要处理统计参数
		}
		return null;
	}*/
	/**
	 * 将预测结果和句子进行解码成一个个词
	 * @param code
	 * @param sentence
	 * @return
	 */
	default String[] decode(int[] predict, String sentence) {
		List<String> res = new ArrayList<>();
		char[] chars = sentence.toCharArray();
		for(int i = 0; i < predict.length;i++) {
			if(predict[i] == 0 || predict[i] == 1) {
				int a = i;
				while(predict[i] != 2) {
					i++;
					if(i == predict.length) {
						break;
					}
				}
				int b = i;
				if(b == predict.length) {
					b--;
				}
				res.add(new String(chars,a,b-a+1));
			} else {
				res.add(new String(chars,i,1));
			}
		}
		String[] s = new String[res.size()];
		return res.toArray(s);
	}
	
	public void initParameter();
}
