package com.outsider.nlp.dependency;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.outsider.common.util.IOUtils;

/**
 * 用特征模板
 * 生成依存句法分析语料
 * @author outsider
 *
 *
 *
id  词语 词语 词性(粗) 词性(细) 句法特征   中心词   当前词与中心词的关系
1	坚决	坚决	a	ad	_	2	方式	
2	惩治	惩治	v	v	_	0	核心成分	
3	贪污	贪污	v	v	_	7	限定	
4	贿赂	贿赂	n	n	_	3	连接依存	
5	等	等	u	udeng	_	3	连接依存	
6	经济	经济	n	n	_	7	限定	
7	犯罪	犯罪	v	vn	_	2	受事	


特征模板:
对于一个句子的2个单词，i和j，它们之间的关系就是分类器中的一个类别，而特征的产生由下面的特征模板而来：
	i	j
  W(i)  W(j)
  P(i)  P(j)
 P(i+1) P(j+1)
 P(i+2) P(j+2)
 P(i-1) P(j-1)
 P(i-2) P(j-2)
 	Dis = i-j
W(i)+W(j) P(i)+P(j)
W(i)+W(j)+Dis P(i)+P(j)+Dis
P(i)+P(j)+P(i-1) P(i)+P(j)+P(i+1)
P(i)+P(j)+P(j-1) P(i)+P(j)+P(j+1)
增加特征:
W(i-2) W(j-2)
W(i-1) W(j-1)
W(i+1) W(j+1)
W(i+2) W(j+2)

其中W是词本身，P是词性，Dis是词距离，有正负之分
加号表示特征组合
 */
