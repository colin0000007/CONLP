package com.outsider.demo.nlp;

import java.util.Arrays;

import com.outsider.nlp.postagger.POSTagger;
import com.outsider.nlp.postagger.StaticPOSTagger;
import com.outsider.nlp.segmenter.Segmenter;
import com.outsider.nlp.segmenter.StaticSegmenter;

public class POSTaggerDemo {
	public static void main(String[] args) {
		POSTagger tagger = StaticPOSTagger.getPOSTagger();
		String s = "1996年我出生了。";
		Segmenter segmenter = StaticSegmenter.getSegmenter();
		String[] seg = segmenter.predictAndReturnTerms(s);
		String[] result = tagger.predictAndReturnStr(seg);
		System.out.println(Arrays.toString(result));
	}
}
