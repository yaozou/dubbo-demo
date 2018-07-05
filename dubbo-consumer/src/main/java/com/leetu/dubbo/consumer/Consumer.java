package com.leetu.dubbo.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.leetu.dubbo.api.DemoService;

public class Consumer {
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "dubboContext.xml" });
		context.start();

		DemoService demoService = (DemoService) context.getBean("demoService");
		String hello = demoService.sayHello("this is test");
		System.out.println(hello);

		System.in.read();
	}
}
