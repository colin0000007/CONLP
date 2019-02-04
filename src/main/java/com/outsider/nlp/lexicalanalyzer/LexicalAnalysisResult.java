package com.outsider.nlp.lexicalanalyzer;

/**
 * 封装词法分析的结果
 * @author outsider
 */
public class LexicalAnalysisResult {
	//分词结果
	private String[] segmentationResult;
	//词性标注结果
	private String[] postaggingResult;
	public String[] getSegmentationResult() {
		return segmentationResult;
	}
	public void setSegmentationResult(String[] segmentationResult) {
		this.segmentationResult = segmentationResult;
	}
	public String[] getPostaggingResult() {
		return postaggingResult;
	}
	public void setPostaggingResult(String[] postaggingResult) {
		this.postaggingResult = postaggingResult;
	}
	public LexicalAnalysisResult(String[] segmentationResult, String[] postaggingResult) {
		super();
		this.segmentationResult = segmentationResult;
		this.postaggingResult = postaggingResult;
	}
	public LexicalAnalysisResult() {}
}
