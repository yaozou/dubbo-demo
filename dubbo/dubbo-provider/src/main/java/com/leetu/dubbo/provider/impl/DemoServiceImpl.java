package com.leetu.dubbo.provider.impl;

import com.leetu.dubbo.api.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.ws.rs.Path;

/**
 * 服务提供方实现接口
 *
 */
@Path("pay")
@Component("PayOrderFacadeImpl")
@Slf4j
public class DemoServiceImpl implements DemoService {

	@Override
	public String sayHello(String name) {
		return "Hello " + name;
	}

}
