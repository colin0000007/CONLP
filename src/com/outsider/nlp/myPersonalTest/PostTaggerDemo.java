package com.outsider.nlp.myPersonalTest;

import org.junit.Test;

public class PostTaggerDemo {
	
	@Test
	public void testHMMTagger() {
		String path = "D:\\nlp语料\\词性标注\\train.txt";
		/**
		 * 从语料种统计:
		 *词性33个: ns,nt,nz,ws,a,c,d,mq,e,f,h,i,j,k,m,nhf,n,vd,o,p,q,r,u,nd,v,vl,w,nhs,x,nh,ni,nl,vu
		 *词数量:119280
		 */
	/*	long start = System.currentTimeMillis();
		String[] corpus = FileUtils.loadTextDataFromFile(path, "utf-8", " ");
		FirstOrderHMMPOSTagger tagger = new FirstOrderHMMPOSTagger(33, 119280);
		tagger.train(corpus);
		long end = System.currentTimeMillis();
		System.out.println("训练耗时:"+(end-start)/1000+"秒");
		String testPath = "D:\\\\nlp语料\\\\词性标注\\\\dev.txt";
		String[] testCorpus = LoadCorpus.loadDataFromFile(testPath, "utf-8", " ");
		//词性标注这里基本可以照搬分词的工作，在汉语中，大多数词语只有一个词性，或者出现频次最高的词性远远高于第二位的词性。
		tagger.output(new String[] {"你","最近","怎么样"},true);*/
		//float accuray = tagger.accuray(testCorpus);
		//System.out.println("测试语料准确率:"+accuray);//准确率只有60%左右
		//float accuray2 = tagger.accuray(corpus);
		//爆内存了 跑不出来
		//System.out.println("训练语料准确率:"+accuray2);
	}
}
