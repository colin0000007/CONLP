package com.outsider.common.algorithm.daTrieTree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.outsider.common.util.IOUtils;
/**
 * 实现说明Double Array Trie Tree：
 * 以base[n] = -2表示叶子节点
 * 以base[0] = 1表示根节点
 * 递归构建：深度优先
 * 编码直接使用字符的Unicode值，没有解决稀疏问题
 * 构建速度较慢
 * 没有对无前缀的词做tail处理，单独成一个node节点
 * 只适合构建静态树
 * @author outsider
 *
 */
public abstract class DATrieTree{
	protected int size = 655350;
	protected int[] check;
	protected int[] base;
	protected int arrMaxLen; //base最长能有多大
	protected int maxDepth;
	protected int maxCode;
	protected int minCode;
	protected int tokenSize;
	protected int rootBaseValue = 1;
	public DATrieTree() {
	}
	public DATrieTree(List<String> tokens) {
		build(tokens);
	}
	private void init() {
		size = (int) (arrMaxLen / 0.01);
		base = new int[size];
		base[0] = rootBaseValue;//根节点
		check = new int[size];
	}
	public void build(List<String> tokens) {
		//深度为1时的所有不重复节点，也就是根节点下面的节点,词的第一个字符
		Set<Character> parentChars = new HashSet<>();
		tokenSize = tokens.size();
		for(int i = 0; i < tokenSize; i++) {
			parentChars.add(tokens.get(i).charAt(0));
			int len = tokens.get(i).length();
			arrMaxLen += len;
			maxDepth  = len > maxDepth ? len : maxDepth;
			String s = tokens.get(i);
			for(int j = 0; j < len; j++) {
				int u = code(s.charAt(j));
				maxCode = u > maxCode ? u : maxCode;
				minCode = u < minCode ? u: minCode;
			}
		}
		//根据空间使用率来初始化大小
		init();
		//终于找到原因了：
		//因为第一层父节点的base索引值是确定的，如果不设置为占用，
		//那么可能在生成孩子节点时被占用，这就导致了问题
		////必须对父节点base占位
		for(char c : parentChars ) {
			base[base[0]+code(c)] = -2;
		}
		//递归构建
		for(char c : parentChars ) {
			walk(tokens, 1, c, 0);
		}
	}
	/**
	 * 
	 * @param tokens
	 * @param depth 指父节点所在的树的深度
	 * @param parentChar
	 * @param lastTransferBaseIndex 转移到父节点的转移基数的index
	 */
	public void walk(List<String> tokens, int depth, char parentChar, int lastTransferBaseIndex) {
		Set<Character> childChars = new TreeSet<Character>();
		//寻找当前字符的所有子节点
		for(int i = 0; i < tokenSize; i++) {
			//词的长度必须大于depth，并且前缀是parentChar
			if(tokens.get(i).length() > depth && tokens.get(i).charAt(depth-1) == parentChar) {
				childChars.add(tokens.get(i).charAt(depth));
			}
		}
		int a = base[lastTransferBaseIndex];
		//已经是叶子节点的情况,已经在insert函数种被赋值为-2
		//这里是非叶子节点的情况
		if(childChars.size() > 0) {
			insert(childChars, a + code(parentChar));
			depth++;
			for(char c : childChars) {
				walk(tokens, depth, c, a + code(parentChar));
			}
		}
	}
	
