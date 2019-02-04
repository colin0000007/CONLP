package com.outsider.nlp.segmenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.outsider.common.dataStructure.Table;
import com.outsider.common.util.IOUtils;
import com.outsider.model.crf.unsupervised.old.UnsupervisedCRF;
import com.outsider.model.data.SegmentationDataConverter;
import com.outsider.model.hmm.SequenceNode;

public class UnsupervisedCRFSegmenter extends UnsupervisedCRF implements Segmenter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static void testSeg(Segmenter segmenter) {
		List<String> sentences = IOUtils.readTextAndReturnLines("./src/test/resources/sentences4segmentation.txt", "gbk");
		long time = 0;//4.3953996
		long len = 0;
		for(String sentence : sentences) {
			long start = System.currentTimeMillis();
			String[] res = segmenter.seg(sentence);
			long end = System.currentTimeMillis();
			time += (end - start);
			len += sentence.length();
			System.out.println(Arrays.toString(res));
		}
		System.out.println("∫ƒ ±:"+time+"∫¡√Î");
	}
	public UnsupervisedCRFSegmenter() {
		super(65536, 4);
	}

	public UnsupervisedCRFSegmenter(int observationNum, int stateNum) {
		super(observationNum, stateNum);
	}

	@Override
	public void train(Table table, int xColumnIndex, int yColumnIndex) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String generateModelTemplate() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public String[] seg(String text) {
		return seg(new String[] {text}).get(0);
	}

	@Override
	public List<String[]> seg(String[] texts) {
		List<int[]> intids = SegmentationUtils.strs2int(texts);
		List<String[]> res = new ArrayList<>(intids.size());
		for(int i = 0; i < intids.size(); i++) {
			int[] code = predict(intids.get(i));
			String[] result = SegmentationUtils.decode(code, texts[i]);
			res.add(result);
		}
		return res;
	}

	@Override
	public void train(String[] words) {
		SegmentationDataConverter converter = new SegmentationDataConverter();
		List<SequenceNode> nodes = converter.convert(words);
		train(nodes);
	}

	@Override
	public void train(ArrayList<String[]> corpuses) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		UnsupervisedCRFSegmenter segmenter = new UnsupervisedCRFSegmenter();
		String[] words = IOUtils.loadSegmentionCorpus("./data/pku_training.splitBy2space.utf8", "utf-8", "  ");
		segmenter.train(words);
		testSeg(segmenter);
	}

}
