package com.yao.api.loadbalance;

import java.util.List;

/**
 * @Description: 负载均衡
 * @author: yaozou
 * @Date: 2019/4/17 11:03
 */
public interface LoadBalance {

    String select(List<String> repos);
}
