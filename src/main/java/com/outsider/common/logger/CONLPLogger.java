package com.outsider.common.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CONLPLogger{
	/**
	 * CONLP全局日志器
	 */
	public static Logger logger = Logger.getLogger("CONLP");
	/**
	 * 关闭全局日志器
	 */
	public static void closeGlobalLogger() {
		logger.setLevel(Level.OFF);
	}
	/**
	 * 打开全局日志器
	 */
	public static void openGlobalAllLevelLogger() {
		logger.setLevel(Level.ALL);
	}
	/**
	 * 关闭一个指定的日志器
	 * @param logger
	 */
	public static void closeLogger(Logger logger) {
		logger.setLevel(Level.OFF);
	}
	/**
	 * 打开一个指定的日志器
	 * @param logger
	 */
	public static void openLogger(Logger logger) {
		logger.setLevel(Level.ALL);
	}
	/**
	 * 获取一个类专属的打印器
	 * 注意传入class相同将获取到同一个logger
	 * 使用默认的格式化:CONLPLogFormatter
	 * @param clazz
	 * @return
	 */
	public static Logger getLoggerOfAClass(Class clazz) {
		Handler handler = new ConsoleHandler();
		Logger logger =Logger.getLogger(clazz.getName());
		handler.setFormatter(new CONLPLogFormatter());
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		return logger;
	}
	/**
	 * 获取一个类的专属日志器
	 * 注意传入class相同将获取到同一个logger
	 * 指定格式化对象
	 * @param clazz
	 * @param formatter 格式化对象 
	 * @return
	 */
	public static Logger getLoggerOfAClass(Class clazz, Formatter formatter) {
		Handler handler = new ConsoleHandler();
		Logger logger =Logger.getLogger(clazz.getName());
		handler.setFormatter(formatter);
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		return logger;
	}
	
}
