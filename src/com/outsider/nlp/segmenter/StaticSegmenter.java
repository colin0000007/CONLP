package com.outsider.nlp.segmenter;

public class StaticSegmenter {
	private static final Segmenter segmenter1 = new FirstOrderHMMSegmenter();
	private static final Segmenter segmenter2 = new SecondOrderHMMSegmenter();
	static {
		segmenter1.loadModel(null);
		segmenter2.loadModel(null);
	}
	public static Segmenter getSegmenter() {
		return segmenter1;
	}
	public static Segmenter getSecondOrderHMMSegmenter() {
		return segmenter2;
	}
	
}
