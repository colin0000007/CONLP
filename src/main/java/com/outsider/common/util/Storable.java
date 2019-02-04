package com.outsider.common.util;

/**
 * 基于对象利用反射保存对象的属性和加载对象的属性
 * @author outsider
 *
 */
public interface Storable {
	/**
	 * 保存对象的属性到指定的目录
	 * 考虑到该对象可能由父类，所以也要保存父类的属性，但是不考虑实现的接口中的常量
	 * 若不指定文件名，默认生成
	 * @param directory 目录
	 * @param fileName 文件名
	 */
	void save(String directory, String fileName);
	/**
	 * 从某个文件中加载对象的属性并赋值，包括父类，不包括接口中的字段
	 * @param directory 目录
	 * @param fileName 文件名，若文件名为null使用生成的默认文件名
	 */
	void open(String directory, String fileName);
	
}
