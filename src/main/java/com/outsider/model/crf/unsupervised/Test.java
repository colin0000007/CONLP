package com.outsider.model.crf.unsupervised;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import com.outsider.common.util.IOUtils;
import com.outsider.model.data.SegmentationDataConverter;
import com.outsider.model.hmm.SequenceNode;

public class Test {
	public static void main(String[] args) {
		
		try {
			System.setErr(new PrintStream(new FileOutputStream("C:\\Users\\outsider\\Desktop\\iters.txt")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		UnsupervisedCRF crf  = new UnsupervisedCRF(65536, 4);
		String[] words = IOUtils.loadSegmentionCorpus("./data/segmentation/ctb6.train.seg.utf8.splitBy1space.txt", "utf-8", " ");
		SegmentationDataConverter dataConverter = new SegmentationDataConverter();
		List<SequenceNode> nodes = dataConverter.convert(words);
		crf.train(nodes);
		String test = "自然语言处理是人工智能研究中非常重要的一个领域。";
		String test2 =  "你看过穆赫兰道这部电影吗？";
		String test3 = "原标题：日媒拍到了现场罕见一幕，据日本新闻网（NNN）9月8日报道，日前，日本海上自卫队现役最大战舰之一的直升机航母“加贺”号在南海航行时，遭多艘中国海军战舰抵近跟踪监视。 ";
		String[] res = crf.seg(test);
		String[] res2 = crf.seg(test2);
		String[] res3 = crf.seg(test3);
		System.out.println(Arrays.toString(res));
		System.out.println(Arrays.toString(res2));
		System.out.println(Arrays.toString(res3));
		/**
			召回率:0.8543504907694179,精准率:0.8167286843894236,F值:0.835116088700273,错误率:0.19171352914206582
			召回率:0.8774084755343917,精准率:0.9000009827913239,F值:0.8885611434005103,错误率:0.09748876603654272
			总精确率:1.7167296671807475,总召回率:1.7317589663038095,总f得分:1.7236772321007834
			
			iter 1:
			召回率:0.8258025881186081,精准率:0.7855172045499048,F值:0.805156299178477,错误率:0.2254825821301919
			召回率:0.8429352981192093,精准率:0.8535255539601847,F值:0.8481973708947346,错误率:0.144657040748867
			总精确率:1.6390427585100895,总召回率:1.6687378862378175,总f得分:1.6533536700732117
			
			iter11:
			f-score:1.6748159294726115
			
			iter 20:
			召回率:0.8258400157195924,精准率:0.8099551248520221,F值:0.8178204427312571,错误率:0.19377204719620483
			召回率:0.8400226116449971,精准率:0.8826093521920774,F值:0.8607895692812191,错误率:0.11172643742035623
			总精确率:1.6925644770440995,总召回率:1.6658626273645896,总f得分:1.6786100120124763
			
			iter22:
			召回率:0.8260365106247602,精准率:0.8102073218858123,F值:0.8180453496668737,错误率:0.19350069708906834
			召回率:0.8407124584415211,精准率:0.8831119162640901,F值:0.8613907554569505,错误率:0.11127612076151422
			总精确率:1.6933192381499025,总召回率:1.6667489690662813,总f得分:1.6794361051238242
		 *
		 */
	}
}


/**
 * 迭代一次结果:
 *[原, 标题, ：, 日媒, 拍, 到, 了, 现场, 罕见, 一, 幕, ，, 据, 日本, 新闻网, （, NNN）9月8日, 报道, ，, 日前, ，, 日本, 海上, 自卫队, 现役, 最大战, 舰之, 一, 的, 直升机, 航母, “, 加贺, ”, 号, 在, 南海, 航行, 时, ，, 遭多, 艘, 中国, 海军, 战舰, 抵近, 跟踪, 监视, 。,  ]
 * 
**/