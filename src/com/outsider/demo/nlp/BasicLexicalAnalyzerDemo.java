package com.outsider.demo.nlp;

import java.util.Arrays;

import com.outsider.nlp.lexicalanalyzer.BasicLexicalAnalyzer;

public class BasicLexicalAnalyzerDemo {
	public static void main(String[] args) {
		String str = "2018年9月24日，今天是中秋节，你做了什么呢？";
		String[] r1 = BasicLexicalAnalyzer.analyze(str);
		System.out.println(Arrays.toString(r1));
		String str1 = "原标题：日媒拍到了现场罕见一幕" + 
				"据日本新闻网（NNN）9月8日报道，日前，日本海上自卫队现役最大战舰之一的直升机航母“加贺”号在南海航行时，遭多艘中国海军战舰抵近跟踪监视。" ; 
		String[] r2 = BasicLexicalAnalyzer.analyze(str1);
		System.out.println(Arrays.toString(r2));
	}
}
