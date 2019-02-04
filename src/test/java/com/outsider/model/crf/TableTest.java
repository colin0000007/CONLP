package com.outsider.model.crf;

import com.outsider.common.dataStructure.Table;

public class TableTest {
	public static void main(String[] args) {
		String data = "Äã B\nºÃ E";
		Table table = Table.generateTable(data, " ");
		String c = table.get(0, 1);
		System.out.println(c);
	}
}
