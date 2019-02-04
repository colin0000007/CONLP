package com.outsider.nlp.segmenter;

import java.util.ArrayList;
import java.util.List;

import com.outsider.model.data.DataConverter;
import com.outsider.model.data.SegmentationDataConverter;
import com.outsider.model.hmm.FirstOrderGeneralHMM;
import com.outsider.model.hmm.SequenceNode;
/**
 * 一阶HMM分词器
 * @author outsider
 *
 */
public class FirstOrderHMMSegmenter extends FirstOrderGeneralHMM implements Segmenter{
	/**
	 * 默认就构造的是BMES这种分词
	 */
	public FirstOrderHMMSegmenter() {
		super(4, 65536);
	}
	
	public FirstOrderHMMSegmenter(int stateNum, int observationNum, double[] pi, double[][] transferProbability1,
			double[][] emissionProbability) {
		super(stateNum, observationNum, pi, transferProbability1, emissionProbability);
	}

	public FirstOrderHMMSegmenter(int stateNum, int observationNum) {
		super(stateNum, observationNum);
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
	
}
