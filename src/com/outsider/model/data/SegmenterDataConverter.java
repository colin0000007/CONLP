package com.outsider.model.data;

import java.util.ArrayList;
import java.util.List;

import com.outsider.model.hmm.SequenceNode;

public class SegmenterDataConverter implements DataConverter<String[], List<SequenceNode>>{
	private static final SegmenterDataConverter obj = new SegmenterDataConverter(); 
	
	@Override
	public List<SequenceNode> rawData2ConvertedData(String[] rawData, Object... otherParameters) {
		List<SequenceNode> nodes = new ArrayList<>();
		String[] words = rawData;
		for(String token : words) {
			token = token.trim();
			if(token.length() == 1) { //单字成词
				SequenceNode node = new SequenceNode(token.charAt(0), 3);
				nodes.add(node);
			} else if(token.length() == 2) {//双字成词
				nodes.add(new SequenceNode(token.charAt(0), 0));
				nodes.add(new SequenceNode(token.charAt(1),  2));
			} else if(token.length() >2){//大于2个字符成词
				int len = token.length();
				//之前这里写出了bug，必须考虑顺序，不能交换位置
				nodes.add(new SequenceNode(token.charAt(0), 0));
				for(int i = 1; i < len-1;i++) {
					nodes.add(new SequenceNode(token.charAt(i), 1));
				}
				nodes.add(new SequenceNode(token.charAt(len-1), 2));
			}
		}
		return nodes;
	}
	
	public int[] rawData2StateIndex(String[] rawData, Object... otherParameters) {
		String[] words = rawData;
		int[] state = new int[(int) otherParameters[0]];
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
	

	
	private SegmenterDataConverter() {}
	public static SegmenterDataConverter getInstance() {
		return obj;
	}

	
	
}
