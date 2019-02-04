package com.outsider.nlp.postagger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.outsider.common.util.IOUtils;
import com.outsider.common.util.nlp.NLPUtils;
import com.outsider.nlp.lexicalanalyzer.LexicalAnalysisResult;

/**
 * 解析PKU格式的词性标注语料
 * @author outsider
 *
 */
public class PKUFormatParser {
	public static LexicalAnalysisResult  parse(String rawData, String splitCharr) {
		//按PKU人民日报的切分标准解析
		LexicalAnalysisResult result = new LexicalAnalysisResult();
		List<String> words = new ArrayList<>();
		List<String> natures = new ArrayList<>();
		String splitChar = splitCharr;
		String corpus = rawData;
		Pattern pattern0 = Pattern.compile("\\[[^\\[\\]]+\\]/{0,1}\\w{1,5}\\s");
		Matcher matcher0 = pattern0.matcher(corpus);
		int start = 0;
		int lastEnd = 0;
		while(matcher0.find()) {
			String s = matcher0.group();
			start = matcher0.start();
			//当遇到[]情况时，处理它前面的非中括号情况，再处理中括号情况
			//lastEnd -> start
			dealWithoutBracketSituation(corpus.substring(lastEnd, start), splitChar, words, natures);
			dealBracketSituation(matcher0.group(), splitChar, words, natures);
			lastEnd = matcher0.end();
		}
		//处理最后一个匹配到的[]后面部分
		if(lastEnd < corpus.length()) {
			 dealWithoutBracketSituation(corpus.substring(lastEnd, corpus.length()), splitChar, words ,natures);
		}
		String[] wordsArr = new String[words.size()];
		String[] natureArr = new String[natures.size()];
		words.toArray(wordsArr);
		natures.toArray(natureArr);
		result.setSegmentationResult(wordsArr);
		result.setPostaggingResult(natureArr);
		return result;
	}
	//处理中括号情况
	private static void dealBracketSituation(String s, String splitChar, List<String> words, List<String> natures){
		
		int in = s.lastIndexOf("]");//找到[]中的内容
		//处理[]内的
		String[] s2 = s.substring(1, in).split(splitChar);
		for(int j = 0; j < s2.length;j++) {
			//这种方式可以解决 //w这种情况的出现
			int index = s2[j].lastIndexOf("/");
			if(index == -1) {
				System.out.println(s);
				continue;
			}
			String word = s2[j].substring(0, index);
			String wordNature = s2[j].substring(index+1, s2[j].length());
			words.add(word);
			natures.add(wordNature);
		}
	}
	
	public static void dealWithoutBracketSituation(String subCorpus, String splitChar, List<String> words, List<String> natures){
		if(subCorpus.trim().equals("")) {
			return;
		}
		String[] s = subCorpus.split(splitChar);
		for(int i =0; i< s.length;i++) {
			//语料中存在一些没有被标记词性的错误
			/*if(!s[i].contains("/")) {
				System.out.println("dddd:"+subCorpus);
				continue;
			}*/
			int index = s[i].lastIndexOf("/");
			String word = s[i].substring(0, index);
			String wordNature = s[i].substring(index+1, s[i].length());
			words.add(word);
			natures.add(wordNature);
		}
	}
	public static void main(String[] args) {
		String path = "D:\\nlp语料\\词性标注\\词性标注@人民日报199801.txt";
		String data = IOUtils.readText(path, "utf-8");
		LexicalAnalysisResult result = parse(data, "  ");
		String[] natures = result.getPostaggingResult();
		String[] words = result.getSegmentationResult();
		StringBuilder sb = new StringBuilder();
		List<String> mis = new ArrayList<>();
		mis.add("Ag");
		mis.add("Vg");
		mis.add("Tg");
		mis.add("Ng");
		mis.add("Dg");
		mis.add("Bg");
		mis.add("Yg");
		mis.add("Vg");
		mis.add("Vg");
		for(int i = 0; i < words.length; i++) {
			if(mis.contains(natures[i])) {
				natures[i] = natures[i].toLowerCase();
			}
			sb.append(words[i]+"\t"+natures[i]+"\n");
		}
		IOUtils.writeTextData2File(sb.toString(), "D:\\\\nlp语料\\\\词性标注\\\\词性标注@人民日报199801_crf.txt", "utf-8");
	}
}
