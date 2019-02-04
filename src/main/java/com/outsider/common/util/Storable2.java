package com.outsider.common.util;

public interface Storable2 {
	/**
	 * 保存对象的属性到指定的目录
	 * 考虑到该对象可能由父类，所以也要保存父类的属性，但是不考虑实现的接口中的常量
	 * 若不指定文件名，默认生成
	 * @param filePath 文件路径
	 */
	void save(String filePath);
	/**
	 * 从某个文件中加载对象的属性并赋值，包括父类，不包括接口中的字段
	 * @param filePath 文件路径
	 */
	void open(String filePath);
}
