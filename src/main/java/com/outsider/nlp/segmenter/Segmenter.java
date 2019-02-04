package com.outsider.nlp.segmenter;

import java.util.ArrayList;
import java.util.List;

import com.outsider.common.util.Storable;
import com.outsider.model.SequenceModel;
/**
 * 公共的分词接口
 * 这里继承SequenceModel是为了暴露SequenceModel中的方法
 * @author outsider
 *
 */
public interface Segmenter extends Storable,SequenceModel, SegmentationPredictor{
	/**
	 * 单份语料训练，
	 * @param words 已经分好词的词数组
	 */
	void train(String[] words);
	/**
	 * 单份语料训练，指定自定义的数据转换器
	 * 如果指定了转换器，那么就必须指定对应的解码器
	 * 所以此方法不可行
	 * 废除
	 * @deprecated
	 * @param words
	 * @param converter
	 */
	//void train(String[] words, DataConverter converter);
	/**
	 * 多份语料训练
	 * 由于在SequenceModel种存在这同名方法参数为List<SequenceNode>，造成了冲突
	 * 这里只有暂时妥协为ArrayList
	 * @param multiWords
	 */
	void train(ArrayList<String[]> corpuses);
	/**
	 * 多份语料，指定转换器
	 * 废除理由同上
	 * @deprecated
	 * @param multiWords
	 * @param converter 
	 */
	//void train(List<String[]> multiWords, DataConverter converter);
}
