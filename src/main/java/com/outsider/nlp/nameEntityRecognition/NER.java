package com.outsider.nlp.nameEntityRecognition;

import java.util.List;
import java.util.Map;

import com.outsider.model.Model;
/**
 * 命名实体识别接口
 * @author outsider
 *
 */
public interface NER{
	/**
	 * 抽取实体，返回一个map，key是实体的类别，value是该类别下的实体
	 * @param text
	 * @return
	 */
	public List<Entity> extractEntity(String text);
	/**
	 * 获取人名实体
	 * @param text
	 * @return
	 */
	public List<Entity> getPersonNameEntity(String text);
	/**
	 * 获取地名实体
	 * @param text
	 * @return
	 */
	public List<Entity> getLocationEntity(String text);
	
	/**
	 * 获取组织名
	 * @param text
	 * @return
	 */
	public List<Entity> getOrganizationEntity(String text);
	
	
}
