package com.outsider.nlp.nameEntityRecognition;

import java.util.List;

import com.outsider.common.dataStructure.Table;
import com.outsider.common.util.IOUtils;
import com.outsider.model.crf.SupervisedCRF;
import com.outsider.model.data.NERCRFDataConverter;
import com.outsider.model.hmm.SequenceNode;
import com.outsider.nlp.segmenter.SegmentationUtils;

public class SupervisedCRFNER extends SupervisedCRF implements NER{

	public SupervisedCRFNER() {
		super(65536,EntityType.id2tag.length);
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
		/*String dataPath = "./data/ner/train_crf.txt";
		String srcData = IOUtils.readTextWithLineCheckBreak(dataPath, "utf-8");
		Table table = Table.generateTable(srcData, "\t");
		NERCRFDataConverter converter = new NERCRFDataConverter(0, 1);
		List<SequenceNode> nodes = converter.convert(table);*/
		SupervisedCRFNER ner = new SupervisedCRFNER();
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
		
		NERTaggerTest.score(ner);
		/**
		 * hmm:
		 *  中国,L(0,1)
			北京,L(16,17)
			中国,L(19,20)
			人民大会,O(25,28)
			
			precision:0.6044205,recall:0.5618832,F-score:0.5823762,errorRate:0.36773986
			监督crf:
			precision:0.6969159,recall:0.5081702,F-score:0.587762,errorRate:0.22099984

		 */
	}

}
