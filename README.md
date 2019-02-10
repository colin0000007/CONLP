# **CONLP**
自己开发的一个自然语言处理java库，大多数算法由我自己实现。
项目地址：https://github.com/colin0000007/CONLP


### **CONLP v2.0更新**

**注意:由于GitHub单个文件限制100m，所以部分模型的文件我放到了[网盘](https://pan.baidu.com/s/1Uu5GLPoCWCXQARuctoIm6w)中,https://pan.baidu.com/s/1Uu5GLPoCWCXQARuctoIm6w**

#### 1.**前言**
很不幸的是这个项目基本算是停止了，当初我查资料时看到[HanLP](https://github.com/hankcs/HanLP)作者的博客，非常敬佩他一个人做出了这么好的开源项目，当时我正好学完机器学习的基础，实现过几个算法。我一向以为看原理和实现出算法完全是两回事，于是想自己从底层方面做点东西锻炼下自己。于是便有了CONLP这个项目，里面大多数算法由我自己实现。这个过程，我真的学到了很多，为了实现一些复杂的算法，我动手推导公式，理清公式的细节。有些算法写的异常艰难。比如：条件随机场这个算法不太好找到供学习的代码，找到一两份也不太好看懂，好在我硬着头皮最后还是基本搞定了。这个过程给我最大的启发就是实现最大熵模型时纠正了我对一些关数学式子的理解。扯多了。总得来说，写这个项目真的磨练了我，从论文到实现算法这种能力得到了提升。原本我想想好好写这个项目，向HanLP的作者学习。但无赖依靠我目前的水平这个项目还做不到商用级别，所以我把它定位为学习类别的项目。CONLP中包含了分词，命名实体识别，词性标注，依存句法分析这些nlp中比较核心的东西。对于想入门nlp的那些学习者还是足够的。这里面也包含了我收集的各种语料资源。虽然并没有完全达到我当初的想法，但能一直坚持走到这儿我已经满足了，如果继续nlp我可能会做一些更偏应用方面的nlp，毕竟毕业在即，还是得为了工作考虑啊。废话就到这里了。

#### 2.**几点重要的更新**
##### 2.1 **机器学习方面及算法方面**
+ 新增无监督学习HMM的实现
+ 新增监督学习条件随机场（CRF）的实现
+ **新增无监督学习CRF的实现**（IIS优化）
+ 新增最大熵模型的实现
+ 新增最小生成树的实现（用到依存句法分析中）
+ 引入了crf++的java移植版（感谢[zhifac提供的crf4j](https://github.com/zhifac/crf4j)）
+ 引入了openlp的Gis优化的最大熵模型的部分接口
##### 2.2 **NLP方面**
+ 新增了crf++分词，监督学习crf分词，无监督crf分词demo(不用作生成)，默认使用crf++训练出的模型，5份测试语料中平均精确度达到了0.9
+ 优化了词性标注，词性使用粗粒度词性，准确率0.89，只使用了一阶HMM作为分词器，监督crf训练出来效果差不多，crf++训练耗时爆内存无法完成
+ 新增命名实体识别，只提供三类实体（人民，地名，组织机构），默认使用一阶HMM训练出的模型，crf++爆内存,自己写的监督crf模型存储和加载有问题
+ 新增依存句法分析，使用清华大学提供的训练语料，模型使用openlp提供的最大熵模型训练，CONLP只提供了使用接口
##### 2.3 **其他**
+ 优化了框架的一些不合理设计和一些算法代码
+ 将项目更改为maven构建
+ 所有demo都在src/test/java下面
##### 2.4 **关于语料**
分词，词性标注，命名实体识别，依存分析的语料我都会放在项目中，依存分析的语料很难找，哈工大和宾州树库都需要收费授权使用

### **CONLP v0.1 更新**
1.新增Double Array TrieTree的实现，用于存储词典（由于自己实现走了弯路，用的[darts-java](https://github.com/komiya-atsushi/darts-java)的实现，在此感谢，开源地址）

2.新增一阶HMM词性标注，目前仍然存在一些小问题，会慢慢更新

3.新增词法分析器BasicLexicalAnalyzer，封装分词和词性标注

4.其他：demo演示更新

注：[词性标注中英文对照参考](https://blog.csdn.net/qq_37667364/article/details/82832925)
由于2014人民日报切分语料中存在不同类型的错误，花了大量时间在解析语料上，字典树TrieTree的实现也花费了大量时间，因为几乎没有找到说字典树实现细节问题，所以写的时候没有考虑字典序，实现出来构建树超级慢。
另外自己实在是很忙，杂七杂八的事情很多，但业余时间几乎都奉献在这个项目上了。
最近的方向：完善词性标注，考虑jieba分词的分词方案，二阶HMM平滑处理，CRF和无监督HMM。

### **项目相关**
#### **0. 项目结构**
![项目结构](https://raw.githubusercontent.com/colin0000007/CONLP/master/CONLP%20project%20structure.png)

#### **1. 介绍**

这是一个可以用来自然语言处理学习的项目，包括分词，词性标注，命名实体识别，依存句法分析等功能的实现，也包含了HMM，CRF，最大熵模型和其他算法的底层实现，源码基本都有注释，可用作学习使用。
   
#### **2. 使用**
----
这是由maven构建的java项目，所有demo都在src/test/java下面，接口和算法源码也都有注释，可参照demo来使用。
##### **2.1 分词的使用**  
提供了通用的分词调用接口：
```
public interface SegmentationPredictor {
	/**
	 * 单个句子分词
	 * @param text
	 * @return
	 */
	String[] seg(String text);
	/**
	 * 多个句子分词
	 * @param texts
	 * @return
	 */
	List<String[]> seg(String[] texts);
}
```
StaticSegmenter提供了训练好的不同模型的分词器，建议直接使用crfpp效果最好。
```
public class StaticSegmenter {
	private static FirstOrderHMMSegmenter firstOrderHMMSegmenter;
	private static SecondOrderHMMSegmenter secondOrderHMMSegmenter;
	private static SupervisedCRFSegmenter supervisedCRFSegmenter;
	private static CrfppSegmenter crfppSegmenter;
```
若需要训练分词器，参照demo中的演示。
##### **2.2 词性标注器的使用**
和分词器类似，POSTaggingPredictor定义了基本的使用接口。
```
public interface POSTaggingPredictor {
	/**
	 * 标注方法
	 * @param words 词语数组
	 * @return
	 */
	String[] tag(String[] words);
}
```
StaticPOSTagger只提供了一阶HMM模型的分词器。
其他参照演示demo

##### **2.3 词法分析器**
封装了分词器和词性标注器，提供默认使用的分词器和词性标注器
```
public class LexicalAnalyzer {
	private SegmentationPredictor segmenter; 
	private POSTaggingPredictor postagger;
	public static LexicalAnalyzer defaultLexicalAnalyzer;
	public LexicalAnalyzer(SegmentationPredictor segmenter, POSTaggingPredictor postagger) {
		this.segmenter = segmenter;
		this.postagger = postagger;
	}
	
	LexicalAnalysisResult analyze(String text) {
		String[] words = segmenter.seg(text);
		String[] tags = postagger.tag(words);
		LexicalAnalysisResult result = new LexicalAnalysisResult();
		result.setSegmentationResult(words);
		result.setPostaggingResult(tags);
		return result;
	}
	
	public static LexicalAnalyzer getDefault() {
		if(defaultLexicalAnalyzer == null) {
			defaultLexicalAnalyzer = new LexicalAnalyzer(StaticSegmenter.getCRFPPSegmenter(), StaticPOSTagger.getPOSTagger());
		}
		return defaultLexicalAnalyzer;
	}
}
```
其他参照demo
##### **2.4 依存句法分析**
只能使用，不能训练，依存句法分析依赖于分词器和词性标注器。MaxEntDependencyParser为唯一的依存句法分析。和上面类似StaticDependencyParser可获得训练好的模型，模型可输出CoNLL格式的结果，例如：
```
DependencyParser parser = StaticDependencyParser.getMaxEntDependencyParser();
		CoNLLSentence sentence1 = parser.parse("我每天都在写程序");
		System.out.println(sentence1);
		System.out.println();
```
输出:
```
1	我	我	n	n	_	5	施事	_	_
2	每天	每天	r	r	_	5	施事	_	_
3	都	都	d	d	_	5	程度	_	_
4	在	在	p	p	_	5	介词依存	_	_
5	写	写	v	v	_	0	核心成分	_	_
6	程序	程序	n	n	_	5	内容	_	_
```

##### **2.5 通用HMM** 
CONLP将HMM中的模型序列抽象为SequenceNode对象，SequenceNode描述了一个序列结点，包括隐状态和观测两个字段，如果你要使用FirstOrderGeneralHMM或者SecondOrderGeneralHMM，只需要传入一个序列的集合，就可以训练模型。或者自己继承通用模型，实现一个DataConverter接口，将原始数据转换为SequenceNode，就可以扩展适合你使用的通用HMM模型。比如一阶分词器就是一个扩展:

    public class FirstOrderHMMSegmenter extends FirstOrderGeneralHMM implements Segmenter{
    
    	public FirstOrderHMMSegmenter(int stateNum, int observationNum) {
    		super(stateNum, observationNum);
    	}
    	
    	public FirstOrderHMMSegmenter() {
    		super();
    	}
    
    	public FirstOrderHMMSegmenter(int stateNum, int observationNum, double[] pi, double[][] transfer_probability1,
    			double[][] emission_probability) {
    		super(stateNum, observationNum, pi, transfer_probability1, emission_probability);
    	}
    }
##### **2.6 其他机器学习算法**
无监督HMM，crf相关的例子请参照demo
  
## **感谢**
+ 感谢HanLP作者hancks的开源项目和博客提供的极大帮助
+ 感谢参考的科研者的论文
+ 感谢各类博客文章的指点
+ 感谢crf4j，openlp等等优秀的开源项目


