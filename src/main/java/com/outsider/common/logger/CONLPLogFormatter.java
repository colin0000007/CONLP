package com.outsider.common.logger;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CONLPLogFormatter extends Formatter{

	@Override
	public String format(LogRecord record) {
		//System.out.println(record.getMessage());
		return record.getMessage()+"\n";
	}

}
