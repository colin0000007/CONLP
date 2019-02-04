package com.outsider.common.dataStructure;

import java.util.ArrayList;
import java.util.List;

import com.outsider.common.util.Storable;
import com.outsider.common.util.StorageUtils;

public class Table implements Storable{
	private int rowNum;
	private int columnNum;
	private String[][] table;
	
	public Table(int rowNum, int columnNum) {
		this.rowNum = rowNum;
		this.columnNum = columnNum;
	}
	public String[][] getTable() {
		return table;
	}
	
	public Table() {}
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
	}
	public void setTable(String[][] table) {
		this.table = table;
		this.rowNum = table.length;
		this.columnNum = table[0].length;
	}
	public String get(int row, int column) {
		if(row < 0 || row > rowNum) return null;
		return table[row][column];
	}
	public int getRowNum() {
		return rowNum;
	}
	public int getColumnNum() {
		return columnNum;
	}
	/**
	 * 获取一列的数据
	 * @param columnIndex
	 * @return
	 */
	public String[] getDataOfOneColumn(int columnIndex) {
		String[] data = new String[rowNum];
		for(int i = 0; i < rowNum; i++) {
			data[i] = table[i][columnIndex];
		}
		return data;
	}
	/**
	 * 获取一行的数据
	 * @param rowNum
	 * @return
	 */
	public String[] getDataOfOneRow(int row) {
		if(row < 0 || row >= rowNum) return null;
		return table[row];
	}
	
	/**
	 * 生成table
	 * 必须换行\n分割每一行
	 * @param srcData 源数据
	 * @param columnSplitChar 指定列之间的分隔符
	 * @return
	 */
	public static Table generateTable(String srcData, String columnSplitChar){
		if(srcData.trim().equals("")) {
			return null;
		}
		String[] lines = srcData.split("\n");
		List<String> linesChecked = new ArrayList<>();
		for(String line : lines) {
			if(!line.trim().equals("")) {
				linesChecked.add(line);
			}
		}
		lines = null;
		int row = linesChecked.size();
		int column = linesChecked.get(0).split(columnSplitChar).length;
		Table table = new Table(row, column);
		String[][] strs = new String[row][column];
		for(int i = 0; i < row; i++) {
			String[] cols = linesChecked.get(i).split(columnSplitChar);
			for(int j = 0; j < column; j++) {
				strs[i][j] = cols[j];
			}
		}
		table.setTable(strs);
		return table;
	}
	@Override
	public void save(String directory, String fileName) {
		StorageUtils.save(directory, fileName, this);
	}
	@Override
	public void open(String directory, String fileName) {
		StorageUtils.open(directory, fileName, this);
	}
}
