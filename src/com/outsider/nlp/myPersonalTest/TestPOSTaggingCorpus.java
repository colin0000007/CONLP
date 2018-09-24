package com.outsider.nlp.myPersonalTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.outsider.common.util.IOUtils;
import com.outsider.common.util.nlp.ChineseDictionarySortUtil;

public class TestPOSTaggingCorpus {
	public static Set<String> rawData2ConvertedData(String rawData, Object... otherParameters) {
		//按PKU人民日报的切分标准需要解析
		String splitChar = (String) otherParameters[0];
		String corpus = rawData;
		Pattern pattern0 = Pattern.compile("\\[[^\\[\\]]{1,80}\\]/{0,1}\\w{1,5}\\s");
		Matcher matcher0 = pattern0.matcher(corpus);
		Set<String> result = new HashSet<>();//最终的结果
		int start = 0;
		int lastEnd = 0;
		while(matcher0.find()) {
			String s = matcher0.group();
			start = matcher0.start();
			//当遇到[]情况时，处理它前面的非中括号情况，再处理中括号情况
			//lastEnd -> start
			Set<String> nodes1 = dealWithoutBracketSituation(corpus.substring(lastEnd, start), splitChar);
			Set<String> nodes2 = dealBracketSituation(matcher0.group(), splitChar);
			result.addAll(nodes1);
			result.addAll(nodes2);
			lastEnd = matcher0.end();
		}
		//处理最后一个匹配到的[]后面部分
		if(lastEnd < corpus.length()) {
			Set<String> nodes = dealWithoutBracketSituation(corpus.substring(lastEnd, corpus.length()), splitChar);
			result.addAll(nodes);
		}
		return result;
	}
	//处理中括号情况
	private static Set<String> dealBracketSituation(String s, String splitChar){
		int in = s.lastIndexOf("]");//找到[]中的内容
		//处理[]内的
		String[] s2 = s.substring(1, in).split(splitChar);
		Set<String> result = new HashSet<>();
		for(int j = 0; j < s2.length;j++) {
			//这种方式可以解决 //w这种情况的出现
			int index = s2[j].lastIndexOf("/");
			if(index == -1) {
				System.out.println(s);
				continue;
			}
			String word = s2[j].substring(0, index);
			String wordNature = s2[j].substring(index+1, s2[j].length());
			result.add(word);
		}
		//处理[]整体构成的词的词性
		//TODO 暂时只能先不管这种情况
		if(s.charAt(in+1)=='/') {
			result.add(s.substring(in+2));
		} else {
			result.add(s.substring(in+1));
		}
		return result;
	}
	
	public static Set<String> dealWithoutBracketSituation(String subCorpus, String splitChar){
		String[] words = subCorpus.split(splitChar);
		Set<String> result = new HashSet<>();
		for(int i =0; i< words.length;i++) {
			//语料中存在一些没有被标记词性的错误
			if(!words[i].contains("/")) {
				System.out.println("dddd:"+words[i]);
				continue;
			}
			int index = words[i].lastIndexOf("/");
			String word = words[i].substring(0, index);
			String wordNature = words[i].substring(index+1, words[i].length());
			result.add(word);
		}
		return  result;
	}
	
	
	public static void main(String[] args) {
		String path = "D:\\nlp语料\\词性标注\\人民日报语料库2014\\2014人民日报切分语料6_utf8_将中文和符号分开.txt";
		//String path = "D:\\nlp语料\\词性标注\\词性标注@人民日报199801.txt";
		String corpus = IOUtils.readText(path, "utf-8");
		String splitChar = " ";
		//String splitChar = "  ";
		Set<String> words = rawData2ConvertedData(corpus, splitChar);
		List<String> words2 = new ArrayList<>();
		for(String s : words) {
			words2.add(s+"\n");
		}
		ChineseDictionarySortUtil.sortEncodingUtf8(words2);
		StringBuilder sb = new StringBuilder();
		//Pattern pattern = Pattern.compile("[\\d|\\uFF10-\\uFF19]+");//匹配数字
		Pattern pattern = Pattern.compile("[\\d|\\uFF10-\\uFF19]+");
		for(int i =0;i < words2.size();i++) {
			Matcher matcher = pattern.matcher(words2.get(i));
			if(!matcher.find()) {//不把数字添加到词典
				sb.append(words2.get(i));
			}
		}
		IOUtils.writeTextData2File(sb.toString(), "D:\\nlp语料\\词性标注\\dictionary2014_2.txt", "utf-8");
	}
}
