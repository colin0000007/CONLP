package com.outsider.common.util.nlp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.outsider.common.util.IOUtils;

/**
 * 语料操作相关的工具类
 * @author outsider
 *
 */
public class CorpusUtils {
	/**
	 * 制作CRF格式的分词语料，标签是B/M/E/S
	 * @param words 单词数组
	 * @return 处理好的数据，
	 * 例如: 
	 * 你		B
	 * 好		E
	 * 。		S	
	 */
	public static String makeCRFormatSegmentationCorpus(String[] words) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < words.length;i++) {
			words[i] = words[i].trim();
			if(words[i].length() == 0) {
				continue;
			}
			if(words[i].length() == 1) {
				sb.append(words[i]+"\t"+"S\n");
			} else {
				if(i == 0) {
					System.out.println(words[0]);
					System.out.println(words[0].length());
				}
				String s = words[i];
				sb.append(s.charAt(0)+"\t"+"B\n");
				for(int j = 1; j < s.length()-1;j++) {
					sb.append(s.charAt(j)+"\t"+"M\n");
				}
				sb.append(s.charAt(s.length()-1)+"\t"+"E\n");
			}
		}
		return sb.toString();
	}
	/**
	 * 按照PKU2014人民日报的语料解析其中的分词结果和词性标注结果
	 * 格式例如:
	 *  人民网/nz 1月1日/t 讯/ng 据/p 《/w [纽约/nsf 时报/n]/nz 》
	 * @param corpus 没有经过任何处理的原始数据
	 * @param splitChar 分隔符
	 * @return 返回解析结果，例如: 
	 * 你好	d
	 * 吗	ude
	 * ？	w
	 */
	public static StringBuilder makeCRFormatPOSTaggingCorpusWithPKUFormat(String corpus, String splitChar) {
		//按PKU人民日报的切分标准需要解析
		StringBuilder result = new StringBuilder();
		Pattern pattern0 = Pattern.compile("\\[[^\\[\\]]+\\]/{0,1}\\w{1,5}\\s");
		Matcher matcher0 = pattern0.matcher(corpus);
		int start = 0;
		int lastEnd = 0;
		while(matcher0.find()) {
			String s = matcher0.group();
			start = matcher0.start();
			//当遇到[]情况时，处理它前面的非中括号情况，再处理中括号情况
			//lastEnd -> start
			dealWithoutBracketSituation(corpus.substring(lastEnd, start), splitChar, result);
			dealBracketSituation(matcher0.group(), splitChar, result);
			lastEnd = matcher0.end();
		}
		//处理最后一个匹配到的[]后面部分
		if(lastEnd < corpus.length()) {
			dealWithoutBracketSituation(corpus.substring(lastEnd, corpus.length()), splitChar, result);
		}
		return result;
	}
	
	//处理中括号情况
	private static void dealBracketSituation(String s, String splitChar, StringBuilder result){
		int in = s.lastIndexOf("]");//找到[]中的内容
		//处理[]内的
		String[] s2 = s.substring(1, in).split(splitChar);
		for(int j = 0; j < s2.length;j++) {
			//这种方式可以解决 //w这种情况的出现
			int index = s2[j].lastIndexOf("/");
			if(index == -1) {
				System.out.println(s.trim());
				continue;
			}
			String word = s2[j].substring(0, index).toLowerCase().trim();
			String wordNature = s2[j].substring(index+1, s2[j].length()).trim();
			result.append(word+"\t"+wordNature+"\n");
		}
	}
		
	private static void dealWithoutBracketSituation(String subCorpus, String splitChar, StringBuilder result){
		String[] words = subCorpus.split(splitChar);
		for(int i =0; i< words.length;i++) {
			//语料中存在一些没有被标记词性的错误
			if(!words[i].contains("/")) {
				System.out.println(words[i].trim());
				continue;
			}
			int index = words[i].lastIndexOf("/");
			String word = words[i].substring(0, index).toLowerCase().trim();
			String wordNature = words[i].substring(index+1, words[i].length()).trim();
			result.append(word+"\t"+wordNature+"\n");
		}
	}
	
	public static void main(String[] args) {
		//此语料中经过校正依然存在错误，有些词语没有被标注词性
		//String data = IOUtils.readText("D:\\nlp语料\\词性标注\\人民日报语料库2014\\2014人民日报切分语料6_utf8_将中文和符号分开.txt", "utf-8");
		String data = IOUtils.readText("D:\\nlp语料\\词性标注\\人民日报语料库2014\\2014人民日报切分语料6_utf8_将中文和符号分开.txt", "utf-8"); 
		StringBuilder result = makeCRFormatPOSTaggingCorpusWithPKUFormat(data, " ");
		//IOUtils.writeTextData2File(result.toString(), "D:\\nlp语料\\词性标注\\词性标注_crf.txt", "utf-8");
	}
}
