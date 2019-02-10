package com.outsider.nlp.nameEntityRecognition;

import java.util.ArrayList;
import java.util.List;

import com.outsider.common.util.IOUtils;
import com.outsider.common.util.StringUtils;
import com.zhifac.crf4j.Tagger;

public class NERUtils {
	
	/**
	 * 命名实体标记后解码
	 * @param text
	 * @param predict
	 * @return
	 */
	public static List<Entity> decode(String text, String[] predict) {
		char[] x = text.toCharArray();
		if(x.length != predict.length) {
			System.err.println("x长度与标签长度不匹配!");
		}
		List<Entity> ens = new ArrayList<>();
		for(int i = 0; i < predict.length; i++) {
			Entity entity = new Entity();
			entity.setStart(-1);
			if(predict[i].equals("B_P") || predict[i].equals("M_P")) {
				int b = i;
				while(i < x.length && !predict[i].equals("E_P")) i++;
				if(i == x.length) i--;
				entity.setStart(b);
				entity.setEnd(i);
				entity.setEntityType(EntityType.PERSON_NAME);
				entity.setEntity(new String(x, b , i-b+1));
			} else if(predict[i].equals("B_L") || predict[i].equals("M_L")) {
				int b = i;
				while(i < x.length && !predict[i].equals("E_L")) i++;
				if(i == x.length) i--;
				entity.setStart(b);
				entity.setEnd(i);
				entity.setEntityType(EntityType.LOCATION);
				entity.setEntity(new String(x, b , i-b+1));
			} else if(predict[i].equals("B_O") || predict[i].equals("M_O")) {
				int b = i;
				while(i < x.length && !predict[i].equals("E_O")) i++;
				if(i == x.length) i--;
				entity.setStart(b);
				entity.setEnd(i);
				entity.setEntityType(EntityType.ORGANIZATION);
				entity.setEntity(new String(x, b , i-b+1));
			} else if(predict[i].equals("W")) {
				entity.setStart(i);
				entity.setEnd(i);
				entity.setEntityType(EntityType.SINGLE);
				entity.setEntity(new String(x, i , 1));
			}
			if(entity.getStart() != -1) {
				ens.add(entity);
			}
		}
		return ens;
	}
	
	public static List<Entity> nerTag(Tagger tagger, String text){
		//清除之前的待预测数据
		tagger.clear();
		//添加待预测数据
		for(int i = 0; i < text.length(); i++) {
			tagger.add(text.charAt(i) + "");
		}
		//预测
		tagger.parse();
		//转换为char结果并解码
		String[] predict = new String[text.length()];
		for(int i = 0; i < predict.length; i++) {
			int yInt = tagger.y(i);
			String yName = tagger.yname(yInt);
			predict[i] = yName;
		}
		return decode(text, predict);
	}
	
	public static List<Entity> getSpecificEntityType(List<Entity> ens, char type){
		List<Entity> res = new ArrayList<>();
		for(Entity entity : ens) {
			if(entity.getEntityType() == type) {
				res.add(entity);
			}
		}
		return res;
	}
	
	/**
	 * 从文本中读取实体，
	 * 格式必须满足：
	 * 例如：
	 * 中国是一个法制国家。
	 * 中国/ns 是一个法制国家。/o
	 * @param datas 传入分割好的文本内容。以上面的例子就是 [中国/ns][是一个法制国家。/o]构成的数组
	 * @return
	 */
	public static List<Entity> extractEntityFromText(String[] datas){
		List<Entity> ens = new ArrayList<>();
		// nr人名 ns地名 nt机构名
		int start = 0,end = 0;
		for(String ent : datas) {
			ent = ent.trim();
			int i = ent.lastIndexOf('/');
			if(i == -1)
				continue;
			String entityC = ent.substring(0, i);
			end += entityC.length();
			start = end - entityC.length();
			String type = ent.substring(i + 1).toLowerCase();
			if(type.equals("o")) {
				continue;
			}
			Entity entity = new Entity();
			entity.setEntity(entityC);
			entity.setStart(start);
			entity.setEnd(end-1);
			if(type.equals("nr")) {
				entity.setEntityType(EntityType.PERSON_NAME);
			} else if (type.equals("ns")) {
				entity.setEntityType(EntityType.LOCATION);
			} else if(type.equals("nt")) {
				entity.setEntityType(EntityType.ORGANIZATION);
			} else {
				System.out.println("error...");
			}
			ens.add(entity);
		}
		return ens;
	}
	
	public static void main(String[] args) {
		// nr人名 ns地名 nt机构名
		String path = "./data/ner/testright.txt";
		String[] datas = IOUtils.loadSegmentionCorpus(path, "utf-8", " ");
		List<Entity> ens = extractEntityFromText(datas);
		int count = 0;
		for(Entity entity : ens) {
			System.out.println(entity.getEntity()+"("+entity.getStart()+","+entity.getEnd()+")");
			count++;
			if(count > 100) break;
		}
	}
}
