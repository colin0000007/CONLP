package com.outsider.nlp.dependency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 根据词性和词生成待预测样本
 * 送到模型中做分类
 * @author outsider
 * 
 */
public class CoNLLSampleGenerator {
	/**
	 * 单个句子生成预测样本
	 * @param words 句子中按顺序排列的词
	 * @param natures 对应的词性
	 * @return 一个二维样本数组，例如Context[0][1]表示ROOT节点和第一个词之间生成的依存边
	 */
	public static CoNLLSample[][] generate(String[] words, String[] natures) {
		int len = words.length + 1;
		String[] wordsWithRoot = new String[len];
		String[] naturesWithRoot = new String[len];
		System.arraycopy(words, 0, wordsWithRoot, 1, words.length);
		System.arraycopy(natures, 0, naturesWithRoot, 1, natures.length);
		wordsWithRoot[0] = CoNLLWord.ROOT_LEMMA;
		naturesWithRoot[0] = CoNLLWord.ROOT_CPOSTAG;
		CoNLLSample[][] allSamples = new CoNLLSample[len][len];
		for(int i = 1; i < len; i++) {
			for(int j = 0; j < len; j++) {
				//一个问题:对于根节点不应该只有某个节点指向它，它不可能指向其他节点。
				if(i == j) continue;
				String[] context = makeContextFeature(wordsWithRoot, naturesWithRoot, i, j);
				allSamples[i][j] = new CoNLLSample(context);
			}
		}
		return allSamples;
	}
	
	public static List<CoNLLSample[][]> generate(List<String[]> wordsOfSentences, List<String[]> naturesOfSentences) {
		List<CoNLLSample[][]> contexts = new ArrayList<>(wordsOfSentences.size());
		for(int i = 0; i < wordsOfSentences.size(); i++) {
			CoNLLSample[][] context = generate(wordsOfSentences.get(i), naturesOfSentences.get(i));
			contexts.add(context);
		}
		return contexts;
	}
	
	
	public static String[] makeContextFeature(String[] words, String[] natures, int i, int j) {
		int len = words.length;
		String[] context = new String[29];
		//为了区分不同维度的特征，需要加上特征标识，例如，W(i)和W(j)本属于不同维度的特征，但是值域相同
		//若不加以区分，训练时会造成特征冲突
		context[0] = words[i] + "i";//W(i)
		context[1] = words[j] + "j";//W(j)
		context[2] = natures[i]+"i";//P(i)
		context[3] = natures[j]+"j";//P(j)
		//P(i+1) P(j+1)
		 //P(i+2) P(j+2)
		context[4] = i + 1 >= len ? CoNLLWord.OOICPOSTAG+"i+1" : natures[i+1]+"i+1";
		context[5] = j + 1 >= len ? CoNLLWord.OOICPOSTAG+"j+1" : natures[j+1]+"j+1";
		context[6] = i + 2 >= len ? CoNLLWord.OOICPOSTAG+"i+2": natures[i+2]+"i+2";
		context[7] = j + 2 >= len ? CoNLLWord.OOICPOSTAG+"j+2" : natures[j+2]+"j+2";
		//P(i-1) P(j-1)
		 //P(i-2) P(j-2)
		context[8] = i - 1 < 0? CoNLLWord.OOICPOSTAG+"i-1" : natures[i-1]+"i-1";
		context[9] = j - 1 < 0? CoNLLWord.OOICPOSTAG+"j-1" : natures[j-1]+"j-1";
		context[10] = i - 2 < 0? CoNLLWord.OOICPOSTAG+"i-2" : natures[i-2]+"i-2";
		context[11] = j - 2 < 0? CoNLLWord.OOICPOSTAG+"j-2" : natures[j-2]+"j-2";
		//Dis
		int Dis = i - j;
		context[12] = Dis+"";
		//W(i)+W(j) P(i)+P(j)
		context[13] = words[i] + words[j] + "0";
		context[14] = natures[i] + natures[j] + "1";
		//W(i)+W(j)+Dis P(i)+P(j)+Dis
		context[15] = words[i] + words[j] + Dis+"2";
		context[16] = natures[i] + natures[j] + Dis+"3";
		//P(i)+P(j)+P(i-1) P(i)+P(j)+P(i+1)
		context[17] = natures[i] + natures[j] + (i - 1 < 0? CoNLLWord.OOICPOSTAG : natures[i - 1])+"4";
		context[18] = natures[i] + natures[j] + (i + 1 >= len? CoNLLWord.OOICPOSTAG : natures[i + 1])+"5";
		//P(i)+P(j)+P(j-1) P(i)+P(j)+P(j+1)
		context[19] = natures[i] + natures[j] + (j - 1 < 0? CoNLLWord.OOICPOSTAG : natures[j - 1])+"6";
		context[20] = natures[i] + natures[j] + (j + 1 >= len? CoNLLWord.OOICPOSTAG : natures[j + 1])+"7";
		//W(i+1) W(j+1)
		//W(i+2) W(j+2)
		context[21] = i+1 >= len? CoNLLWord.OOILEMMA+"a" : words[i+1]+"a";
		context[22] = j+1 >= len? CoNLLWord.OOILEMMA+"b" : words[j+1]+"b";
		context[23] = i+2 >= len? CoNLLWord.OOILEMMA+"c" : words[i+2]+"c";
		context[24] = j+2 >= len? CoNLLWord.OOILEMMA+"d" : words[j+2]+"d";
		//W(i-1) W(j-1)
		//W(i-2) W(j-2)
		context[25] = i-1 < 0? CoNLLWord.OOILEMMA+"e" : words[i-1]+"e";
		context[26] = j-1 < 0? CoNLLWord.OOILEMMA+"f" : words[j-1]+"f";
		context[27] = i-2 < 0? CoNLLWord.OOILEMMA+"g" : words[i-2]+"g";
		context[28] = j-2 < 0? CoNLLWord.OOILEMMA+"h" : words[j-2]+"h";
		//最后加上标签
		return context;
	}
	
	public static void main(String[] args) {
		CoNLLSample[][] context = generate(new String[] {"坚决","惩治","贪污","贿赂","等","经济","犯罪"}, new String[] {"a","v","v","n","u","n","v"});
		System.out.println("所有依存边:");
		//其中j是i的中心词也就是边的指向是i-->j
		for(int i = 0; i < context.length; i++) {
			for(int j = 0; j < context.length; j++) {
				System.out.println("依存边,"+i+"->"+j+":");
				System.out.println(context[i][j]);
			}
		}
	}
}

/**
 * 
 * 
1	坚决	坚决	a	ad	_	2	方式	
2	惩治	惩治	v	v	_	0	核心成分	
3	贪污	贪污	v	v	_	7	限定	
4	贿赂	贿赂	n	n	_	3	连接依存	
5	等	等	u	udeng	_	3	连接依存	
6	经济	经济	n	n	_	7	限定	
7	犯罪	犯罪	v	vn	_	2	受事	
 * 
 */
