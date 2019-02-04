package com.outsider.model.data;

import java.util.ArrayList;
import java.util.List;

import com.outsider.common.dataStructure.Table;
import com.outsider.model.hmm.SequenceNode;
import com.outsider.nlp.nameEntityRecognition.EntityType;

public class NERCRFDataConverter implements DataConverter<Table, List<SequenceNode>>{
	private int xColumn;
	private int yColumn;
	
	public NERCRFDataConverter(int xColumn, int yColumn) {
		super();
		this.xColumn = xColumn;
		this.yColumn = yColumn;
	}
	@Override
	public List<SequenceNode> convert(Table rawData, Object... otherParameters) {
		List<SequenceNode> nodes = new ArrayList<>(rawData.getRowNum());
		for(int i = 0; i < rawData.getRowNum(); i++) {
			String x = rawData.get(i, xColumn);
			String y = rawData.get(i, yColumn);
			int yid = EntityType.tag2id.get(y);
			int xid = x.charAt(0);
			SequenceNode node = new SequenceNode(xid, yid);
			nodes.add(node);
		}
		return nodes;
	}
	
}
