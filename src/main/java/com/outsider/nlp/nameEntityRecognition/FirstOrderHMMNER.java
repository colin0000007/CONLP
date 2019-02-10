package com.outsider.nlp.nameEntityRecognition;

import java.util.List;

import com.outsider.common.dataStructure.Table;
import com.outsider.common.util.IOUtils;
import com.outsider.model.data.NERCRFDataConverter;
import com.outsider.model.hmm.FirstOrderGeneralHMM;
import com.outsider.model.hmm.SequenceNode;
import com.outsider.nlp.segmenter.SegmentationUtils;
/**
 * 一阶HMM命名实体识别
 * @author outsider
 *
 */
public class FirstOrderHMMNER extends FirstOrderGeneralHMM implements NER{
	
	public FirstOrderHMMNER() {
		super(EntityType.id2tag.length, 65536);
	}

	public FirstOrderHMMNER(int stateNum, int observationNum) {
		super(stateNum, observationNum);
	}

	@Override
	public List<Entity> extractEntity(String text) {
		int[] xids = SegmentationUtils.str2int(text);
		int[] yids = verterbi(xids);
		String[] tags = new String[yids.length];
		for(int i = 0; i < tags.length; i++) {
			tags[i] = EntityType.id2tag[yids[i]];
		}
		return NERUtils.decode(text, tags);
	}

	@Override
	public List<Entity> getPersonNameEntity(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Entity> getLocationEntity(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Entity> getOrganizationEntity(String text) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
