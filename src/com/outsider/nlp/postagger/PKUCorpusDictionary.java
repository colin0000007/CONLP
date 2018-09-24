package com.outsider.nlp.postagger;

import java.io.IOException;

import com.outsider.common.algorithm.dat.darts.DoubleArrayTrie;
import com.outsider.constants.nlp.PathConstans;

public class PKUCorpusDictionary {
	private static DoubleArrayTrie doubleArrayTrie;
	public static int wordNum  = 186428;
	public static DoubleArrayTrie getDictionary() {
		if(doubleArrayTrie == null) {
			doubleArrayTrie = new DoubleArrayTrie();
			try {
				//打开默认词库路径
				doubleArrayTrie.open(PathConstans.DIC_4_POSTAGGER);
				//wordNum = doubleArrayTrie.
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return doubleArrayTrie;
	}
	
	public static DoubleArrayTrie getDictionary(String path) {
		if(doubleArrayTrie == null) {
			doubleArrayTrie = new DoubleArrayTrie();
			try {
				doubleArrayTrie.open(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return doubleArrayTrie;
	}
}
