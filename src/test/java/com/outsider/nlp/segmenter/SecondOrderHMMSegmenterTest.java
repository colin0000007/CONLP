package com.outsider.nlp.segmenter;

import java.util.Arrays;

import com.outsider.common.util.IOUtils;
import com.outsider.constants.nlp.PathConstans;

public class SecondOrderHMMSegmenterTest {
	public static void main(String[] args) {
		//使用示例
		use();
		//训练示例
		//train();
		//[原标题, ：, 日媒, 拍到, 了, 现场, 罕见, 一幕, 据, 日本, 新闻网, （, NNN, ）, 9月, 8日, 报道, ，, 日前, ，, 日本, 海上, 自卫队, 现役, 最大, 战舰, 之, 一, 的, 直升, 机航母, “, 加贺, ”, 号, 在, 南海, 航行, 时, ，, 遭, 多, 艘, 中国, 海军, 战舰, 抵近, 跟踪, 监视, 。]
	}
	public static void use() {
		Segmenter segmenter = new SecondOrderHMMSegmenter();
		segmenter.open(PathConstans.SEOND_ORDER_HMM_SEGMENTER, null);
		TestSeg.testSeg(segmenter);
	}
	/**
	 * 训练示例
	 */
	public static void train() {
		Segmenter segmenter = new SecondOrderHMMSegmenter();
		String basePath = "./data/segmentation";
		String pku = basePath + "pku_training.splitBy2space.utf8";
		String sku = basePath+"sku_train.utf8.splitBy2space.txt";
		String ctb6 = basePath+"ctb6.train.seg.utf8.splitBy1space.txt";
		String cityu = basePath+"cityu_training.utf8.splitBy1space.txt";
		String[] words = IOUtils.loadMultiSegmentionCorpus(new String[] {pku,sku,ctb6,cityu}, new String[] {"utf-8","utf-8","utf-8","utf-8"}, new String[] {"  ","  "," "," "});
		segmenter.train(words);
		segmenter.save("./model/SecondOrderHMMSegmenter", null);
		String s = "HanLP是由一系列模型与算法组成的Java工具包，目标是普及自然语言处理在生产环境中的应用。";
		String s2 = "高锰酸钾，强氧化剂，紫红色晶体，可溶于水，遇乙醇即被还原。常用作消毒剂、水净化剂、氧化剂、漂白剂、毒气吸收剂、二氧化碳精制剂等。";
		String str = "原标题：日媒拍到了现场罕见一幕" + 
				"据日本新闻网（NNN）9月8日报道，日前，日本海上自卫队现役最大战舰之一的直升机航母“加贺”号在南海航行时，遭多艘中国海军战舰抵近跟踪监视。" ; 
		
		SegmenterTest.score(segmenter, "SOHMMSegmenter");
		String[] res = segmenter.seg(s);
		System.out.println(Arrays.toString(res));
		String[] res2 = segmenter.seg(s2);
		System.out.println(Arrays.toString(res2));
		String[] res3 = segmenter.seg(str);
		System.out.println(Arrays.toString(res3));
	}
}
