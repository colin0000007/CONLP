package com.outsider.nlp.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.outsider.common.algorithm.MinimumSpanningTree;
import com.outsider.common.logger.CONLPLogger;
import com.outsider.common.util.Storable2;
import com.outsider.constants.nlp.PathConstans;

import opennlp.tools.ml.maxent.GISModel;
import opennlp.tools.ml.maxent.GISModelReader;
/**
 * 基于最大熵模型的依存句法分析
 * 注意:此class只是作为使用最大熵句法分析的接口，不提供模型的训练
 * 模型的训练使用opennlp完成
 * 
 * 2019.1.22 基本完成，几个问题：
 * （1）是否CoNLLFeatureGenerator和CoNLLSampleGenerator生成样本时会不一致导致预测会出问题
 * （2）是否增加的词上下文特征反而会起负作用？
 * 
 * @author outsider
 */
public class MaxEntDependencyParser extends AbstractDependencyParser implements Storable2{
	//依赖的openlp中的最大熵模型
	private GISModel model;
	private Logger logger = CONLPLogger.getLoggerOfAClass(MaxEntDependencyParser.class);
	public MaxEntDependencyParser() {
		logger.info("load model...");
		long start = System.currentTimeMillis();
		//加载默认路径的模型
		open(PathConstans.DEPENDENCY_PARSER_MAXENT);
		long end = System.currentTimeMillis();
		logger.info("done..."+((end - start) / 1000) + " seconds");
		/*logger.info("load default segmenter...");
		//this.segmenter = 
		logger.info("done...");
		logger.info("load default postagger...");
		//this.posTagger = 
		logger.info("done...");*/
		
	}
	
	
	@Override
	public CoNLLSentence parse(String[] words, String[] natures) {
		if(words.length != natures.length || words.length < 2) {
			return null;
		}
		//复制root节点
		String[] wordsWithROOT = new String[words.length + 1];
		String[] naturesWithROOT = new String[natures.length + 1];
		wordsWithROOT[0] = CoNLLWord.ROOT_LEMMA;
		naturesWithROOT[0] = CoNLLWord.ROOT_CPOSTAG;
		System.arraycopy(words, 0, wordsWithROOT, 1, words.length);
		System.arraycopy(natures, 0, naturesWithROOT, 1, natures.length);
		//构建单词之间的分类样本
		CoNLLSample[][] contexts = CoNLLSampleGenerator.generate(words, natures);
		//分类构建邻接矩阵，为跑最小生成树算法准备
		float[][] graph = new float[contexts.length][contexts.length];
		//保存边的一些附加信息，包括依存关系等
		Edge[][] edges = new Edge[contexts.length][contexts.length];
		for(int i = 0; i < contexts.length; i++) {
			for(int j = 0; j < contexts.length; j++) {
				//初始化graph
				graph[i][j] = Float.MAX_VALUE;
				edges[i][j] = new Edge();
				if(contexts[i][j] == null)
					continue;
				String[] context = contexts[i][j].context;
				double[] prob = model.eval(context);
				String best = model.getBestOutcome(prob);
				int index = model.getIndex(best);
				double bestProb = prob[index];
				edges[i][j].deprelaLabel = index;
				//如果依存关系预测为NULL，选择第二好的一种依存关系作为结果。
				if(best.equals(CoNLLWord.NoneDEPREL)) {
					prob[index] = 0;
					String secondBest = model.getBestOutcome(prob);
					int secondBestIndex = model.getIndex(secondBest);
					edges[i][j].deprelaLabel = secondBestIndex;
					bestProb = prob[secondBestIndex];
				}
				edges[i][j].proba = (float) bestProb;
				graph[i][j] = (float) Math.abs(Math.log(bestProb));
				
			}
		}
		//将有向图无向化，取最有可能的方向作为无向图的边
		//只遍历邻接矩阵的一半
		for(int i = 0; i < contexts.length; i++) {
			for(int j = 0; j < i; j++) {
				float best = 0;
				//因为概率取了对数和绝对值，越小代表概率越大
				if(graph[i][j] < graph[j][i]) {
					best = graph[i][j];
					edges[i][j].edgeDirection = true;
					edges[j][i].edgeDirection = false;
				} else {
					best = graph[j][i];
					edges[j][i].edgeDirection = true;
					edges[i][j].edgeDirection = false;
				}
				graph[i][j] = best;
				graph[j][i] = best;
			}
		}
		//改进：ROOT节点有且只有一个孩子，先找到最有可能的孩子，切断ROOT与其他节点的边避免一个句子出现两个核心成分！
		float maxROOT = Float.MAX_VALUE;
		int bestChild = 0;
		for(int i = 1; i < graph.length; i++) {
			if(graph[i][0] < maxROOT) {
				maxROOT = graph[i][0];
				bestChild = i;
			}
		}
		//将其他非(节点和ROOT之间的概率最大的边)切断
		for(int i = 1; i < graph.length; i++) {
			if(i != bestChild) {
				graph[i][0] = Float.MAX_VALUE;
				graph[0][i] = Float.MAX_VALUE;
			}
		}
		//构建邻接矩阵，为了跑出的结果实际上是最大生成树的结果，将概率取对数后再取绝对值作为权值送到普里姆算法中
		int[] mst = MinimumSpanningTree.primAlgorithm(graph);
		CoNLLWord[] coWords = new CoNLLWord[words.length];
		for(int i = 0; i < coWords.length; i++) {
			coWords[i] = new CoNLLWord();
			coWords[i].setID(i+1);
			coWords[i].setLEMMA(words[i]);
			coWords[i].setCPOSTAG(natures[i]);
			coWords[i].setPOSTAG(natures[i]);
		}
		for(int i = 0; i < mst.length; i++) {
			if(mst[i] != -1) {
				Edge edge = edges[mst[i]][i];
				int start = 0;
				int end = 0;
				if(edge.edgeDirection) {
					start = mst[i];
					end = i;
				} else {
					start = i;
					end = mst[i];
				}
				//可能出现有的节点没有HEAD，中心词，纠正这个错误。
				//出现第i个单词没有核心词 HEAD
				if(start != i) {
					start = i;//这里之前没写，出问题了
					//找出最可能的依存边作为结果
					float maxProba = 0;
					for(int j = 1; j < edges.length; j++) {
						if(edges[start][j].proba > maxProba) {
							maxProba = edges[start][j].proba;
							end = j;
						}
					}
				}
				String DEPRELA = model.getOutcome(edges[start][end].deprelaLabel);
				coWords[start -1].setHEAD(end);
				coWords[start -1].setDEPREL(DEPRELA);
			}
		}
		CoNLLSentence sentence = new CoNLLSentence(coWords);
		return sentence;
	}

