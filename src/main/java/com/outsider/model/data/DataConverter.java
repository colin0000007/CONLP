package com.outsider.model.data;

public interface DataConverter<DataType,ConvertedType> {
	/**
	 * 将原始数据转换为
	 * @param rawData 原始数据
	 * @param otherParameters 可能用到的其他参数
	 * @return
	 */
	ConvertedType convert(DataType rawData, Object...otherParameters);
}
