package com.outsider.model.data;

import java.util.ArrayList;
import java.util.List;

import com.outsider.model.hmm.SequenceNode;

public class SegmentationDataConverter implements DataConverter<String[], List<SequenceNode>>{
	@Override
	public List<SequenceNode> convert(String[] words, Object... otherParameters) {
		List<SequenceNode> nodes = new ArrayList<>();
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
	
	public SegmentationDataConverter() {}
}