	/**
	 * 将子节点插入到树中，主要作用是确定父节点的base值是多少合适
	 * @param childs 子节点
	 * @param parentCharBaseIndex 父节点在base数组中的索引
	 */
	private void insert(Set<Character> childs, int parentCharBaseIndex) {
		//找寻一个合适的base值使得所有的子节点未被占用
		//当i=1开始寻找时会存在覆盖的问题，导致问题
		boolean failed = false;
		for(int i = -minCode+1; i <= base.length-1- maxCode; i++) {
			failed = false;
			for(char c : childs) {
				//大bug：由于这里只是检查了base[i + c]是否被占用，但是并没有设计占用标志，将会导致下一次如
				//果有一个同样的字符将会认为bae[i+c]没有被占用，因为它还是等于0
				if(base[i + code(c)] != 0) {
					failed = true;
					break;
				}
				
			}
			//成功找到
			if(!failed) {
				base[parentCharBaseIndex] = i;
				Iterator<Character> its = childs.iterator();
				while(its.hasNext()) {
					//一定要设置占用因为孩子节点的base索引已经确定，
					//如果不占用将出现被其他节点占用的情况
					char c = its.next();
					base[i + code(c)] = -2;
					check[i + code(c)] = parentCharBaseIndex;
				}
				return;
			}
		}
		System.out.println("base值确定失败！");
		//空间不够重新加大空间
		//resize(11);
		//insert(childs, parentCharBaseIndex);
	}
	
	/**
	 * 对已经构建好的trie树进行插入
	 * @param token
	 */
	public void insert(String token) {
		
	}
	/**
	 * 仅供测试使用
	 */
	/*public void print() {
		for(int i = 0;i < base.length; i++) {
			if(base[i] != 0) {
				System.out.println("base["+i+"]=:"+base[i]+",check["+i+"]="+check[i]);
			}
		}
	}
	*/
	public List<String> match(String tokenPrefix) {
		return null;
	}
	//v0:将汉字的编码缩小到1~20902
	//v1:取消这种方式，因为貌似有的字典会存在英语字母
	public int code(char c) {
		return c/* - 19967*/;
	}
	
	/**
	 * 重构数组大小
	 */
	public void resize(int size) {
		
	}
	
	
	public boolean exist(String token) {
		int last = 0;
		for(int i = 0; i < token.length(); i++) {
			int a = base[last] + code(token.charAt(i));
			if(/* base[a] == 0 || */check[a] != last) {
				return false;
			}
			//无法判断是否封闭，因为存在有的词直接是某些词的前缀
			//词的最后一个字符是否封闭
			/*if(i == token.length() -1 && base[a]!=-2) {
				return false;
			}*/
			last = a;
		}
		return true;
	}
	
	public String idOf(String token) {
		int last = 0;
		StringBuilder sb  = new StringBuilder();
		for(int i = 0; i < token.length(); i++) {
			int a = base[last] + code(token.charAt(i));
			if(check[a] != last) {
				return null;
			}
			//使用索引a来产生id
			sb.append(a+"");
			last = a;
		}
		return sb.toString();
	}
	
	public float spaceUsingRate() {
		int j = base.length;
		for(int i = base.length-1; i >= 0;i--) {
			if(base[i]!=0) {
				break;
			} else {
				j = i;
			}
		}
		System.out.println(j);
		int count = 0;
		for(int k = 0; k < j;k++) {
			if(base[k] != 0) {
				count++;
			}
		}
		return  (float) (1.0*count/(j+1));
	}
	
	static int count = 0;
	public static void main(String[] args) {
		//测试
		DATrieTree da = new DATrieTree() {
		};
		List<String> words = IOUtils.readTextAndReturnLines("D:\\nlp语料\\中文词库\\data\\四十万汉语大词库.txt", "utf-8");
		long start = System.currentTimeMillis();
		System.out.println("词条数:"+words.size());
		words = words.subList(0, 7000);
		da.build(words);
		long end = System.currentTimeMillis();
		System.out.println("构建用时:"+(end - start) / 1000.0 + "秒！");
		words.forEach((e)->{
			if(da.exist(e)) {
				count++;
			} else {
				System.out.println(e);
			}
		});
		if(count!=words.size()) {
			System.out.println("存在训练词在词典种无法找到！");
			System.out.println("能找到:"+count+";总共:"+words.size());
		}
		System.out.println("idOf:"+da.idOf("哀伤"));
		count = 0;
		//测试id是否会重复
		Set<String> set = new HashSet<>();
		words.forEach((e)->{
			set.add(da.idOf(e));
		});
		System.out.println("空间使用率:"+da.spaceUsingRate());
		System.out.println(set.size());
		System.out.println(words.size());
		//可不可以尝试开启n个线程来构建？n表示第一层的节点，因该要好些
	}
}