public class CoNLLFeatureGenerator {
	//不同特征维度之间的分割符
	public static final String splitChar = " ";
	/**
	 * 制作依存句法分析的训练样本 
	 * @param lines CoNLL格式的行list
	 * @return 样本List
	 */
	public static List<String> makeData(List<String> lines){
		//生成句子
		List<CoNLLSentence> sentences = makeSentence(lines);
		List<String> finalTrainData = new ArrayList<>();
		//根据上面的特征模板提取上下文特征并生成训练数据
		int count = 1;
		for(CoNLLSentence sentence : sentences) {
			//i和j即句子中的任意2个单词，i和j之间生成一个样本，如果没有依存关系直接取null  
			//i的中心词是j
			//因为i=0，该节点root不可能有中心词，所以i从1开始
			System.out.println("sentence:"+(count++)+"/"+sentences.size());
			for(int i = 1; i < sentence.length(); i++) {
				CoNLLWord wordi = sentence.getWord(i);
				for(int j = 0; j < sentence.length(); j++) {
					if(i == j) continue;
					//含有依存关系的那条边
					String feature = null;
					if(j == wordi.getHEAD()) {
						feature = makeContextFeature(sentence, i, j, wordi.getDEPREL());
					} else { //不含有依存关系
						feature = makeContextFeature(sentence, i, j, CoNLLWord.NoneDEPREL);
					}
					finalTrainData.add(feature);
				}
			}
		}
		return finalTrainData;
	}
	
	
	public static String makeContextFeature(CoNLLSentence sentence, int i, int j, String DEPREL) {
		StringBuilder sb = new StringBuilder();
		//为了区分不同维度的特征，需要加上特征标识，例如，W(i)和W(j)本属于不同维度的特征，但是值域相同
		//若不加以区分，训练时会造成特征冲突
		sb.append(sentence.LEMMA(i)+"i"+splitChar);//W(i)
		sb.append(sentence.LEMMA(j)+"j"+splitChar);//W(j)
		sb.append(sentence.CPOSTAG(i)+"i"+splitChar);//P(i)
		sb.append(sentence.CPOSTAG(j)+"j"+splitChar);//P(j)
		//P(i+1) P(j+1)
		 //P(i+2) P(j+2)
		sb.append(sentence.CPOSTAG(i+1)+"i+1"+splitChar);
		sb.append(sentence.CPOSTAG(j+1)+"j+1"+splitChar);
		sb.append(sentence.CPOSTAG(i+2)+"i+2"+splitChar);
		sb.append(sentence.CPOSTAG(j+2)+"j+2"+splitChar);
		//P(i-1) P(j-1)
		 //P(i-2) P(j-2)
		sb.append(sentence.CPOSTAG(i-1)+"i-1"+splitChar);//P(i)
		sb.append(sentence.CPOSTAG(j-1)+"j-1"+splitChar);//P(j)
		sb.append(sentence.CPOSTAG(i-2)+"i-2"+splitChar);//P(i)
		sb.append(sentence.CPOSTAG(j-2)+"j-2"+splitChar);//P(j)
		//Dis
		int Dis = i - j;
		sb.append(Dis+splitChar);
		//W(i)+W(j) P(i)+P(j)
		sb.append(sentence.LEMMA(i)+sentence.LEMMA(j)+"0"+splitChar);
		sb.append(sentence.CPOSTAG(i)+sentence.CPOSTAG(j)+"1"+splitChar);
		//W(i)+W(j)+Dis P(i)+P(j)+Dis
		sb.append(sentence.LEMMA(i)+sentence.LEMMA(j)+Dis+"2"+splitChar);
		sb.append(sentence.CPOSTAG(i)+sentence.CPOSTAG(j)+Dis+"3"+splitChar);
		//P(i)+P(j)+P(i-1) P(i)+P(j)+P(i+1)
		sb.append(sentence.CPOSTAG(i)+sentence.CPOSTAG(j)+sentence.CPOSTAG(i-1)+"4"+splitChar);
		sb.append(sentence.CPOSTAG(i)+sentence.CPOSTAG(j)+sentence.CPOSTAG(i+ 1)+"5"+splitChar);
		//P(i)+P(j)+P(j-1) P(i)+P(j)+P(j+1)
		sb.append(sentence.CPOSTAG(i)+sentence.CPOSTAG(j)+sentence.CPOSTAG(j-1)+"6"+splitChar);
		sb.append(sentence.CPOSTAG(i)+sentence.CPOSTAG(j)+sentence.CPOSTAG(j+ 1)+"7"+splitChar);
		//W(i+1) W(j+1)
		//W(i+2) W(j+2)
		sb.append(sentence.LEMMA(i+1)+"a"+splitChar);
		sb.append(sentence.LEMMA(j+1)+"b"+splitChar);
		sb.append(sentence.LEMMA(i+2)+"c"+splitChar);
		sb.append(sentence.LEMMA(j+2)+"d"+splitChar);
		//W(i-1) W(j-1)
		//W(i-2) W(j-2)
		sb.append(sentence.LEMMA(i-1)+"e"+splitChar);
		sb.append(sentence.LEMMA(j-1)+"f"+splitChar);
		sb.append(sentence.LEMMA(i-2)+"g"+splitChar);
		sb.append(sentence.LEMMA(j-2)+"h"+splitChar);
		//最后加上标签
		sb.append(DEPREL);
		return sb.toString();
	}
	
	/**
	 * 将CoNLL中的行解析为多个句子。
	 * @param lines
	 * @return
	 */
	public static List<CoNLLSentence> makeSentence(List<String> lines){
		
		List<ArrayList<String>> sentences = new ArrayList<>();
		ArrayList<String> sentence = new ArrayList<>();
		for(int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).trim();
			if(line.equals("")) {
				//if(sentence.size() > 0) {
				sentences.add(sentence);
				//} else {
				//	System.out.println("出现空sentence！！！");
				//}
				sentence = new ArrayList<>();
			} else {
				sentence.add(line);
			}
		}
		
		List<CoNLLSentence> data = new ArrayList<>();
		for(ArrayList<String> se : sentences) {
			data.add(new CoNLLSentence(se));
		}
		return data;
	}
	
	public static void main(String[] args) {
		List<String> lines = IOUtils.readTextAndReturnLines("D:\\nlp语料\\依存句法分析\\依存分析训练数据_NLP&CC 2013\\THU\\train.conll", "utf-8");
		List<String> samples = makeData(lines);
		String savePath = "D:\\\\nlp语料\\\\依存句法分析\\\\依存分析训练数据_NLP&CC 2013\\\\THU\\train_maxent.data";
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(savePath));
			for(String s : samples) {
				bufferedWriter.write(s);
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
