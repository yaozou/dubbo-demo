package com.yao.server;

import com.yao.server.api.IGpHello;
import com.yao.server.api.IGpHelloImpl;
import com.yao.server.registry.IRegisterCenter;
import com.yao.server.registry.RegisterCenterImpl;
import com.yao.server.rpc.RpcServer;

/**
 * @Description:
 * @author: yaozou
 * @Date: 2019/4/16 17:44
 */
public class ServerTest {
    public static void main(String[] args){
        IRegisterCenter registerCenter = new RegisterCenterImpl();
        RpcServer rpcServer = new RpcServer(registerCenter,"127.0.0.1:8080");

        IGpHello iGpHello = new IGpHelloImpl();
        rpcServer.bind(iGpHello);
        rpcServer.publisher();

        while (true){}
    }
}
