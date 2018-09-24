# CONLP
自己开发的一个自然语言处理java库，后期可能会加入机器学习的一些模型，不限于nlp。
项目地址：https://github.com/colin0000007/CONLP
欢迎关注
CONLP是瞎取的名字，暂定。  
## CONLP v0.1 更新

1.新增Double Array TrieTree的实现，用于存储词典（由于自己实现走了弯路，用的darts-java的实现，在此感谢，开源地址https://github.com/komiya-atsushi/darts-java）

2.新增一阶HMM词性标注，目前仍然存在一些小问题，会慢慢更新

3.新增词法分析器BasicLexicalAnalyzer，封装分词和词性标注

4.其他：demo演示更新

由于2014人民日报切分语料中存在不同类型的错误，花了大量时间在解析语料上，零国外字典树TrieTree的实现也花费了大量时间，因为几乎没有找到说字典树实现细节问题，所以写的时候没有考虑字典序，实现出来构建树超级慢。
另外自己实在是很忙，杂七杂八的事情很多，但业余时间几乎都奉献在这个项目上了。
最近的方向：完善词性标注，考虑jieba分词的分词方案，二阶HMM平滑处理，CRF和无监督HMM。

1.介绍
----
一个通用的隐马模型的java实现，包括一阶（bigram），和二阶模型（trigram）的，二阶模型目前没有使用平滑处理，并在此基础上实现了一阶和二阶HMM的纯序列标注的中文分词和词性标注（词性标注目前还没完善），一阶HMM分词的速度可以达到上百万字每秒，二阶模型有几十万字每秒。
   
2.使用
----
这是一个由eclipse构建的项目，你可以直接下载源码，里面的demo包有示例，在eclipse中打开，目前用jar文件还存在一些问题，也没弄到maven的中央仓库，因为目前还太不完善。  
**2.1分词的使用**  
目前分词的效果只能达到82%左右的正确率，CRF模型用于分词精度很高，后期会实现该模型，训练数据和测试数据来自国际计算语言学会（ACL）中文语言处理小组。
提供了通用的分词接口Segmenter，你可以使用训练好的模型直接使用分词器，如下：

    public static void main(String[] args) {
    		Segmenter segmenter = StaticSegmenter.getSegmenter();
    		String test = "原标题：日媒拍到了现场罕见一幕" + 
    				"据日本新闻网（NNN）9月8日报道，日前，日本海上自卫队现役最大战舰之一的直升机航母“加贺”号在南海航行时，遭多艘中国海军战舰抵近跟踪监视。" ; 
    		String[] terms = segmenter.predictAndReturnTerms(test);
    		System.out.println(Arrays.toString(terms));
    	}
效果如下：

    [原标题, ：, 日媒, 拍到, 了, 现场, 罕见, 一幕, 据, 日本, 新闻网, （, NNN, ）, 9月, 8日, 报道, ，, 日前, ，, 日本, 海上, 自卫队, 现役, 最, 大, 战舰, 之, 一, 的, 直升, 机航母, “, 加贺, ”, 号, 在, 南海, 航行, 时, ，, 遭多, 艘, 中国, 海军, 战舰, 抵近, 跟踪, 监视, 。]
你也可以训练自己领域的分词模型：

    public static void main(String[] args) {
        //传入状态数和字符数
    		Segmenter segmenter = new FirstOrderHMMSegmenter(4, 65536);
    		String[] corpus = null;//需要语料
    		segmenter.train(corpus);
    		segmenter.saveModel("a directory");//保存模型到一个目录，如果null默认保存到项目中的model文件夹
    	}
目前只提供了一阶和二阶HMM分词模型的训练，上面是一阶模型示例。  
**2.2 通用HMM的使用**  
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
    
   
3.项目结构
------
src #源码  
---com.outsider   
---------------demo #示例    
---------------common #通用的一些工具类   
---------------nlp #和nlp相关的一些实现   
--------------model #模型的实现，主要的算法实现   
data #分词训练语料   
model #模型  
    
3.瞻望
----

因为在学习机器学习和nlp，希望能做点什么东西，另外也是看了hancks作者的开源项目hanlp有感，于是写这个项目。目前还存在很多问题，包括分词准确率，模型太少等等，后期会继续更新扩展，尽量完善，兼顾性能和接口的易使用，简单。



