package com.outsider.zhifac.crf4j.extension;

import com.zhifac.crf4j.CrfLearn;
/**
 * 对CrfPP中用于训练模型的CrfLearn.run做一个简单的包装
 * 任何Crf的训练都经过此方法
 * 注意训练数据的格式必须按照Crf的标准格式，即二维表格，行之前以换行符分割，列之间以制表符\t分割
 * @author outsider
 * 
 * 训练过程中输出各值的含义:
 *  iter: number of iterations processed
	terr: error rate with respect to tags. (# of error tags/# of all tag)
	serr: error rate with respect to sentences. (# of error sentences/# of all sentences)
	obj: current object value. When this value converges to a fixed point, CRF++ stops the iteration.
	diff: relative difference from the previous object value.
 *
 *
 *
 *其他参数：
 *N-best outputs：
 *	With the -n option, you can obtain N-best results sorted by the conditional probability of CRF. With n-best output mode, CRF++ first gives one additional line like "# N prob", where N means that rank of the output starting from 0 and prob denotes 
 *	the conditional probability for the output.
 *verbose level：
 *	The -v option sets verbose level. default value is 0. By increasing the level, you can have an extra information from CRF++
	level 1：
		You can also have marginal probabilities for each tag (a kind of confidece measure for each output tag) and a conditional probably for the output (confidence measure for the entire output).
 	level 2：
		You can also have marginal probabilities for all other candidates.
 */
public class CrfppTrainer {
	/**
	 * 对CrfPP中用于训练模型的CrfLearn.run做一个简单的包装
	 * @param templateFilePath 模板文件路径
	 * @param trainDataFilePath 训练数据路径，注意训练数据的格式必须按照Crf的标准格式，即二维表格，行之前以换行符分割，列之间以制表符\t分割
	 * @param modelFile 模型保存路径
	 * @param options 其他命令参数
	 *  -f, –freq=INT使用属性的出现次数不少于INT(默认为1)，特征出现少于1次这丢弃该特征函数，在大量训练数据时可以减少特征函数的个数
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
	 */
	public static void run(String templateFilePath, String trainDataFilePath, String modelFile,
			String[] options) {
		String[] args = new String[] {templateFilePath, trainDataFilePath, modelFile};
		if(options != null && options.length > 0) {
			String[] newArgs = new String[args.length + options.length];
			System.arraycopy(args, 0, newArgs, 0, args.length);
			for(int i = 0; i < options.length; i++) {
				newArgs[i + args.length] = options[i];
			}
			args = newArgs;
		}
		CrfLearn.run(args);
	}
}
