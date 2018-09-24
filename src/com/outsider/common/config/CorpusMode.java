package com.outsider.common.config;

@Deprecated
public class CorpusMode {
	//语料是一篇通顺的文章
	public static final int SEGMENTION_ARTICLE = 0;
	//语料是由很多句子构成但是互不相关
	public static final int SEGMENTION_SENTENCES = 1;
	//语料是一个个的词
	public static final int SEGMENTION_TERMS = 2;
}
