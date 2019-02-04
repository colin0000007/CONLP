package com.outsider.nlp.postagger;

import java.util.List;

import com.outsider.common.algorithm.dat.darts.DoubleArrayTrie;
import com.outsider.model.data.DataConverter;
import com.outsider.model.data.POSTaggingDataConverter;
import com.outsider.model.hmm.SecondOrderGeneralHMM;
import com.outsider.model.hmm.SequenceNode;
import com.outsider.nlp.lexicalanalyzer.LexicalAnalysisResult;

/**
 * 二阶HMM的词性标注器，和一阶没多大差别。
 * @author outsider
 *
 */
public class SecondOrderHMMPOSTagger extends SecondOrderGeneralHMM implements POSTagger{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static DoubleArrayTrie dictionary = POSTaggingUtils.getDefaultDictionary();
	private static WordNatureMapping wordNatureMapping = WordNatureMapping.getDefault();
	
	public SecondOrderHMMPOSTagger() {
		this(wordNatureMapping.getWordNatureNum(),dictionary.getKeySize());
	}
	

	public SecondOrderHMMPOSTagger(int stateNum, int observationNum, double[] pi, double[][] transferProbability1,
			double[][] emissionProbability) {
		super(stateNum, observationNum, pi, transferProbability1, emissionProbability);
	}

	public SecondOrderHMMPOSTagger(int stateNum, int observationNum) {
		super(stateNum, observationNum);
		reInitParameters();
	}
	
	public void setWordNatureMapping(WordNatureMapping wordNatureMapping) {
		this.wordNatureMapping = wordNatureMapping;
	}
	public DoubleArrayTrie getDictionary() {
		return dictionary;
	}
	public void setDictionary(DoubleArrayTrie dictionary) {
		this.dictionary = dictionary;
	}
	public WordNatureMapping getWordNatureMapping() {
		return wordNatureMapping;
	}
	@Override
	public String[] tag(String[] words) {
		int[] wordIntIds = POSTaggingUtils.words2intId(words, dictionary);
		/**
		 * 获取接口POSTagger中传来的待预测的词序列
		 */
		//原始预测
		int[] rawPredict = super.predict(wordIntIds);
		/**
		 * 使用正则处理，处理原则：
		 * 若词中含有数字，那么标注为m
		 * 若词中含有英文或其他外文(其他外文暂时不考虑)，标注为x
		 * 暂时不考虑：
		 * 可以后续添加URL等的标注
		 */
		String numberReg = "[0-9]+";//没有考虑全角的数字
		String otherLanguageReg = "[a-zA-Z]+";
		String[] res = new String[rawPredict.length];
		for(int i = 0; i < words.length; i++) {
			if(words[i].matches(numberReg)) {
				res[i] = "m";
			} else if(words[i].matches(otherLanguageReg)) {
				res[i] = "x";
			} else {
				res[i] = wordNatureMapping.int2natureName(rawPredict[i]);
			}
		}
		return res;
	}
	@Override
	protected void solve(List<SequenceNode> nodes) {
		//遍历序列开始训练
		pi[nodes.get(0).getState()]++;
		emissionProbability[nodes.get(0).getState()][nodes.get(0).getNodeIndex()]++;
		for(int i = 1;i < nodes.size(); i++) {
			SequenceNode node = nodes.get(i);
			//需要处理词典中没有的词的情况，也就是nodeIndex=-1
			if(node.getNodeIndex() == -1) {
				continue;
			}
			//状态统计
			pi[node.getState()]++;
			//状态转移统计
			transferProbability1[nodes.get(i-1).getState()][node.getState()]++;
			//状态下观测分布统计
			emissionProbability[node.getState()][node.getNodeIndex()]++;
		}
	}
	
	@Override
	public int[] verterbi(int[] O) {
		double[][] deltas = new double[O.length][this.stateNum];
		//保存deltas[t][i]的值是由上一个哪个状态产生的
		int[][] states = new int[O.length][this.stateNum];
		//初始化deltas[0][]
		for(int i = 0;i < this.stateNum; i++) {
			/**
			 * 如果出现未登陆词，那么认为发射概率比INFINITY还小，这里取1.5倍的INFINITY
			 */
			if(O[0]==-1)
				deltas[0][i] = pi[i] + 1.5*INFINITY;
			else
				deltas[0][i] = pi[i] + emissionProbability[i][O[0]];
		}
		//计算deltas
		for(int t = 1; t < O.length; t++) {
			for(int i = 0; i < this.stateNum; i++) {
				deltas[t][i] = deltas[t-1][0]+transferProbability1[0][i];
				for(int j = 1; j < this.stateNum; j++) {
					double tmp = deltas[t-1][j]+transferProbability1[j][i];
					if (tmp > deltas[t][i]) {
						deltas[t][i] = tmp;
						states[t][i] = j;
					}
				}
				/**
				 * 如果出现未登陆词，那么认为发射概率比INFINITY还小，这里取1.5倍的INFINITY
				 */
				if(O[t]==-1)
					deltas[t][i] += 1.5*INFINITY;
				else
					deltas[t][i] += emissionProbability[i][O[t]];
			}
		}
		//回溯找到最优路径
		int[] predict = new int[O.length];
		double max = deltas[O.length-1][0];
		for(int i = 1; i < this.stateNum; i++) {
			if(deltas[O.length-1][i] > max) {
				max = deltas[O.length-1][i];
				predict[O.length-1] = i;				
			}
		}
		for(int i = O.length-2;i >= 0;i-- ) {
			predict[i] = states[i+1][predict[i+1]];
		}
		return predict;
	}

	@Override
	public void train(LexicalAnalysisResult result) {
		DataConverter<LexicalAnalysisResult, List<SequenceNode>> converter = new POSTaggingDataConverter(dictionary);
		List<SequenceNode> nodes = converter.convert(result);
		super.train(nodes);
	}
	/**
	 * 传入CRF格式的数据进行训练
	 * 即: 词语\t词性\n
	 * @param lines
	 * @param wordColumnIndex 词所在列的索引
	 * @param natureColumnIndex 词性所在列的索引
	 */
	public void trainWithCRFormatData(List<String> lines, int wordColumnIndex, int natureColumnIndex) {
		//将data转换为LexicalAnalysisResult
		LexicalAnalysisResult result = new LexicalAnalysisResult();
		String[] words = new String[lines.size()];
		String[] natures = new String[lines.size()];
		for(int i = 0; i < lines.size(); i++) {
			String[] s = lines.get(i).split("\t");
			words[i] = s[wordColumnIndex];
			words[i] = s[natureColumnIndex];
		}
		result.setSegmentationResult(words);
		result.setPostaggingResult(natures);
		this.train(result);
	}

}
