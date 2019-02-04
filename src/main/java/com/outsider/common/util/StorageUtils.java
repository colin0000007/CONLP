package com.outsider.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
/**
 * 对象存储工具
 * 将对象所有非final字段保存到一个文件中
 * 可以用于模型的保存，但只适合小模型的保存
 * @author outsider
 *
 */
public class StorageUtils {
	public static void open(String directory, String fileName, Object obj) {
		Class currentClass = obj.getClass();
		if(fileName == null || fileName.trim().equals("")) {
			fileName = currentClass.getSimpleName()+"_fields";
		}
		List<Object[]> allValues = (List<Object[]>) IOUtils.readObject(directory+"//"+fileName);
		int count = 0;
		while(!currentClass.getName().equals("java.lang.Object")) {
			Field[] fields = currentClass.getDeclaredFields();
			fields = filterFinalField(fields);
			if(fields.length == 0) {
				currentClass = currentClass.getSuperclass();
				continue;
			}
			Object[] objs = (Object[]) allValues.get(count);
			for(int i = 0; i < fields.length; i++) {
				try {
					fields[i].setAccessible(true);
					fields[i].set(obj, objs[i]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			currentClass = currentClass.getSuperclass();
			count++;
		}
	}
	
	/**
	 * 过滤掉含有final的字段
	 * @param fields
	 * @return
	 */
	private static Field[] filterFinalField(Field[] fields) {
		List<Field> noFinal = new ArrayList<>();
		for(int i = 0; i < fields.length; i++) {
			int modifiers = fields[i].getModifiers();
			//只处理final字段
			if(!Modifier.isFinal(modifiers)) {
				noFinal.add(fields[i]);
			}
		}
		Field[] res = new Field[noFinal.size()];
		noFinal.toArray(res);
		return res;
	}
	
	/**
	 * 保存对象的属性到指定的目录
	 * 考虑到该对象可能由父类，所以也要保存父类的属性，但是不考虑实现的接口中的常量
	 * 若不指定文件名，默认生成
	 * @param directory 目录
	 * @param fileName 文件名
	 */
	public static void save(String directory, String fileName, Object obj) {
		Class currentClass = obj.getClass();
		List<Object[]> allValues = new ArrayList<>();
		if(fileName == null || fileName.trim().equals("")) {
			fileName = currentClass.getSimpleName()+"_fields";
		}
		while(!currentClass.getName().equals("java.lang.Object")) {
			//处理字段的保存
			Field[] fields = currentClass.getDeclaredFields();
			fields = filterFinalField(fields);
			if(fields.length == 0) {
				//这里写出了bug，没有对currentClass进行更新，会造成死循环
				currentClass = currentClass.getSuperclass();
				continue;
			}
			try {
				Object[] values = new Object[fields.length];
				for(int i = 0; i < values.length;i++) {
					fields[i].setAccessible(true);
					values[i] = fields[i].get(obj);
				}
				allValues.add(values);
			} catch (Exception e) {
				e.printStackTrace();
			}
			currentClass = currentClass.getSuperclass();
		}
		IOUtils.writeObject2File(directory+"//"+fileName, allValues);
	}
}
