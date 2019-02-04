package com.outsider.model.crf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import com.outsider.common.util.Storable;
import com.outsider.common.util.StorageUtils;

/**
 * 特征模板描述
 * 注意只解析每一行
 * 例如：
 * 					U1:%x[0,0]\n"
				   "U2:%x[-1,0]\n"
				   "U3:%x[-1,0]\n"
				   "U4:%x[0,0]/%x[-1,0]/%x[1,0]
	解析每一行保存为一个特征模板
 * @author outsider
 *
 */
public class FeatureTemplate implements Serializable, Storable{
	//保存前缀Uxx
	//private String prefix;
	/**
	 * 解析模板的正则表达式
	 */
	public static final String regx = "%x\\[(-{0,1}\\d),(\\d)\\]";
	/**
	 * 保存 行偏移和列位置
	 * 一个int[] int[0] 保存行偏移，int[1]保存列位置
	 */
	private List<int[]> offsetList;
	/**
	 * 保存特征模板
	 */
	private String template;
	/**
	 * 特征模板的编号
	 */
	private short templateNumber;
	
	public static final Pattern PATTERN = Pattern.compile(regx);
	public FeatureTemplate(String template) {
		this.template = template ;
	}
	public FeatureTemplate() {
	}
	public List<int[]> getOffsetList() {
		return offsetList;
	}

	public void setOffsetList(List<int[]> offsetList) {
		this.offsetList = offsetList;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public void setTemplateNumber(short templateNumber) {
		this.templateNumber = templateNumber;
	}
	public short getTemplateNumber() {
		return templateNumber;
	}
	/**
	 * 构建特征模板
	 * @param template
	 * @return
	 */
	public static FeatureTemplate createFeatureTemplate(String template) {
		FeatureTemplate featureTemplate = new FeatureTemplate();
		featureTemplate.offsetList = new ArrayList<>();
		featureTemplate.template = template;
		Matcher matcher = PATTERN.matcher(template);
		int i = 0;
		while(matcher.find()) {
			if(i == 0 ) {
				int start = matcher.start();
				//包括冒号
				featureTemplate.templateNumber = Short.parseShort(template.substring(1, start-1));
				i++;
			} 
			int[] t = new int[] {Integer.parseInt(matcher.group(1)),Integer.parseInt(matcher.group(2)) };
			featureTemplate.offsetList.add(t);
		}
		/**
		 * 解析到Bigram的B，匹配不上
		 */
		if(featureTemplate.offsetList.size() == 0) {
			return null;
		}
		return featureTemplate;
	}
	
	@Override
	public String toString() {
		return "FeatureTemplate:"+template;
	}
	
	
	
	public static void main(String[] args) {
		String template = "U1:%x[0,0]\n"+
				   "U2:%x[-1,0]\n"+
				   "U3:%x[-1,0]\n"+
				   "U4:%x[0,0]/%x[-1,0]/%x[1,0]";
		System.out.println(Arrays.toString(template.split("\n")));
	}
	
	@Override
	public void save(String directory, String fileName) {
		StorageUtils.save(directory, fileName, this);
	}
	
	@Override
	public void open(String directory, String fileName) {
		StorageUtils.open(directory, fileName, this);
	}
	
	
}