	@Override
	public CoNLLSentence[] parse(List<String[]> words, List<String[]> natures) {
		CoNLLSentence[] sentences = new CoNLLSentence[words.size()];
		for(int i = 0; i < sentences.length; i++) {
			sentences[i] = parse(words.get(i), natures.get(i));
		}
		return sentences;
	}

	@Override
	/**
	 * 该方法不需要实现
	 * 模型不在这里保存
	 */
	@Deprecated
	public void save(String filePath) {
	}

	@Override
	public void open(String filePath) {
		GISModelReader reader = null;
		try {
			reader = new GISModelReader(new File(filePath));
			this.model = (GISModel) reader.getModel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//parsing过程中用到的数据类型
	protected class Edge{
		//无向边的实际边方向，true表示i->j（正向）,否者j->i方向。
		//例如:edge[1][0].edgeDirection = false,表示1和0之间的节点实际上的方向是0->1
		public boolean edgeDirection = true;
		//依存关系，只保存索引，具体的String标签到model中取出来
		public int deprelaLabel;
		//保存依存概率
		public float proba;
	}
	
	
	public void test(String[] words, String[] natures) {
		CoNLLSentence sentence = parse(words, natures);
		System.out.println(sentence);
		System.out.println();
	}

	@Override
	public CoNLLSentence parse(String sentence) {
		String[] words = segmenter.seg(sentence);
		String[] tags = posTagger.tag(words);
		return this.parse(words, tags);
	}

	@Override
	public CoNLLSentence[] parse(String[] sentences) {
		CoNLLSentence[] result = new CoNLLSentence[sentences.length];
		for(int i = 0;i < sentences.length; i++) {
			result[i] = parse(sentences[i]);
		}
		return result;
	}
	
	//测试CoNLLSampleGenerator和CoNLLFeatureGenerator产生的样本是否一致
	/*public static void main(String[] args) {
		String[] words = new String[] {"坚决","惩治","贪污","贿赂","等","经济","犯罪"};
		String[] natures = new String[] {"a","v","v","n","u","n","v"};
		CoNLLSample[][] samples = CoNLLSampleGenerator.generate(words, natures);
		List<String> lines = new ArrayList<String>();
		lines.add("1	坚决	坚决	a	ad	_	2	方式");
		lines.add("2	惩治	惩治	v	v	_	0	核心成分");
		lines.add("3	贪污	贪污	v	v	_	7	限定");
		lines.add("4	贿赂	贿赂	n	n	_	3	连接依存");
		lines.add("5	等	等	u	udeng	_	3	连接依存");
		lines.add("6	经济	经济	n	n	_	7	限定");
		lines.add("7	犯罪	犯罪	v	vn	_	2	受事");
		lines.add("");
		List<String> samples2 = CoNLLFeatureGenerator.makeData(lines);
		List<String> slines = new ArrayList<>();
		for(int i = 0; i < samples.length; i++) {
			for(int j = 0; j < samples.length; j++) {
				if(samples[i][j] != null) {
					String[] r = samples[i][j].context;
					StringBuilder sb = new StringBuilder();
					for(String s : r) {
						sb.append(s+" ");
					}
					slines.add(sb.toString().trim());
				}
			}
		}
		List<String> samples22 = new ArrayList<>();
		for(String s : samples2) {
			int index = s.lastIndexOf(" ");
			s = s.substring(0, index);
			samples22.add(s);
		}
		System.out.println(slines.size()+":"+samples2.size());
		int count = 0;
		for(int i = 0; i < slines.size(); i++) {
			if(slines.get(i).equals(samples22.get(i))) {
				count++;
			} else {
				System.out.println("CoNLLSampleGenerator:"+slines.get(i));
				System.out.println("CoNLLFeatureGenerator:"+samples22.get(i));
				System.out.println();
			}
			
		}
		System.out.println(count);
		System.out.println(count == slines.size());
	}*/
	
	/**
1	坚决	坚决	a	ad	_	2	方式	
2	惩治	惩治	v	v	_	0	核心成分	
3	贪污	贪污	v	v	_	7	限定	
4	贿赂	贿赂	n	n	_	3	连接依存	
5	等	等	u	udeng	_	3	连接依存	
6	经济	经济	n	n	_	7	限定	
7	犯罪	犯罪	v	vn	_	2	受事	
	 */
}
