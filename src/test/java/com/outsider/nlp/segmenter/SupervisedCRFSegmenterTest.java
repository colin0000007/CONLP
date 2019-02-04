package com.outsider.nlp.segmenter;

import java.util.Arrays;
import java.util.List;

import com.outsider.common.dataStructure.Table;
import com.outsider.common.util.IOUtils;
import com.outsider.common.util.nlp.CorpusUtils;
import com.outsider.common.util.nlp.NLPUtils;
import com.outsider.constants.nlp.PathConstans;
import com.outsider.model.data.CRFSegmentationTableDataConverter;
import com.outsider.model.data.SegmentationDataConverter;
import com.outsider.model.hmm.SequenceNode;
/**
 * 使用默认模板
 * 只是用一份语料最好：4.2907094316046495 索引 2 特征函数875035
 * 总精确率:4.286389518392307,总召回率:4.296123341643277,总f得分:4.2907094316046495
 * 全模板：特征函数 2323339
 * 总精确率:4.31152655184218,总召回率:4.30433444739526,总f得分:4.307312639515478 分词直观感受还可以
 * 
 * 使用2份语料最好：4.337030776105888  索引:[2, 3] 特征函数 1869221
 * 总精确率:4.339109999777037,总召回率:4.336089665088207,总f得分:4.337030776105888
 * 
 * 使用3份语料：4.347382274041837 索引组合:[1, 2, 3] 特征函数 2107239
 * 总精确率:4.347279501022128,总召回率:4.348538072250367,总f得分:4.347382274041837
	后面的相对没有太多提升又太耗内存了
	
ctb8:
总精确率:4.371331921870376,总召回率:4.360915860640292,总f得分:4.365571586390583

ctb8+sku:
总精确率:4.382265856799716,总召回率:4.359842697453232,总f得分:4.3704761002059325

	往ctb6语料中加入了测试语料中的第一句，可以让自然语言处理分在一起了
 * @author outsider
 *
 */
public class SupervisedCRFSegmenterTest {

	public static void main(String[] args) {
		//train();
		train2();
		use();
		//7587206
	}
	
	/**
	 * 使用示例
	 */
	public static void use() {
		Segmenter segmenter = new SupervisedCRFSegmenter();
		long start = System.currentTimeMillis();
		segmenter.open(PathConstans.SUPERVISED_CRF_SEGMENTER, null);
		long end = System.currentTimeMillis();
		System.out.println("耗时:"+(end - start)+"毫秒!");
		TestSeg.testSeg(segmenter);
		SegmenterTest.score(segmenter, "SCRF");
	}
	/**
	 * 训练示例
	 */
	public static void train() {
		//测试
		SupervisedCRFSegmenter segmenter = new SupervisedCRFSegmenter();
		String basePath = "./data/segmentation";
		String pku = basePath + "pku_training.splitBy2space.utf8";
		String sku = basePath+"sku_train.utf8.splitBy2space.txt";
		String ctb6 = basePath+"ctb6.train.seg.utf8.splitBy1space.txt";
		String cityu = basePath+"cityu_training.utf8.splitBy1space.txt";
		// 1 2 3
		//String[] words = IOUtils.loadMultiSegmentionCorpus(new String[] {sku,ctb6,cityu}, new String[] {"utf-8","utf-8","utf-8"}, new String[] {"  "," "," "});
		// 2
		//String[] words = IOUtils.loadMultiSegmentionCorpus(new String[] {ctb6}, new String[] {"utf-8"}, new String[] {" "});
		String[] words = IOUtils.loadMultiSegmentionCorpus(new String[] {/*pku,*/cityu}, new String[] {/*"utf-8",*/"utf-8"}, new String[] {/*"  ",*/" "});
		// 2 3
		//String[] words = IOUtils.loadMultiSegmentionCorpus(new String[] {ctb6/*, cityu*/}, new String[] {"utf-8"/*, "utf-8"*/}, new String[] {" "/*, " "*/});
		SegmentationDataConverter converter = new SegmentationDataConverter();
		
		String ctb8 = "D:\\nlp语料\\ctb8.0\\parse_result\\seg\\train.tsv";
		String srcData = IOUtils.readTextWithLineCheckBreak(ctb8, "utf-8");
		CRFSegmentationTableDataConverter converter2 = new CRFSegmentationTableDataConverter(0, 1);
		Table table = Table.generateTable(srcData, "\t");
		List<SequenceNode> nodes = converter2.convert(table);
		
		List<SequenceNode> nodes2 = converter.convert(words);
		nodes.addAll(nodes2);
		segmenter.train(nodes);
		String test3 = "原标题：日媒拍到了现场罕见一幕，据日本新闻网（NNN）9月8日报道，日前，日本海上自卫队现役最大战舰之一的直升机航母“加贺”号在南海航行时，遭多艘中国海军战舰抵近跟踪监视。 ";
		String[] res = segmenter.seg(test3);
		System.out.println(Arrays.toString(res));
		//String s1 = segmenter.generateModelTemplate();
		for(int i = 0; i < 4; i++) {
			System.out.println(Arrays.toString(segmenter.getTransferProbabilityWeights()[i]));
		}
		//IOUtils.writeTextData2File(s1, "C:\\Users\\outsider\\Desktop\\crf.model", "utf-8");
		//segmenter.save(PathConstans.SUPERVISED_CRF_SEGMENTER, null);
		TestSeg.testSeg(segmenter);
		SegmenterTest.score(segmenter, "SCRF");
	}
	
	public static void train2() {
		//测试
		SupervisedCRFSegmenter segmenter = new SupervisedCRFSegmenter();
		String basePath = "./data/segmentation/";
		String pku = basePath + "pku_training.splitBy2space.utf8";
		String[] words = IOUtils.loadMultiSegmentionCorpus(new String[] {pku}, new String[] {"utf-8"}, new String[] {"  "});
		SegmentationDataConverter converter = new SegmentationDataConverter();
		List<SequenceNode> nodes = converter.convert(words);
		segmenter.train(nodes);
		String test3 = "原标题：日媒拍到了现场罕见一幕，据日本新闻网（NNN）9月8日报道，日前，日本海上自卫队现役最大战舰之一的直升机航母“加贺”号在南海航行时，遭多艘中国海军战舰抵近跟踪监视。 ";
		String[] res = segmenter.seg(test3);
		System.out.println(Arrays.toString(res));
		//String s1 = segmenter.generateModelTemplate();
		for(int i = 0; i < 4; i++) {
			System.out.println(Arrays.toString(segmenter.getTransferProbabilityWeights()[i]));
		}
		//IOUtils.writeTextData2File(s1, "C:\\Users\\outsider\\Desktop\\crf.model", "utf-8");
		//segmenter.save(PathConstans.SUPERVISED_CRF_SEGMENTER, null);
		TestSeg.testSeg(segmenter);
		SegmenterTest.score(segmenter, "SCRF");
	}
	
}
