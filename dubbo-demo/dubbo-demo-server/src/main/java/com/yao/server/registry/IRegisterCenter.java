package com.yao.server.registry;

/**
 * @Description: 注册中心
 * @author: yaozou
 * @Date: 2019/4/16 17:54
 */
public interface IRegisterCenter {
    /**
     * 将serviceName与serviceAddress绑定一起注册在zk上
     * @param serviceName com.yao.server.api.IGpHello
     * @param serviceAddress 127.0.0.1:8080
     */
    void register(String serviceName,String serviceAddress);
}
