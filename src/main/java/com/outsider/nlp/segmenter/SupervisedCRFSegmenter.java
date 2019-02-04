package com.outsider.nlp.segmenter;

import java.util.ArrayList;
import java.util.List;

import com.outsider.common.dataStructure.Table;
import com.outsider.model.crf.FeatureFunction;
import com.outsider.model.crf.SupervisedCRF;
import com.outsider.model.data.CRFSegmentationTableDataConverter;
import com.outsider.model.data.DataConverter;
import com.outsider.model.data.SegmentationDataConverter;
import com.outsider.model.hmm.SequenceNode;
/**
 * 监督学习CRF分词器
 * @author outsider
 *
 */
public class SupervisedCRFSegmenter extends SupervisedCRF implements Segmenter{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1;

	/**
	 * 
	 */
	
	
	public SupervisedCRFSegmenter() {
		super(65536, 4);
	}

	public SupervisedCRFSegmenter(int observationNum, int stateNum) {
		super(observationNum, stateNum);
	}

	/**
	 * 默认的特征模板
	 * 下面的模板时HanLP中的分词模板
	 * @return
	 */
	@Override
	public String getDefaultTemplate() {
		return "# Unigram\n" +
	            "U0:%x[-1,0]\n" +
	            "U1:%x[0,0]\n" +
	            "U2:%x[1,0]\n" +
	            "U3:%x[-2,0]%x[-1,0]\n" +
	            "U4:%x[-1,0]%x[0,0]\n" +
	            "U5:%x[0,0]%x[1,0]\n" +
	            "U6:%x[1,0]%x[2,0]\n" +
	            "\n" +
	            "# Bigram\n" +
	            "B";
		/*return "U00:%x[-2,0]\n" + 
		"U01:%x[-1,0]\n" + 
		"U02:%x[0,0]\n" + 
		"U03:%x[1,0]\n" + 
		"U04:%x[2,0]\n" +
		"U05:%x[-2,0]/%x[-1,0]/%x[0,0]\n" + 
		"U06:%x[-1,0]/%x[0,0]/%x[1,0]\n" + 
		"U07:%x[0,0]/%x[1,0]/%x[2,0]\n" + 
		"U08:%x[-1,0]/%x[0,0]\n" + 
		"U09:%x[0,0]/%x[1,0]\n" + 
		"#Bigram\n"+
		"B";*/
	}
	
	@Override
	public String[] seg(String text) {
		int[] intids = SegmentationUtils.strs2int(new String[] {text}).get(0);
		int[] predict = this.predict(intids);
		return SegmentationUtils.decode(predict, text);
	}

	@Override
	public List<String[]> seg(String[] texts) {
		List<int[]> intidList = SegmentationUtils.strs2int(texts);
		List<String[]> result = new ArrayList<>(intidList.size());
		for(int i = 0; i < texts.length; i++) {
			int[] predict = this.predict(intidList.get(i));
			String[] res = SegmentationUtils.decode(predict, texts[i]);
			result.add(res);
		}
		return result;
	}

	@Override
	public void train(String[] words) {
		List<SequenceNode> nodes = new SegmentationDataConverter().convert(words);
		train(nodes);
	}

	@Override
	public void train(ArrayList<String[]> corpuses) {
		//合并多个数组
		 String[] words = SegmentationUtils.mergeCorpus(corpuses);
		//训练
		List<SequenceNode> nodes = new SegmentationDataConverter().convert(words);
		train(nodes);
	}
	
	@Override
	public String generateModelTemplate() {
		char[] stateChar = new char[] {'B','M','E','S'};
		/*Map<Integer, String> outOfBoundaryChar = new HashMap<>();
		String base = "_B";
		for(int i = 0; i < maxOffsetOfTemplate; i++) {
			outOfBoundaryChar.put(-(i+1), base+"_"+(i+1));
			outOfBoundaryChar.put(i+observationNum, base+"+"+(i+1));
		}*/
		StringBuilder sb = new StringBuilder();
		for(FeatureFunction featureFunction : featureFunctionTrie.getValues()) {
			double[] weight = featureFunction.getWeight();
			int[] x = featureFunction.getX();
			String s = "";
			for(int i = 0;i < x.length; i++) {
				/*if(x[i] < 0 || x[i] >= observationNum) {
					s += outOfBoundaryChar.get(x[i])+"/";
					continue;
				} */
				s+=(char)x[i]+"/";
			}
			s = s.substring(0,s.length()-1);
			sb.append("U"+featureFunction.getFeatureTemplateNum()+":"+s+"\n");
			for(int i = 0; i < weight.length;i++) {
				if(weight[i] != 0) {
					sb.append("\t"+stateChar[i]+":"+weight[i]+"\n");
				}
			}
		}
		return sb.toString();
	}

	@Override
	public void train(Table table, int xColumnIndex, int yColumnIndex) {
		DataConverter<Table, List<SequenceNode>> converter = new CRFSegmentationTableDataConverter(xColumnIndex, yColumnIndex);
		List<SequenceNode> nodes = converter.convert(table);
		train(nodes);
	}
	
	
}
