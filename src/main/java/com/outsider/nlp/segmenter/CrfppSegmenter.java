package com.outsider.nlp.segmenter;

import java.util.ArrayList;
import java.util.List;

import com.outsider.zhifac.crf4j.extension.CrfppModelTemplate;

/**
 * CRF4j实现的CRF的分词器
 * @author outsider
 *
 */
public class CrfppSegmenter extends CrfppModelTemplate implements SegmentationPredictor{
	
	
	
	
	public CrfppSegmenter() {
		super();
	}

	public CrfppSegmenter(String modelFile, int nbest, int vlevel, double costFactor) {
		super(modelFile, nbest, vlevel, costFactor);
	}

	public String[] seg(String text) {
		return SegmentationUtils4Crfpp.seg(text, tagger);
	}

	public List<String[]> seg(String[] texts) {
		List<String[]> res = new ArrayList<>(texts.length);
		for(String text : texts) {
			res.add(seg(text));
		}
		return res;
	}
}
