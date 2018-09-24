package com.outsider.demo.nlp;

import java.util.Arrays;

import com.outsider.nlp.segmenter.FirstOrderHMMSegmenter;
import com.outsider.nlp.segmenter.Segmenter;
import com.outsider.nlp.segmenter.StaticSegmenter;

public class SegmenterDemo {
	public static void demo() {
	}
	
	public static void main(String[] args) {
		//一阶模型
		Segmenter segmenter = StaticSegmenter.getSegmenter();
		String test = "原标题：日媒拍到了现场罕见一幕" + 
				"据日本新闻网（NNN）9月8日报道，日前，日本海上自卫队现役最大战舰之一的直升机航母“加贺”号在南海航行时，遭多艘中国海军战舰抵近跟踪监视。" ; 
		String[] terms = segmenter.predictAndReturnTerms(test);
		System.out.println(Arrays.toString(terms));
		//二阶模型
		Segmenter seondOrderHMMSegmenter = StaticSegmenter.getSecondOrderHMMSegmenter();
		String[] result = seondOrderHMMSegmenter.predictAndReturnTerms(test);
		System.out.println(Arrays.toString(result));
		//词性标注待更新，应该要不了多久
		
		String ss = "农民告诉记者，他们今年应该能收到500多斤玉米/平方米。";
		String[] s = seondOrderHMMSegmenter.predictAndReturnTerms(ss);
		System.out.println(Arrays.toString(s));
		
		String[] s2 = segmenter.predictAndReturnTerms(ss);
		System.out.println(Arrays.toString(s2));
	}
}
