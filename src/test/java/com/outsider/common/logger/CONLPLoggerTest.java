package com.outsider.common.logger;

import java.util.logging.Logger;

public class CONLPLoggerTest {
	public static void main(String[] args) {
		//非全局日志器，类级别的日志器
		Logger logger = CONLPLogger.getLoggerOfAClass(CONLPLogger.class);
		logger.info("test Logger!");
		System.out.println();
		//全局日志器
		Logger globalLogger = CONLPLogger.logger;
		globalLogger.info("test gloabl logger!");
	}
}
