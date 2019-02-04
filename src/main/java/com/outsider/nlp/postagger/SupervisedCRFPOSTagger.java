package com.outsider.nlp.postagger;

import java.util.List;

import com.outsider.common.algorithm.dat.darts.DoubleArrayTrie;
import com.outsider.common.dataStructure.Table;
import com.outsider.common.util.IOUtils;
import com.outsider.model.crf.FeatureFunction;
import com.outsider.model.crf.SupervisedCRF;
import com.outsider.model.hmm.SequenceNode;
import com.outsider.model.metric.Metric;
import com.outsider.nlp.lexicalanalyzer.LexicalAnalysisResult;
/**
 * 不实用，不如一阶HMM，训练数据不能用完
 * @author outsider
 *
 */
public class SupervisedCRFPOSTagger extends SupervisedCRF implements POSTagger{
	private DoubleArrayTrie<String> dictionary = POSTaggingUtils.getDefaultDictionary();
	private WordNatureMapping wordNatureMapping = WordNatureMapping.getDefault();
	
	
	
	public SupervisedCRFPOSTagger(DoubleArrayTrie<String> dic, WordNatureMapping mapping) {
		super(dic.getKeySize(), mapping.getWordNatureNum());
		this.dictionary = dic;
		this.wordNatureMapping = mapping;
	}
	@Override
	public void train(Table table, int xColumnIndex, int yColumnIndex) {
		System.err.println("this type of train method is not implemented!");
	}
	
	public DoubleArrayTrie<String> getDictionary() {
		return dictionary;
	}
	public WordNatureMapping getWordNatureMapping() {
		return wordNatureMapping;
	}
	
	@Override
	public String generateModelTemplate() {
		return null;
	}

	@Override
	public String[] tag(String[] words) {
		int[] intids = POSTaggingUtils.words2intId(words, dictionary);
		int[] res = veterbi(intids);
		String[] tags = new String[res.length];
		for(int i = 0; i < tags.length; i++) {
			tags[i] = wordNatureMapping.int2natureName(res[i]);
		}
		return tags;
	}

	@Override
	public void train(LexicalAnalysisResult result) {
		System.err.println("this type of train method is not implemented!");
	}
	
	
	@Override
	public int[] veterbi(int[] observations) {
		int xLen = observations.length;
		//保存当前最佳状态由前一个状态中哪一个状态得到的
		int[][] psi = new int[xLen][stateNum];
		double[][] deltas = new double[xLen][stateNum];
		//初始化delta,第一次计算wk*f(x,y_0) 此时没有Bigram
		List<FeatureFunction> funcs = getUnigramFeatureFunction(observations, 0 );
		for(int i = 0; i  < stateNum; i++) {
			double weight = computeUnigramWeight(funcs, i);
			deltas[0][i] = weight;
		}
		//DP计算所有delta
		for(int t = 1; t < xLen; t++) {
			//获取当前位置的特征函数list
			List<FeatureFunction> curFuncs = getUnigramFeatureFunction(observations, t );
			for(int i = 0; i < stateNum; i++) {
				//找到最好的前驱状态
				deltas[t][i] = deltas[t-1][0] + computeBigramWeight(0, i);
				for(int j = 1; j < stateNum; j++) {
					double tmp = deltas[t-1][j] + computeBigramWeight(j, i);
					if(tmp > deltas[t][i]) {
						deltas[t][i] = tmp;
						psi[t][i] = j;//保存当前最佳状态由哪一个前驱状态产生
					}
				}
				//加上当前Unigram的累积权重值, 遍历特征函数特别耗时
				deltas[t][i] += computeUnigramWeight(curFuncs, i);
			}
		}
		//找到最后一个观测的最佳状态，回溯
		int[] best = new int[xLen];//保存最优预测序列
		double max = deltas[xLen-1][0];
		for(int i = 1; i < stateNum; i++) {
			if(deltas[xLen-1][i] > max) {
				max = deltas[xLen-1][i];
				best[xLen-1] = i;
			}
		}
		//回溯
		for(int i = xLen - 2; i >=0; i--) {
			best[i] = psi[i+1][best[i+1]];
		}
		return best;
	}
	
	@Override
	public String getDefaultTemplate() {
		return "# Unigram\r\n" + 
				"U00:%x[-1,0]\r\n" + 
				"U01:%x[0,0]\r\n" + 
				"U02:%x[1,0]\r\n" + 
				"U03:%x[-2,0]%x[-1,0]\r\n" + 
				"U04:%x[-1,0]%x[0,0]\r\n" + 
				"U05:%x[0,0]%x[1,0]\r\n" + 
				"U06:%x[1,0]%x[2,0]\r\n" + 
				"	            \r\n" + 
				"# Bigram\r\n" + 
				"B";
	}
	
	public static void main(String[] args) {
		SupervisedCRFPOSTagger tagger = new SupervisedCRFPOSTagger(POSTaggingUtils.getDefaultDictionary(), 
				WordNatureMapping.getCoarseWordNatureMapping());
		//tagger.setWordNatureMapping(WordNatureMapping.getCoarseWordNatureMapping());
		List<SequenceNode> nodes = POSTaggingUtils.generateNodesWithCRFormatData("D:\\\\nlp语料\\\\词性标注\\\\词性标注_crf_coarseNature.txt", "utf-8", 0, 1
				,tagger.getDictionary(), tagger.getWordNatureMapping());
		System.out.println("结点生成完成...");
		nodes = nodes.subList(0, nodes.size() /3);
		tagger.train(nodes);
		
		String[] words = new String[] {"你","最近","过得","还好","吗","？"};
		String[] res = tagger.tag(words);
		for(int i = 0; i < res.length; i++) {
			System.out.print(words[i]+"/"+res[i]);
		}
		System.out.println();
		String[] testData = IOUtils.readTextAndReturnLinesOfArray("D:\\\\nlp语料\\\\词性标注\\\\词性标注@人民日报199801_crf.txt", "utf-8");
		int testLen = (int) (testData.length);
		String[] words1 = new String[testLen];
		String[] natures = new String[testLen];
		for(int i = 0; i < testLen; i++) {
			String[] s = testData[i].split("\t");
			words1[i] = s[0];
			natures[i] = s[1].substring(0, 1).toLowerCase();
		}
		String[] predict = tagger.tag(words1);
		for(int i = 0; i < predict.length; i++) {
			predict[i] = predict[i].substring(0, 1).toLowerCase();
		}
		float accuracy = Metric.accuracyScore(predict, natures);
		System.out.println("测试集合准确率:"+accuracy);
	}

}
