package com.outsider.nlp.lexicalanalyzer;

import com.outsider.nlp.postagger.POSTaggingPredictor;
import com.outsider.nlp.postagger.StaticPOSTagger;
import com.outsider.nlp.segmenter.SegmentationPredictor;
import com.outsider.nlp.segmenter.StaticSegmenter;

public class LexicalAnalyzer {
	private SegmentationPredictor segmenter; 
	private POSTaggingPredictor postagger;
	public static LexicalAnalyzer defaultLexicalAnalyzer;
	public LexicalAnalyzer(SegmentationPredictor segmenter, POSTaggingPredictor postagger) {
		this.segmenter = segmenter;
		this.postagger = postagger;
	}
	
	LexicalAnalysisResult analyze(String text) {
		String[] words = segmenter.seg(text);
		String[] tags = postagger.tag(words);
		LexicalAnalysisResult result = new LexicalAnalysisResult();
		result.setSegmentationResult(words);
		result.setPostaggingResult(tags);
		return result;
	}
	
	public static LexicalAnalyzer getDefault() {
		if(defaultLexicalAnalyzer == null) {
			defaultLexicalAnalyzer = new LexicalAnalyzer(StaticSegmenter.getCRFPPSegmenter(), StaticPOSTagger.getPOSTagger());
		}
		return defaultLexicalAnalyzer;
	}
}
