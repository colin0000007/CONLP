package com.outsider.nlp.dependency;

import java.util.Arrays;

/**
 * 一个句子中任意2个词之间生成的一个样本
 * @author outsider
 *
 */
public class CoNLLSample {
	public String[] context;
	public CoNLLSample(String[] context) {
		this.context = context;
	}
	
	
	@Override
	public String toString() {
		return Arrays.toString(context);
	}
}
