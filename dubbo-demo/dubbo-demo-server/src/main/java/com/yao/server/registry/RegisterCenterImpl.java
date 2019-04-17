package com.yao.server.registry;

import com.yao.server.bean.ZkConfig;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @Description:注册中心实现
 * @author: yaozou
 * @Date: 2019/4/16 17:56
 */
public class RegisterCenterImpl implements IRegisterCenter {
    private CuratorFramework curatorFramework;
    {
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(ZkConfig.CONNECTION_STR).sessionTimeoutMs(4000).
                retryPolicy(new ExponentialBackoffRetry(100,10)).build();
        curatorFramework.start();
    }
    @Override
    public void register(String serviceName, String serviceAddress) {
        //增加结点
        // registrys/com.yao.server.api.IGpHello
        String servicePath = ZkConfig.ZK_REGISTER_PATH + "/"+serviceName;
        try{
            if (curatorFramework.checkExists().forPath(servicePath) == null){
                curatorFramework.create().creatingParentContainersIfNeeded().
                        withMode(CreateMode.PERSISTENT).forPath(servicePath,"0".getBytes());
            }
            // 已经存在 /registrys/iGpHello
            // 服务器发布的地址:127.0.0.1:8080 address /registrys/com.yao.server.api.IGpHello
            String addressPath = servicePath+"/"+serviceAddress;
            String rsNode = curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(addressPath,"0".getBytes());
            System.out.println("服务注册成功："+rsNode);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
