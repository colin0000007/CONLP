package com.outsider.nlp.dependency;
/**
 * 注意：尽量不要用带标点的句子测试
 * 因为训练语料中并没有带标点，如果多个标点分割很可能时多个逻辑独立的短句子
 * 可能造成多个短句只有一个核心成分，因为算法中处理为只能有一个核心成分
 * 可以考虑将句子分割成多个短句子，分别分析。
 * @author outsider
 *
 */
public class MaxEntDependencyParserTest {
	
	public static void main(String[] args) {
		DependencyParser parser = StaticDependencyParser.getMaxEntDependencyParser();
		CoNLLSentence sentence1 = parser.parse("我每天都在写程序");
		System.out.println(sentence1);
		System.out.println();
		CoNLLSentence sentence2 = parser.parse("摘下星星送给你");
		System.out.println(sentence2);
		System.out.println();
		CoNLLSentence sentence3 = parser.parse("我目前在魔都某女校学习外语");
		System.out.println(sentence3);
		System.out.println();
		CoNLLSentence sentence4 = parser.parse("我爱你");
		System.out.println(sentence4);
		System.out.println();
		CoNLLSentence sentence5 = parser.parse("曾经有一份真挚的爱情摆在我面前");
		System.out.println(sentence5);
		System.out.println();
		CoNLLSentence sentence6 = parser.parse("曾经有一份真挚的爱情摆在我面前，我没有珍惜，直到失去才追悔莫及。");
		System.out.println(sentence6);
		System.out.println();
		CoNLLSentence sentence7 = parser.parse("我没有珍惜");
		System.out.println(sentence7);
		System.out.println();
		CoNLLSentence sentence8 = parser.parse("直到失去才追悔莫及");
		System.out.println(sentence8);
		System.out.println();
		CoNLLSentence sentence9 = parser.parse("最大熵是什么好吃吗");
		System.out.println(sentence9);
		System.out.println();
		CoNLLSentence sentence10 = parser.parse("把市场经济奉行的等价交换原则引入党的生活和国家机关政务活动中");
		System.out.println(sentence10);
		System.out.println();
		
		
		
		//目前出现的问题？不合法情况会出现，准确率应该不够高。
		//和hancks做的比起来感觉要差一些，可能的原因（1）特征模板不一样（2）最小生成树这里可能有问题？
		//刚才试了下，hancks产生的特征维度有28个维度（去掉标签）我的是21个
	}
	
	/**

11	我	我	r	r	_	5	施事
2	每天	每天	r	r	_	5	施事
3	都	都	d	d	_	5	程度
4	在	在	p	p	_	5	介词依存
5	写	写	v	v	_	0	核心成分
6	程序	程序	n	n	_	5	内容


1	摘下	摘下	v	v	_	2	限定
2	星星	星星	n	n	_	3	受事
3	送给	送给	v	v	_	0	核心成分
4	你	你	r	r	_	3	受事


1	我	我	r	r	_	7	施事
2	目前	目前	t	t	_	7	时间
3	在	在	p	p	_	6	介词依存
4	魔都	魔都	n	n	_	6	限定
5	某	某	r	r	_	6	限定
6	女校	女校	n	n	_	7	施事
7	学习	学习	v	v	_	0	核心成分
8	外语	外语	n	n	_	7	内容


1	坚决	坚决	a	a	_	2	方式
2	惩治	惩治	v	v	_	0	核心成分
3	贪污	贪污	v	v	_	7	限定
4	贿赂	贿赂	n	n	_	3	连接依存
5	等	等	u	u	_	3	连接依存
6	经济	经济	n	n	_	7	限定
7	犯罪	犯罪	v	v	_	2	受事

1	我	我	r	r	_	2	经验者
2	爱	爱	v	v	_	0	核心成分
3	你	你	r	r	_	2	目标

1	我	我	r	r	_	2	经验者
2	爱	爱	v	v	_	0	核心成分
3	你	你	r	r	_	2	目标
4	。	。	w	w	_	2	内容


1	曾经	曾经	d	d	_	2	时间
2	有	有	v	v	_	0	核心成分
3	一份	一份	n	n	_	6	限定
4	真挚	真挚	a	a	_	6	限定
5	的	的	u	u	_	2	“的”字依存
6	爱情	爱情	n	n	_	7	施事
7	摆	摆	v	v	_	2	受事
8	在	在	p	p	_	10	介词依存
9	我	我	r	r	_	10	限定
10	面前	面前	f	f	_	7	处所

	 */
}
