package com.outsider.nlp.segmenter;

import java.util.ArrayList;
import java.util.List;

import com.outsider.common.dataStructure.Table;
import com.outsider.common.util.StringUtils;
import com.outsider.model.data.CRFSegmentationTableDataConverter;
import com.outsider.model.data.DataConverter;
import com.outsider.model.hmm.SequenceNode;

public class SegmentationUtils {
	/**
	 * 将字符串数组的每一个字符串中的字符直接转换为Unicode码
	 * @param strs 字符串数组
	 * @return Unicode值
	 */
	public static List<int[]> strs2int(String[] strs) {
		List<int[]> res = new ArrayList<>(strs.length);
		for(int i = 0; i < strs.length;i++) {
			int[] O = new int[strs[i].length()];
			for(int j = 0; j < strs[i].length();j++) {
				O[j] = strs[i].charAt(j);
			}
			res.add(O);
		}
		return res;
	}
	
	public static int[] str2int(String str) {
		return strs2int(new String[] {str}).get(0);
	}
	/**
	 * 根据预测结果解码
	 * BEMS 0123
	 * @param predict 预测结果
	 * @param sentence 句子
	 * @return
	 */
	public static String[] decode(int[] predict, String sentence) {
		List<String> res = new ArrayList<>();
		char[] chars = sentence.toCharArray();
		for(int i = 0; i < predict.length;i++) {
			if(predict[i] == 0 || predict[i] == 1) {
				int a = i;
				while(predict[i] != 2) {
					i++;
					if(i == predict.length) {
						break;
					}
				}
				int b = i;
				if(b == predict.length) {
					b--;
				}
				res.add(new String(chars,a,b-a+1));
			} else {
				res.add(new String(chars,i,1));
			}
		}
		String[] s = new String[res.size()];
		return res.toArray(s);
	}
	
	/**
	 * 合并多份分词语料
	 * @param corpuses
	 * @return
	 */
	public static String[] mergeCorpus(List<String[]> corpuses) {
		String[] s = new String[0];
		for(int i = 0; i < corpuses.size(); i++) {
			String[] co = corpuses.get(i);
			s = StringUtils.concat(s, co);
		}
		return s;
	}
	
	/**
	 * 将分词语料抓换为int状态数组，BMES
	 * @param words 单词数组
	 * @param charLen 字符个数
	 * @return
	 */
	public static int[] segmentationCorpus2state(String[] words, int charLen) {
		//otherParameters[0] 长度参数
		int[] state = new int[charLen];
		int count = 0;
		for(int i = 0; i < words.length; i++) {
			/*if(words[i].trim().length()!=words[i].length()) {
				System.out.println("出现能被trim的字符/"+words[i]);
			}*/
			words[i] = words[i].trim();
			if(words[i].length() == 1) { //单字成词
				state[count] = 3;
				count++;
			} else if(words[i].length() == 2) {//双字成词
				state[count] = 0;
				state[count+1] = 2;
				count+=2;
			} else if(words[i].length() >2){//大于2个字符成词
				int len = words[i].length();
				//之前这里写出了bug，必须考虑顺序，不能交换位置
				state[count] = 0;
				for(int j = count+1; j < count+len-1; j++) {
					state[j] = 1;
				}
				state[count+len-1] = 2;
				count+=len;
			}
		}
		return state;
	}
	/**
	 * CRF格式的分词数据转换为序列结点
	 * @param table
	 * @param xColumnIndex
	 * @param yColumnIndex
	 * @return
	 */
	public List<SequenceNode> CRFSegmentationTable2SequenceNodes(Table table, int xColumnIndex, int yColumnIndex){
		DataConverter<Table, List<SequenceNode>> converter = new CRFSegmentationTableDataConverter(xColumnIndex, yColumnIndex);
		List<SequenceNode> nodes = converter.convert(table);
		return nodes;
	}
	
	/**
	 * 根据预测结果解码
	 * BEMS
	 * @param predict 预测结果
	 * @param sentence 句子
	 * @return
	 */
	public static String[] decode(char[] predict, String sentence) {
		List<String> res = new ArrayList<String>();
		char[] chars = sentence.toCharArray();
		for(int i = 0; i < predict.length;i++) {
			if(predict[i] == 'B' || predict[i] == 'M') {
				int a = i;
				while(predict[i] != 'E') {
					i++;
					if(i == predict.length) {
						break;
					}
				}
				int b = i;
				if(b == predict.length) {
					b--;
				}
				res.add(new String(chars,a,b-a+1));
			} else {
				res.add(new String(chars,i,1));
			}
		}
		String[] s = new String[res.size()];
		return res.toArray(s);
	}
	
	
}
