package com.outsider.test;

import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.junit.Test;

public class ReadConfigFileTest {
	@Test
	public void t1() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("src/main/resources/path.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(properties.getProperty("LOG_PATH"));
		URL url = this.getClass().getResource("");
		System.out.println(url.toString());
	}
	
	@Test
	public void t2() {
		int[] arr = new int[10];
		int i = 0;
		while(i < 10) {
			arr[i++] = (int) (Math.random()*100);
		}
		for(int j = 0; j < 10; j++) {
			System.out.println(arr[j]);
		}
	}
}
