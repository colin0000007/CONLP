package com.outsider.nlp.myPersonalTest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.outsider.common.util.IOUtils;

public class SomeTest {
	
	//处理有些语料换行没有分隔符的错误
	@Test
	public void t2() {
		String basePath = "D:\\nlp语料\\中文简体和繁体分词语料\\简体\\";
		String sku = basePath+"sku_train.utf8.splitBy2space.txt";//已处理好
		String msr  = basePath+"msr_training.utf8.splitBy2space.txt";//还是没用
		String ctb6 = basePath+"ctb6.train.seg.utf8.splitBy1space.txt";
		String cityu = basePath+"cityu_training.utf8.splitBy1space.txt";
		String as = basePath + "as_training.utf8.splitBy1spce.txt";
		String tagCorpus ="D:\\nlp语料\\词性标注\\dev.txt";
		String msrTest = basePath+"msr_test_gold.utf8.txt";
		String skuTest = basePath+"sku_test.txt";
		String ctb6Test = basePath+"ctb6.test.seg.txt";
		String asTest = basePath+"as_test_gold.utf8.txt";
		String encoding = "utf-8";
		String path = asTest;
		try {
			BufferedReader reader = null;
			if((!encoding.trim().equals(""))&&encoding!=null) {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),encoding));
			} else {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			}
			String s="";
			StringBuilder sb  = new StringBuilder();
			while((s=reader.readLine())!=null) {
				sb.append(s.trim()+" "+"\n");
			}
			reader.close();
			IOUtils.writeTextData2File(sb.toString(), path, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testOuter() {
		outer:for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10;j++) {
					if(i==2 && j== 9) {
						System.out.println("直接从内层循环跳出最外层！");
						break outer;
					}
				}
		}
	System.out.println("已跳出外层循环！");
	}

	@Test
	public void t4() {
		String regx = "\\[.+\\]";
		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher("[你/x好/s]/a");
		while(matcher.find()) {
			System.out.println(matcher.start()+":"+matcher.group()+":"+matcher.end());
		}
	}
}
