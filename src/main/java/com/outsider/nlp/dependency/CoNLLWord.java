package com.outsider.nlp.dependency;

public class CoNLLWord {
	private int ID;//当前词在句子中的序号，１开始.
	private String LEMMA;//当前词语（或标点）的原型或词干，在中文中，此列与FORM相同
	private String CPOSTAG;//粗粒度词性
	private String POSTAG;//细粒度词性
	private int HEAD;//当前词语的中心词,比如序号为2的词的中心词是4，那么2->4构成一条有向边
	private String DEPREL;//当前词语与中心词的依存关系
	
	//根节点
	public static final CoNLLWord ROOT = new CoNLLWord(0, "ROOT_L","ROOT_CP" ,"ROOT_P", -1,"");
	// out of index POSTAG 越界的细粒度词性
	public static final String OOIPOSTAG = "PNULL";
	// out of index CPOSTAG 越界的粗粒度词性
	public static final String OOICPOSTAG = "CNULL";
	// null dependency relation label 无依存关系标签
	public static final String NoneDEPREL = "DNULL";
	// the LEMMA of ROOT 根节点的LEMMA字段
	public static final String ROOT_LEMMA = "ROOT_L";
	// the CPOSTAG of ROOT 根节点的粗粒度词性
	public static final String ROOT_CPOSTAG = "ROOT_CP";
	//
	public static final String OOILEMMA = "LNULL";
	
	public CoNLLWord() {
	}
	
	public CoNLLWord(int iD, String lEMMA, String pOSTAG, int hEAD, String dEPREL) {
		super();
		ID = iD;
		LEMMA = lEMMA;
		POSTAG = pOSTAG;
		CPOSTAG = pOSTAG.substring(0, 1).toLowerCase();//粗粒度词性取细粒度词性的第一个字符
		HEAD = hEAD;
		DEPREL = dEPREL;
	}
	
	public CoNLLWord(int iD, String lEMMA, String cPOSTAG, String pOSTAG, int hEAD, String dEPREL) {
		super();
		ID = iD;
		LEMMA = lEMMA;
		CPOSTAG = cPOSTAG;
		POSTAG = pOSTAG;
		HEAD = hEAD;
		DEPREL = dEPREL;
	}

	public CoNLLWord(int iD, String lEMMA, String pOSTAG) {
		super();
		ID = iD;
		LEMMA = lEMMA;
		POSTAG = pOSTAG;
		CPOSTAG = pOSTAG.substring(0, 1).toLowerCase();//粗粒度词性取细粒度词性的第一个字符
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getLEMMA() {
		return LEMMA;
	}

	public void setLEMMA(String lEMMA) {
		LEMMA = lEMMA;
	}

	public String getCPOSTAG() {
		return CPOSTAG;
	}

	public void setCPOSTAG(String cPOSTAG) {
		CPOSTAG = cPOSTAG;
	}

	public String getPOSTAG() {
		return POSTAG;
	}

	public void setPOSTAG(String pOSTAG) {
		POSTAG = pOSTAG;
		CPOSTAG = pOSTAG.substring(0, 1).toLowerCase();
	}

	public int getHEAD() {
		return HEAD;
	}

	public void setHEAD(int hEAD) {
		HEAD = hEAD;
	}

	public String getDEPREL() {
		return DEPREL;
	}

	public void setDEPREL(String dEPREL) {
		DEPREL = dEPREL;
	}
	
}
