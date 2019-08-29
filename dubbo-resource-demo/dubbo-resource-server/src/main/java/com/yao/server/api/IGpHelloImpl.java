package com.yao.server.api;

import com.yao.server.annotation.RpcAnnotation;

import javax.xml.ws.soap.Addressing;

/**
 * @Description:
 * @author: yaozou
 * @Date: 2019/4/16 17:45
 */
@RpcAnnotation(IGpHello.class)
@Addressing
public class IGpHelloImpl implements IGpHello {
    @Override
    public String sayHello(String name) {
        return "I'm "+name;
    }
}
