package com.outsider.common.algorithm.dat.darts;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DoubleArrayTrie<T> implements Serializable{
	private final static int BUF_SIZE = 16384;
	private final static int UNIT_SIZE = 8; // size of int + int

	private static class Node {
		int code;
		int depth;
		int left;
		int right;
	};

	private int check[];
	private int base[];

	private boolean used[];
	private int size;
	private int allocSize;
	private List<String> key;
	private int keySize;
	private int length[];
	private int value[];
	private int progress;
	private int nextCheckPos;
	private List<T> values;
	// boolean no_delete_;
	int error_;

	// int (*progressfunc_) (size_t, size_t);

	// inline _resize expanded
	private int resize(int newSize) {
		int[] base2 = new int[newSize];
		int[] check2 = new int[newSize];
		boolean used2[] = new boolean[newSize];
		if (allocSize > 0) {
			System.arraycopy(base, 0, base2, 0, allocSize);
			System.arraycopy(check, 0, check2, 0, allocSize);
			System.arraycopy(used2, 0, used2, 0, allocSize);
		}

		base = base2;
		check = check2;
		used = used2;

		return allocSize = newSize;
	}
	public int getKeySize() {
		return keySize;
	}
	/**
	 * 主要的构建方法
	 * @param parent
	 * @param siblings
	 * @return
	 */
	private int fetch(Node parent, List<Node> siblings) {
		if (error_ < 0)
			return 0;

		int prev = 0;

		for (int i = parent.left; i < parent.right; i++) {
			if ((length != null ? length[i] : key.get(i).length()) < parent.depth)
				continue;

			String tmp = key.get(i);

			int cur = 0;
			if ((length != null ? length[i] : tmp.length()) != parent.depth)
				cur = (int) tmp.charAt(parent.depth) + 1;

			if (prev > cur) {
				error_ = -3;
				return 0;
			}

			if (cur != prev || siblings.size() == 0) {
				Node tmp_node = new Node();
				tmp_node.depth = parent.depth + 1;
				tmp_node.code = cur;
				tmp_node.left = i;
				if (siblings.size() != 0)
					siblings.get(siblings.size() - 1).right = i;

				siblings.add(tmp_node);
			}

			prev = cur;
		}

		if (siblings.size() != 0)
			siblings.get(siblings.size() - 1).right = parent.right;
		return siblings.size();
	}

	private int insert(List<Node> siblings) {
		if (error_ < 0)
			return 0;

		int begin = 0;
		int pos = ((siblings.get(0).code + 1 > nextCheckPos) ? siblings.get(0).code + 1
				: nextCheckPos) - 1;
		int nonzero_num = 0;
		int first = 0;

		if (allocSize <= pos)
			resize(pos + 1);

		outer: while (true) {
			pos++;

			if (allocSize <= pos)
				resize(pos + 1);

			if (check[pos] != 0) {
				nonzero_num++;
				continue;
			} else if (first == 0) {
				nextCheckPos = pos;
				first = 1;
			}

			begin = pos - siblings.get(0).code;
			if (allocSize <= (begin + siblings.get(siblings.size() - 1).code)) {
				// progress can be zero
				double l = (1.05 > 1.0 * keySize / (progress + 1)) ? 1.05 : 1.0
						* keySize / (progress + 1);
				resize((int) (allocSize * l));
			}

			if (used[begin])
				continue;

			for (int i = 1; i < siblings.size(); i++)
				if (check[begin + siblings.get(i).code] != 0)
					continue outer;

			break;
		}

		// -- Simple heuristics --
		// if the percentage of non-empty contents in check between the
		// index
		// 'next_check_pos' and 'check' is greater than some constant value
		// (e.g. 0.9),
		// new 'next_check_pos' index is written by 'check'.
		if (1.0 * nonzero_num / (pos - nextCheckPos + 1) >= 0.95)
			nextCheckPos = pos;

		used[begin] = true;
		size = (size > begin + siblings.get(siblings.size() - 1).code + 1) ? size
				: begin + siblings.get(siblings.size() - 1).code + 1;

		for (int i = 0; i < siblings.size(); i++)
			check[begin + siblings.get(i).code] = begin;

		for (int i = 0; i < siblings.size(); i++) {
			List<Node> new_siblings = new ArrayList<Node>();

			if (fetch(siblings.get(i), new_siblings) == 0) {
				base[begin + siblings.get(i).code] = (value != null) ? (-value[siblings
						.get(i).left] - 1) : (-siblings.get(i).left - 1);

				if (value != null && (-value[siblings.get(i).left] - 1) >= 0) {
					error_ = -2;
					return 0;
				}

				progress++;
				// if (progress_func_) (*progress_func_) (progress,
				// keySize);
			} else {
				int h = insert(new_siblings);
				base[begin + siblings.get(i).code] = h;
			}
		}
		return begin;
	}

	public DoubleArrayTrie() {
		check = null;
		base = null;
		used = null;
		size = 0;
		allocSize = 0;
		// no_delete_ = false;
		error_ = 0;
	}

	// no deconstructor

	// set_result omitted
	// the search methods returns (the list of) the value(s) instead
	// of (the list of) the pair(s) of value(s) and length(s)

	// set_array omitted
	// array omitted

	void clear() {
		// if (! no_delete_)
		check = null;
		base = null;
		used = null;
		allocSize = 0;
		size = 0;
		// no_delete_ = false;
	}

	public int getUnitSize() {
		return UNIT_SIZE;
	}

	public int getSize() {
		return size;
	}

	public int getTotalSize() {
		return size * UNIT_SIZE;
	}

	public int getNonzeroSize() {
		int result = 0;
		for (int i = 0; i < size; i++)
			if (check[i] != 0)
				result++;
		return result;
	}
	
/*	public int buildWithoutDictionarySorting(List<T> values) {
		//产生keys，默认调用toString
		List<String> keys = new ArrayList<>();
		Map<String, T> map = new HashMap<>();
		for(T t : values) {
			map.put(t.toString(), t);
			keys.add(t.toString());
		}
		//必须字典序排序
		Collections.sort(keys);
		//构建value数组
		this.values = new ArrayList<>();
		
		for(int i = 0; i < keys.size();i++) {
			this.values.add(map.get(keys.get(i)));
		}
		return build(keys, null, null, keys.size());
	}*/
	
	/**
	 * 用按照字典序排好的keys和对应的values来构建字典树
	 * @param keys 排序号的keys
	 * @param values 对应的values
	 * @return 是否出错
	 */
	public int build(List<String> keys, List<T> values) {
		this.values = values;
		return build(keys, null, null, keys.size());
	}
	/**
	 * 只用keys来构建字典树
	 * List必须是已经按照字典序排好序
	 * @param keys
	 * @return
	 */
	public int build(List<String> keys) {
		
		return build(keys, null, null, keys.size());
	}
	/**
	 * 传入TreeSet，String作为泛型默认就按照字典序排列
	 * @param keys
	 * @return
	 */
	public int build(TreeSet<String> keys) {
		List<String> keys2 = new ArrayList<>(keys);
		return build(keys2, null, null, keys2.size());
	}
	
	/**
	 * 传入TreeMap，key是String，默认就是字典序
	 * @param map
	 * @return
	 */
	public int build(TreeMap<String, T> map) {
		List<String> keys = new ArrayList<>();
		Set<Entry<String, T>> entrys  = map.entrySet();
		this.values = new ArrayList<>(map.size());
		for(Entry<String, T> entry : entrys) {
			keys.add(entry.getKey());
			this.values.add(entry.getValue());
		}
		return build(keys, null, null, keys.size());
	}
	
	/**
	 * 底层的构建方法
	 * @param _key
	 * @param _length
	 * @param _value
	 * @param _keySize
	 * @return
	 */
	public int build(List<String> _key, int _length[], int _value[],
			int _keySize) {
		if (_keySize > _key.size() || _key == null)
			return 0;

		// progress_func_ = progress_func;
		key = _key;
		length = _length;
		keySize = _keySize;
		value = _value;
		progress = 0;

		resize(65536 * 32);

		base[0] = 1;
		nextCheckPos = 0;

		Node root_node = new Node();
		root_node.left = 0;
		root_node.right = keySize;
		root_node.depth = 0;
		List<Node> siblings = new ArrayList<Node>();
		//System.out.println("siblings:"+siblings.size());
		fetch(root_node, siblings);
		insert(siblings);

		// size += (1 << 8 * 2) + 1; // ???
		// if (size >= allocSize) resize (size);

		used = null;
		key = null;

		return error_;
	}

	public void open(String fileName) throws IOException {
		File file = new File(fileName);
		size = ((int) file.length() -4) / UNIT_SIZE;
		check = new int[size];
		base = new int[size];
		//System.out.println("loaded check array size:"+size);
		DataInputStream is = null;
		try {
			is = new DataInputStream(new BufferedInputStream(
					new FileInputStream(file), BUF_SIZE));
			
			//size = is.readInt();
			for (int i = 0; i < size; i++) {
				base[i] = is.readInt();
				check[i] = is.readInt();
				
			}
			keySize = is.readInt();
			File valuesFile = new File(fileName+"_values");
			if(valuesFile.exists()) {
				ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(fileName+"_values"));
				try {
					this.values = (List<T>) objIn.readObject();
					System.out.println("values.size():"+values.size());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} finally {
			if (is != null)
				is.close();
		}
	}

	public void save(String fileName) throws IOException {
		DataOutputStream out = null;
		
		//对base和check数组保存保存size+1的大小（这里的size并不是check数组的大小），不然可能出现Out Of Index
		size = size + 1;
		try {
			out = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(fileName)));
			//写入数组长度，方便恢复
			//out.writeInt(check.length);
			//System.out.println("writed check array size:"+size);
			for (int i = 0; i < size; i++) {
				out.writeInt(base[i]);
				out.writeInt(check[i]);
			}
			out.writeInt(keySize);
			//保存key对应的value
			if(values != null) {
				ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream(fileName+"_values"));
				obj.writeObject(values);
			}
			out.close();
		} finally {
			if (out != null)
				out.close();
		}
	}
	/**
	 * 精确匹配
	 * 返回该词在字典序中的int位置，注意
	 * 这一点很重要
	 * @param key
	 * @return
	 */
	public int exactMatchSearch(String key) {
		return exactMatchSearch(key, 0, 0, 0);
	}
	
	public int exactMatchSearch(String key, int pos, int len, int nodePos) {
		if (len <= 0)
			len = key.length();
		if (nodePos <= 0)
			nodePos = 0;

		int result = -1;

		char[] keyChars = key.toCharArray();

		int b = base[nodePos];
		int p;

		for (int i = pos; i < len; i++) {
			p = b + (int) (keyChars[i]) + 1;
			if (b == check[p])
				b = base[p];
			else
				return result;
		}

		p = b;
		int n = base[p];
		if (b == check[p] && n < 0) {
			result = -n - 1;
		}
		return result;
	}
	/**
	 * 自己写的，非darts，获取词的id
	 * 不可行，必须使用int类型的id
	 * @param key
	 * @return
	 */
	@Deprecated
	public String idOf(String key) {
		if(key.length() == 0) {
			return null;
		}
		char[] keyChars = key.toCharArray();
		int b = base[0];
		int p;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keyChars.length; i++) {
			p = b + (int) (keyChars[i]) + 1;
			if (b == check[p]) {
				b = base[p];
				sb.append(p+"");
			}
			else
				return null;
		}
		return sb.toString();
	}
	
	public int intIdOf(T t) {
		return exactMatchSearch(t.toString());
	}
	public T getValue(String key) {
		int id = intIdOf(key);
		if(id < 0 || id >= values.size()) {
			return null;
		}
		return  values.get(id);
	}
	public List<T> getValues() {
		
		return  values;
	}
	public T getValue(int id) {
		if(id <0 || id > values.size() - 1) {
			return null;
		}
		return values.get(id);
	}
	public int intIdOf(String key) {
		return exactMatchSearch(key);
	}
	public List<Integer> commonPrefixSearch(String key) {
		return commonPrefixSearch(key, 0, 0, 0);
	}
	/**
	 * 前缀匹配
	 * @param key
	 * @param pos
	 * @param len
	 * @param nodePos
	 * @return
	 */
	public List<Integer> commonPrefixSearch(String key, int pos, int len,
			int nodePos) {
		if (len <= 0)
			len = key.length();
		if (nodePos <= 0)
			nodePos = 0;

		List<Integer> result = new ArrayList<Integer>();

		char[] keyChars = key.toCharArray();

		int b = base[nodePos];
		int n;
		int p;

		for (int i = pos; i < len; i++) {
			p = b;
			n = base[p];

			if (b == check[p] && n < 0) {
				result.add(-n - 1);
			}

			p = b + (int) (keyChars[i]) + 1;
			if (b == check[p])
				b = base[p];
			else
				return result;
		}

		p = b;
		n = base[p];

		if (b == check[p] && n < 0) {
			result.add(-n - 1);
		}

		return result;
	}

	// debug
	public void dump() {
		for (int i = 0; i < size; i++) {
			System.err.println("i: " + i + " [" + base[i] + ", " + check[i]
					+ "]");
		}
	}
}