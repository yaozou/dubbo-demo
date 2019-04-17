package com.yao.api.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * @Description:
 * @author: yaozou
 * @Date: 2019/4/17 11:04
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> repos) {
        int len = repos.size();
        Random random = new Random();
        return repos.get(random.nextInt(len));
    }
}
