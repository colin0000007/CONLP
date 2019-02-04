package com.outsider.nlp.dictionary;

import java.util.List;
/**
 * Œ¥ π”√
 * @author outsider
 *
 */
public class UserDictionary {
	private List<String> words;
	public UserDictionary() {
	}
	public UserDictionary(List<String> words) {
		this.words = words;
	}
	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}
	
}
