package com.outsider.nlp.segmenter;

import java.util.Arrays;
import java.util.List;

import com.outsider.common.util.IOUtils;

public class TestSeg {
	public static void testSeg(SegmentationPredictor segmenter) {
		List<String> sentences = IOUtils.readTextAndReturnLines("./src/test/resources/sentences4segmentation.txt", "gbk");
		long time = 0;//4.3953996
		long len = 0;
		for(String sentence : sentences) {
			long start = System.currentTimeMillis();
			String[] res = segmenter.seg(sentence);
			long end = System.currentTimeMillis();
			time += (end - start);
			len += sentence.length();
			System.out.println(Arrays.toString(res));
		}
		System.out.println("耗时:"+time+"毫秒");
	}
	
	public static void testSeg2(Segmenter segmenter) {
		String s1 = "高锰酸钾，强氧化剂，紫红色晶体，可溶于水，遇乙醇即被还原。常用作消毒剂、水净化剂、氧化剂、漂白剂、毒气吸收剂、二氧化碳精制剂等。";
		String s2 = "《夜晚的骰子》通过描述浅草的舞女在暗夜中扔骰子的情景,寄托了作者对庶民生活区的情感。";
		String s3 = "财政部副部长王保安调任国家统计局党组书记。";
		String[] ss = new String[] {s1,s2,s3};
		List<String[]> result = segmenter.seg(ss);
		for(String[] res : result) {
			for(String word : res) {
				System.out.print(word+"|");
			}
			System.out.println();
		}
	}
}
