package com.outsider.model.hmm;

import java.util.Arrays;
import java.util.List;

import com.outsider.common.util.IOUtils;
import com.outsider.model.data.DataConverter;
import com.outsider.model.data.SegmentationDataConverter;
import com.outsider.nlp.segmenter.SegmentationUtils;

public class UnsupervisedFirstOrderGeneralHMMTest {

	public static void main(String[] args) {
		/**
		 * 少量数据建议串行
  		大量数据，几十万，百万甚至更高的数据强烈建议并行训练,性能是串行的好4倍以上
		 */
		//testHMM();
		testParallelHMM();
	}
	
	/**
	 * 串行
	 */
	public static void testHMM() {
		UnsupervisedFirstOrderGeneralHMM hmm = new UnsupervisedFirstOrderGeneralHMM(4, 65536);
		//关闭日志打印
		//CONLPLogger.closeLogger(hmm.logger);
		String path = "./data/pku_training.splitBy2space.utf8";
		String data = IOUtils.readText(path, "utf-8");
		String[] words = data.split("  ");
		DataConverter<String[], List<SequenceNode>> converter = new SegmentationDataConverter();
		List<SequenceNode> nodes = converter.convert(words);
		nodes = nodes.subList(0, 10000);//只使用10000个节点
		//训练之前设置先验概率，必须设置，EM对初始值敏感，如果不设置默认为都为0，所有参数都将一样，没有意义
		//如果只给了其中一些参数的先验值，可以随机初始化其他参数，例如
		//hmm.randomInitA();
		//hmm.randomInitB();
		//hmm.randomInitPi();
		//hmm.randomInitAllParameters();
		//设置先验信息至少设置参数pi，A，B中的一个
		hmm.setPriorPi(new double[] {-1.138130826175848, -2.632826946498266, -1.138130826175848, -1.2472622308278396});
		hmm.setPriorTransferProbability1((double[][]) IOUtils.readObject("src/main/resources/A"));
		hmm.setPriorEmissionProbability((double[][]) IOUtils.readObject("src/main/resources/B"));
		hmm.train(nodes, -1, 0.5);
		String str = "原标题：日媒拍到了现场罕见一幕" + 
				"据日本新闻网（NNN）9月8日报道，日前，日本海上自卫队现役最大战舰之一的直升机航母“加贺”号在南海航行时，遭多艘中国海军战舰抵近跟踪监视。" ; 
		//将词转换为对应的Unicode码
		int[] x = SegmentationUtils.str2int(str);
		int[] predict = hmm.predict(x);
		System.out.println(Arrays.toString(predict));
		String[] res = SegmentationUtils.decode(predict, str);
		System.out.println(Arrays.toString(res));
	}
	/**
	 * 并行
	 */
	public static void testParallelHMM() {
		UnsupervisedFirstOrderGeneralHMM hmm = new UnsupervisedFirstOrderGeneralHMM(4, 65536);
		//CONLPLogger.closeLogger(hmm.logger);
		String basePath = "./data/";
		String path = "./data/pku_training.splitBy2space.utf8";
		String data = IOUtils.readText(path, "utf-8");
		String[] words = data.split("  ");
		DataConverter<String[], List<SequenceNode>> converter = new SegmentationDataConverter();
		List<SequenceNode> nodes = converter.convert(words);
		System.out.println("nodes.size:"+nodes.size());
		nodes = nodes.subList(0, 100000);//使用500000个节点
		hmm.setPriorPi(new double[] {-1.138130826175848, -2.632826946498266, -1.138130826175848, -1.2472622308278396});
		//hmm.setPriorTransferProbability1((double[][]) IOUtils.readObject("src/main/resources/A"));
		hmm.setPriorEmissionProbability((double[][]) IOUtils.readObject("src/main/resources/B"));
		hmm.parallelTrain(nodes, -1, 0.5);
		String str = "原标题：日媒拍到了现场罕见一幕" + 
				"据日本新闻网（NNN）9月8日报道，日前，日本海上自卫队现役最大战舰之一的直升机航母“加贺”号在南海航行时，遭多艘中国海军战舰抵近跟踪监视。" ; 
		//将词转换为对应的Unicode码
		int[] x = SegmentationUtils.str2int(str);
		int[] predict = hmm.predict(x);
		System.out.println(Arrays.toString(predict));
		String[] res = SegmentationUtils.decode(predict, str);
		System.out.println(Arrays.toString(res));
	}
	
}
