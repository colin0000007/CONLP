package com.outsider.common.algorithm.daTrieTree;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Double Array Trie Tree接口
 * @author outsider
 *
 */
public interface DATrieTreeInterface {
	/**
	 * 根据词来构建树
	 * @param tokens 词列表
	 */
	void build(List<String> tokens);
	/**
	 * 插入一个词到树中
	 * @param token 词
	 */
	void insert(String token);
	/**
	 * 匹配前缀是tokenPrefix的所有词
	 * @param tokenPrefix 前缀
	 * @return
	 */
	List<String> match(String tokenPrefix);
	/**
	 * 字符编码
	 * @param c 字符
	 * @return 编码
	 */
	int code(char c);
	/**
	 * 重构数组大小
	 * @param size 大小
	 */
	void resize(int size);
	/**
	 * 判断一个词是否存在
	 * @param token 词
	 * @return
	 */
	boolean exist(String token);
	/**
	 * 获取一个词的id
	 * @param token 词
	 * @return 字符串id
	 */
	String idOf(String token);
	
}
