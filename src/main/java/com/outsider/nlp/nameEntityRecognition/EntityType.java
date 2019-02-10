package com.outsider.nlp.nameEntityRecognition;

import java.util.HashMap;
import java.util.Map;


public class EntityType {
	public static char PERSON_NAME = 'P';
	public static char LOCATION = 'L';
	public static char ORGANIZATION = 'O'; 
	public static char SINGLE = 'W';
	
	public static Map<String, Integer> tag2id;
	public static String[] id2tag;
	static {
		tag2id = new HashMap<>(11);
		tag2id.put("B_P", 0);
		tag2id.put("M_P", 1);
		tag2id.put("E_P", 2);
		
		tag2id.put("B_L", 3);
		tag2id.put("M_L", 4);
		tag2id.put("E_L", 5);
		
		tag2id.put("B_O", 6);
		tag2id.put("M_O", 7);
		tag2id.put("E_O", 8);
		
		tag2id.put("W", 9);
		
		tag2id.put("O", 10);
		
		id2tag = new String[11];
		id2tag[0] = "B_P";
		id2tag[1] = "M_P";
		id2tag[2] = "E_P";
		id2tag[3] = "B_L";
		id2tag[4] = "M_L";
		id2tag[5] = "E_L";
		id2tag[6] = "B_O";
		id2tag[7] = "M_O";
		id2tag[8] = "E_O";
		id2tag[9] = "W";
		id2tag[10] = "O";
	}
	
}
