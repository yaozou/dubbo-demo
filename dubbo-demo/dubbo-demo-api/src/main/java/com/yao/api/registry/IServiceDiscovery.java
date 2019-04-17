package com.yao.api.registry;

/**
 * @Description:
 * @author: yaozou
 * @Date: 2019/4/17 10:40
 */
public interface IServiceDiscovery {
    /**
     * 根据服务名称得到调用地址
     * @param serviceName
     * @return
     */
    String discover(String serviceName);
}
