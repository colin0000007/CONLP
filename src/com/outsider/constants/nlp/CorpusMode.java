package com.outsider.constants.nlp;

/**
 * 暂时不用，之前考虑到有些语料不是一篇通篇文章，而是句子组成
 * @author outsider
 *
 */
@Deprecated
public class CorpusMode {
	//语料是一篇通顺的文章
	public static final int ARTICLE = 0;
	//语料是由很多句子构成但是互不相关
	public static final int SENTENCES = 1;
	//语料是一个个的词
	public static final int TERMS = 2;
}
