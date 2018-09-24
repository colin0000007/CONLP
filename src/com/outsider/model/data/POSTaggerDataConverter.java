package com.outsider.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.outsider.common.algorithm.dat.darts.DoubleArrayTrie;
import com.outsider.model.hmm.SequenceNode;
import com.outsider.nlp.postagger.POSTagger;

public class POSTaggerDataConverter implements DataConverter<String, List<SequenceNode>>{
	private DoubleArrayTrie dictionary;
	public POSTaggerDataConverter(DoubleArrayTrie dictionary) {
		this.dictionary = dictionary;
	}
	@Override
	public List<SequenceNode> rawData2ConvertedData(String rawData, Object... otherParameters) {
		//按PKU人民日报的切分标准需要解析
		String splitChar = (String) otherParameters[0];
		String corpus = rawData;
		Pattern pattern0 = Pattern.compile("\\[[^\\[\\]]{1,80}\\]/{0,1}\\w{1,5}\\s");
		Matcher matcher0 = pattern0.matcher(corpus);
		List<SequenceNode> result = new ArrayList<>();//最终的结果
		int start = 0;
		int lastEnd = 0;
		while(matcher0.find()) {
			String s = matcher0.group();
			start = matcher0.start();
			//当遇到[]情况时，处理它前面的非中括号情况，再处理中括号情况
			//lastEnd -> start
			List<SequenceNode> nodes1 = dealWithoutBracketSituation(corpus.substring(lastEnd, start), splitChar);
			List<SequenceNode> nodes2 = dealBracketSituation(matcher0.group(), splitChar);
			result.addAll(nodes1);
			result.addAll(nodes2);
			lastEnd = matcher0.end();
		}
		//处理最后一个匹配到的[]后面部分
		if(lastEnd < corpus.length()) {
			List<SequenceNode> nodes = dealWithoutBracketSituation(corpus.substring(lastEnd, corpus.length()), splitChar);
			result.addAll(nodes);
		}
		return result;
	}
	//处理中括号情况
	private List<SequenceNode> dealBracketSituation(String s, String splitChar){
		int in = s.lastIndexOf("]");//找到[]中的内容
		//处理[]内的
		String[] s2 = s.substring(1, in).split(splitChar);
		List<SequenceNode> result = new ArrayList<>();
		for(int j = 0; j < s2.length;j++) {
			//这种方式可以解决 //w这种情况的出现
			int index = s2[j].lastIndexOf("/");
			String word = s2[j].substring(0, index);
			String wordNature = s2[j].substring(index+1, s2[j].length());
			SequenceNode node = new SequenceNode(dictionary.intIdOf(word), POSTagger.natureName2Int.get(wordNature));
			result.add(node);
		}
		//处理[]整体构成的词的词性
		//TODO 暂时只能先不管这种情况
		//String s4 = s.substring(end+1, s.length()-1);
		return result;
	}
	
	public List<SequenceNode> dealWithoutBracketSituation(String subCorpus, String splitChar){
		String[] words = subCorpus.split(splitChar);
		List<SequenceNode> result = new ArrayList<>();
		for(int i =0; i< words.length;i++) {
			int index = words[i].lastIndexOf("/");
			//语料中存在一些没有被标记词性的错误
			if(index == -1) {
				continue;
			}
			String word = words[i].substring(0, index);
			//语料中存在一些没有被标记词性的错误
			String wordNature = words[i].substring(index+1, words[i].length());
			SequenceNode node = new SequenceNode(dictionary.intIdOf(word), POSTagger.natureName2Int.get(wordNature));
			result.add(node);
		}
		return  result;
	}
}
