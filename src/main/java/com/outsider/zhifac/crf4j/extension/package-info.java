package com.outsider.zhifac.crf4j.extension;
/*
 * 这个包下面写一些对crf4j的扩展，包装
 * 
 *1.运行参数说明：
 *   	-f, –freq=INT使用属性的出现次数不少于INT(默认为1)，特征出现少于1次这丢弃该特征函数，在大量训练数据时可以减少特征函数的个数
	 *	-m, –maxiter=INT设置INT为LBFGS的最大迭代次数 (默认10k)
	 *	-c, –cost=FLOAT      设置FLOAT为代价参数，过大会过度拟合 (默认1.0)，控制模型的一个很重要参数，控制过拟合
	 *	-e, –eta=FLOAT设置终止标准FLOAT(默认0.0001)
	 *	-C, –convert将文本模式转为二进制模式
	 *	-t, –textmodel为调试建立文本模型文件
	 *	-a, –algorithm=(CRF|MIRA)
	 *	选择训练算法，默认为CRF-L2
	 *	-p, –thread=INT线程数(默认1)，利用多个CPU减少训练时间
	 *	-H, –shrinking-size=INT
	 *	设置INT为最适宜的跌代变量次数 (默认20)
	 *	-h, –help显示帮助并退出
	 *
   
   2.模型打开参数说明：
   	N-best outputs：
	 	With the -n option, you can obtain N-best results sorted by the conditional probability of CRF. With n-best output mode, CRF++ first gives one additional line like "# N prob", where N means that rank of the output starting from 0 and prob denotes 
	 	the conditional probability for the output.
 
 	verbose level：
	 	The -v option sets verbose level. default value is 0. By increasing the level, you can have an extra information from CRF++
		level 1：
			You can also have marginal probabilities for each tag (a kind of confidece measure for each output tag) and a conditional probably for the output (confidence measure for the entire output).
	 	level 2：
			You can also have marginal probabilities for all other candidates.
 *  cost-factor：
 *  	With this option, you can change the hyper-parameter for the CRFs.
 *  	With larger C value, CRF tends to overfit to the give training corpus. This parameter trades the balance between overfitting and underfitting. The results will significantly be influenced by this parameter. You can find an optimal value by using held-out data or more general model selection method such as cross validation.
 * 
 * 3.模型训练过程中的输出含义：
 *  iter: number of iterations processed
	terr: error rate with respect to tags. (# of error tags/# of all tag)
	serr: error rate with respect to sentences. (# of error sentences/# of all sentences)
	obj: current object value. When this value converges to a fixed point, CRF++ stops the iteration.
	diff: relative difference from the previous object value.
	
  4.注意TaggerImpl中有些方法并没有实现，比如add(String[] lines); parse(String str);
  
*/
