package com.outsider.nlp.nameEntityRecognition;

import java.util.List;

import com.outsider.model.hmm.FirstOrderGeneralHMM;
/**
 * 一阶HMM命名实体识别
 * 未实现
 * @author outsider
 * @deprecated
 *
 */
public class FirstOrderHMMNER extends FirstOrderGeneralHMM implements NER{

	@Override
	public List<Entity> extractEntity(String text) {
		// TODO Auto-generated method stub
		return null;
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
