package com.outsider.nlp.nameEntityRecognition;

import java.util.List;

import com.outsider.common.dataStructure.Table;
import com.outsider.common.util.IOUtils;
import com.outsider.model.data.NERCRFDataConverter;
import com.outsider.model.hmm.SequenceNode;

public class SupervisedCRFNERTest {
	public static void main(String[] args) {
		//由于没有优化模型存储和加载，所以模型很大，加载也慢，不推荐
		//train();
		use();
	}
	
	
	public static void train() {
		String dataPath = "./data/ner/train_crf.txt";
		String srcData = IOUtils.readTextWithLineCheckBreak(dataPath, "utf-8");
		Table table = Table.generateTable(srcData, "\t");
		NERCRFDataConverter converter = new NERCRFDataConverter(0, 1);
		List<SequenceNode> nodes = converter.convert(table);
		SupervisedCRFNER ner = new SupervisedCRFNER();
		ner.train(nodes);
		//ner.save("./model/ner", null);
	}
	
	public static void use() {
		SupervisedCRFNER ner = new SupervisedCRFNER();
		long start = System.currentTimeMillis();
		ner.open("./model/ner", null);
		long end = System.currentTimeMillis();
		System.out.println("done...in "+((end - start) / 1000)+"seconds");
		System.out.println("done...");
		List<Entity> ents = ner.extractEntity("中国成立于1949年10月1日，北京是中国的首都，人民大会堂是政治会议中心。");
		for(Entity entity : ents) {
			System.out.println(entity);
		}
		//NERTaggerTest.score(ner);
	}
	
	
}
