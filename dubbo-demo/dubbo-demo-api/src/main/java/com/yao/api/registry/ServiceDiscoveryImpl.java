package com.yao.api.registry;

import com.yao.api.loadbalance.LoadBalance;
import com.yao.api.loadbalance.RandomLoadBalance;
import com.yao.server.bean.ZkConfig;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: yaozou
 * @Date: 2019/4/17 10:42
 */
public class ServiceDiscoveryImpl implements IServiceDiscovery {
    List<String> repos = new ArrayList<>();
    private CuratorFramework curatorFramework;
    {
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(ZkConfig.CONNECTION_STR).sessionTimeoutMs(4000).
                retryPolicy(new ExponentialBackoffRetry(100,10)).build();
        curatorFramework.start();
    }
    @Override
    public String discover(String serviceName) {
        // /registrys/com.yao.server.api.IGpHello
        String path = com.yao.api.registry.ZkConfig.ZK_REGISTER_PATH +"/"+serviceName;
        try {
            repos = curatorFramework.getChildren().forPath(path);
        }catch (Exception e){
            e.printStackTrace();
        }
        // 动态感知服务节点的一个变化 监听
        registerWatch(path);
        // 负载均衡 随机
        LoadBalance loadBalance =  new RandomLoadBalance();
        return loadBalance.select(repos);
    }

    private void registerWatch(final String path) {
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework,path,true);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                repos = curatorFramework.getChildren().forPath(path);
            }
        };
        childrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            childrenCache.start();
        }catch (Exception e){
            throw new RuntimeException("注册PathChild Watcher 异常："+e);
        }
    }
}
