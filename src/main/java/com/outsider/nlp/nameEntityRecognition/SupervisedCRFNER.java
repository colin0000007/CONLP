package com.outsider.nlp.nameEntityRecognition;

import java.util.List;

import com.outsider.common.dataStructure.Table;
import com.outsider.model.crf.SupervisedCRF;
import com.outsider.nlp.segmenter.SegmentationUtils;

public class SupervisedCRFNER extends SupervisedCRF implements NER{

	public SupervisedCRFNER() {
		super();
	}

	public SupervisedCRFNER(int observationNum, int stateNum) {
		super(observationNum, stateNum);
	}

	@Override
	public List<Entity> extractEntity(String text) {
		int[] xids = SegmentationUtils.str2int(text);
		int[] yids = veterbi(xids);
		String[] tags = new String[yids.length];
		for(int i = 0; i < tags.length; i++) {
			tags[i] = EntityType.id2tag[yids[i]];
		}
		return NERUtils.decode(text, tags);
	}

	@Override
	public List<Entity> getPersonNameEntity(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Entity> getLocationEntity(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Entity> getOrganizationEntity(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void train(Table table, int xColumnIndex, int yColumnIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String generateModelTemplate() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		String dataPath = "./data/ner/train1_crf.txt";
		//String srcData = IOUtils.readTextWithLineCheckBreak(dataPath, "utf-8");
		//Table table = Table.generateTable(srcData, "\t");
		//NERCRFDataConverter converter = new NERCRFDataConverter(0, 1);
		//List<SequenceNode> nodes = converter.convert(table);
		SupervisedCRFNER ner = new SupervisedCRFNER(65536, EntityType.id2tag.length);
		//ner.train(nodes);
		//ner.save("./model/ner", null);
		long start = System.currentTimeMillis();
		ner.open("./model/ner", null);
		long end = System.currentTimeMillis();
		System.out.println("done...in "+((end - start) / 1000)+"seconds");
		System.out.println("done...");
		List<Entity> ents = ner.extractEntity("中国成立于1949年10月1日，北京是中国的首都，人民大会堂是政治会议中心。");
		for(Entity entity : ents) {
			System.out.println(entity);
		}
	}

}
