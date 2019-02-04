package com.outsider.common.util.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.outsider.common.algorithm.dat.darts.DoubleArrayTrie;
import com.outsider.common.util.IOUtils;
import com.outsider.common.util.StringUtils;
/**
 * 一些基本的NLP相关操作的工具类
 * @author outsider
 *
 */
public class NLPUtils {
	
	/**
	 * 将单词转换为整型id
	 * @param words 单词数组
	 * @param dictionary 字典树
	 * @return 整型id数组
	 */
	public static int[] words2intId(String[] words, DoubleArrayTrie dictionary) {
		int[] intId = new int[words.length];
		for(int i = 0; i < intId.length; i++) {
			intId[i] = dictionary.intIdOf(words[i]);
		}
		return intId;
	}
	
	/**
	 * 将字符串数组的每一个字符串中的字符直接转换为Unicode码
	 * @param strs 字符串数组
	 * @return Unicode值
	 */
	public static List<int[]> strs2int(String[] strs) {
		List<int[]> res = new ArrayList<>(strs.length);
		for(int i = 0; i < strs.length;i++) {
			int[] O = new int[strs[i].length()];
			for(int j = 0; j < strs[i].length();j++) {
				O[j] = strs[i].charAt(j);
			}
			res.add(O);
		}
		return res;
	}
	
	
	/**
	 * 将文章分割为句子
	 * 以后再考虑多线程分词
	 * @param article
	 * @return
	 */
	public static String[] cutSentences(String article) {
		//。？！.?!
		
		return null;
	}
	
	
	/**
	 * 解析人民日报这种类型的语料中的实体转换为标签。
	 * 实体类别:人名，地名，组织名。
	 * 人民日报语料中的人名的姓和名是分开的。
	 * nt是机构名，ns是地名，nr是人名
	 * O,P_B,P_M,P_E,L_B,L_M,L_E,O_B,O_M,O_E
	 * @param text
	 * @return 
	 * O,P_B,P_M,P_E,L_B,L_M,L_E,O_B,O_M,O_E
	 * @deprecated 没有弄完，太麻烦了，使用微软亚洲研究院的语料
	 */
	public static String[][] parseNERcorpus(String text) {
		Pattern p1 = Pattern.compile("\\[[^\\[\\]]+\\]\\w{1,5}\\s");
		char[] chs = text.toCharArray();
		Matcher m1 = p1.matcher(text);
		StringBuilder sb = new StringBuilder();
		int b = 0;
		int e = 0;
		while( m1.find()) {
			String s = m1.group();
			e = m1.start();
			//e-n是不包含[]的部分
			//处理不包含[]的部分
			String tmp1 = text.substring(b,e).trim();
			String[] tmp1s = tmp1.split("  ");
			
			for(int i = 0; i < tmp1s.length; i++) {
				tmp1s[i] = tmp1s[i].trim();
				if(tmp1s[i].equals("")) continue;
				String[] xy = tmp1s[i].split("/");
				if(xy.length != 2) {
					System.out.println(tmp1s[i]);
				}
				char[] xs = xy[0].toCharArray();
				if(xy[1].equals("nt")) {
					sb.append(xs[0]+"\tO_B\n");
					for(int k = 1; k < xs.length - 1; k++) {
						sb.append(xs[k]+"\tO_M\n");
					}
					sb.append(xs[xs.length - 1]+"\tO_E\n");
				} else if(xy[1].equals("ns")) {
					sb.append(xs[0]+"\tL_B\n");
					for(int k = 1; k < xs.length - 1; k++) {
						sb.append(xs[k]+"\tL_M\n");
					}
					sb.append(xs[xs.length - 1]+"\tL_E\n");
				} else if(xy[1].equals("nr")) {
					//合并姓名
					int nrb = i;
					while(i < tmp1s.length && tmp1s[i].split("/")[1].equals("nr")) i++;
					String name = "";
					for(int j = nrb; j < i; j++) {
						name += tmp1s[j].split("/")[0];
					}
					char[] nrc = name.toCharArray();
					sb.append(nrc[0]+"\tP_B\n");
					for(int k = 1; k < nrc.length - 1; k++) {
						sb.append(nrc[k]+"\t+P_M\n");
					}
					sb.append(nrc[nrc.length - 1]+"\tP_E\n");
					System.out.println("name:"+name);
					i--;
				} else {
					for(char c : xs) {
						sb.append(c+"\t"+"O\n");
					}
				}
			}
			
			b = m1.end();
			//处理包含[]的部分
			for(int i = e; i <= b; i++) {
			}
		}
		return null;
	}

	
	
	/**
	 * 转换微软亚洲研究院的命名实体识别语料为CRF格式
	 * nt是机构名，ns是地名，nr是人名
	 * O,P_B,P_M,P_E,L_B,L_M,L_E,O_B,O_M,O_E
	 * W_P,W_L,W_O 单字实体
	 * W单字实体，不区分是什么单字实体
	 * @return 格式示例：
	 * 	李	P_B
	 * 	明	P_E
	 */
	public static String parseNERCorpusOfMASR2crformat(String text) {
		text = StringUtils.fullWidthChar2HalfWidthChar(text.trim());
		String[] xys = text.split(" ");
		StringBuilder sb = new StringBuilder();
		for(String xy : xys) {
			int inde = xy.lastIndexOf("/");
			String[] xySplit = new String[2];
			xySplit[0] = xy.substring(0,inde);
			xySplit[1] = xy.substring(inde+1);
			char[] xchs = xySplit[0].toCharArray();
			int len = xchs.length;
			String[] labels = new String[3];
			boolean entity = true;
			if(xySplit[1].equals("o")) {
				for(char c : xchs) {
					sb.append(c+"\tO\n");
				}
				entity = false;
			} else if(xySplit[0].length() == 1) {
				sb.append(xchs[0]+"\tW\n");
				entity = false;
			} else if(xySplit[1].equals("nt")) {
				labels[0] = "B_O";
				labels[1] = "M_O";
				labels[2] = "E_O";
			} else if(xySplit[1].equals("ns")) {
				labels[0] = "B_L";
				labels[1] = "M_L";
				labels[2] = "E_L";
			}else if(xySplit[1].equals("nr")) {
				labels[0] = "B_P";
				labels[1] = "M_P";
				labels[2] = "E_P";
			} else {
				System.out.println("error...:");
			}
			if(entity) {
				sb.append(xchs[0]+"\t"+labels[0]+"\n");
				for(int k = 1; k < len - 1; k++) {
					sb.append(xchs[k]+"\t"+labels[1]+"\n");
				}
				sb.append(xchs[len-1]+"\t"+labels[2]+"\n");
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		//String s = fullWidthChar2HalfWidthChar("19980101-01-001-016/m  谢谢/v  ！/w  （/w  新华社/nt  北京/ns  １２月/t  ３１日/t  电/n  ）/w  ");
		//19980101-01-001-016/m  谢谢/v  ！/w  （/w  新华社/nt  北京/ns  １２月/t  ３１日/t  电/n  ）/w  
		//System.out.println(s);
		String path = "D:\\nlp语料\\命名实体识别\\MSRA\\train1.txt";
		String text = IOUtils.readText(path, "utf-8");
		String res = parseNERCorpusOfMASR2crformat(text);
		IOUtils.writeTextData2File(res, "D:\\\\nlp语料\\\\命名实体识别\\\\MSRA\\\\train1_crf.txt", "utf-8");
	}
}
