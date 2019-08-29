package com.yao.api;

import com.yao.api.proxy.RpcClientProxy;
import com.yao.api.registry.IServiceDiscovery;
import com.yao.api.registry.ServiceDiscoveryImpl;
import com.yao.server.api.IGpHello;

/**
 * @Description:
 * @author: yaozou
 * @Date: 2019/4/15 15:10
 */
public class ApiTest {
    public static void main(String[] args){
        //动态代理类
        IServiceDiscovery serviceDiscovery = new ServiceDiscoveryImpl();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(serviceDiscovery);
        //远程调用
        IGpHello iGpHello = rpcClientProxy.create(IGpHello.class);
        System.out.println(iGpHello.sayHello("Yao"));
    }
}
