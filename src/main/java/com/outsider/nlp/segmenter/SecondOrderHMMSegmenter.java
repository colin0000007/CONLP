package com.outsider.nlp.segmenter;

import java.util.ArrayList;
import java.util.List;

import com.outsider.model.data.DataConverter;
import com.outsider.model.data.SegmentationDataConverter;
import com.outsider.model.hmm.SecondOrderGeneralHMM;
import com.outsider.model.hmm.SequenceNode;

public class SecondOrderHMMSegmenter extends SecondOrderGeneralHMM implements Segmenter{

	public SecondOrderHMMSegmenter(int stateNum, int observationNum) {
		super(stateNum, observationNum);
	}
	public SecondOrderHMMSegmenter() {
		super(4, 65536);
	}
	public SecondOrderHMMSegmenter(int stateNum, int observationNum, double[] pi, double[][] transfer_probability1,
			double[][] emission_probability) {
		super(stateNum, observationNum, pi, transfer_probability1, emission_probability);
	}
	
	@Override
	public void train(String[] words) {
		//使用默认的转换器
		DataConverter<String[], List<SequenceNode>> converter = new SegmentationDataConverter();
		List<SequenceNode> nodes = (List<SequenceNode>) converter.convert(words);
		super.train(nodes);
	}
	@Override
	public void train(ArrayList<String[]> corpuses) {
		String[] afterMerge = SegmentationUtils.mergeCorpus(corpuses);
		DataConverter<String[], List<SequenceNode>> converter = new SegmentationDataConverter();
		List<SequenceNode> nodes = converter.convert(afterMerge);
		super.train(nodes);
	}
	@Override
	public String[] seg(String text) {
		List<int[]> intids = SegmentationUtils.strs2int(new String[] {text});
		int[] charids = intids.get(0);
		int[] predi = this.predict(charids);
		return SegmentationUtils.decode(predi, text);
	}
	@Override
	public List<String[]> seg(String[] texts) {
		List<String[]> res = new ArrayList<>(texts.length);
		for(String text : texts) {
			res.add(seg(text));
		}
		return res;
	}
	/**
	 * [Ha, n, LP, 是, 由, 一, 系列, 模型, 与, 算法, 组成, 的, Ja, v, a工, 具包, ，, 目标, 是, 普及, 自然, 语言, 处理, 在, 生产, 环境, 中, 的, 应用, 。]
[高, 锰酸, 钾, ，, 强氧化剂, ，, 紫, 红色, 晶体, ，, 可, 溶于, 水, ，, 遇, 乙醇, 即, 被, 还原, 。, 常用, 作消, 毒剂, 、, 水净, 化剂, 、, 氧化剂, 、, 漂白剂, 、, 毒气, 吸收剂, 、, 二氧化碳, 精制剂, 等, 。]

	 */
}
