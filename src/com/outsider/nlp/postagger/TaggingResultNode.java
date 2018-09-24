package com.outsider.nlp.postagger;

/**
 * 存储返回结果利用好处理
 * @author outsider
 *
 */
public class TaggingResultNode {
	public String word;
	public String nature;
	public TaggingResultNode(String word, String nature) {
		this.word = word;
		this.nature = nature;
	}
}
