package com.yao.api.proxy;

import com.yao.api.registry.IServiceDiscovery;
import com.yao.server.bean.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @Description:
 * @author: yaozou
 * @Date: 2019/4/17 15:15
 */
public class RpcClientProxy {
    private IServiceDiscovery serviceDiscovery;

    public RpcClientProxy(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(final Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new RpcClientHandler(interfaceClass, serviceDiscovery));
    }
}

class RpcClientHandler implements InvocationHandler {
    Class interfaceClass;
    IServiceDiscovery serviceDiscovery;

    public RpcClientHandler(Class interfaceClass, IServiceDiscovery serviceDiscovery) {
        this.interfaceClass = interfaceClass;
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 服务发现 url
        String serviceName = interfaceClass.getName();
        String serviceAddress = serviceDiscovery.discover(serviceName);
        String[] addrs = serviceAddress.split(":");
        String host = addrs[0];
        int port = Integer.parseInt(addrs[1]);
        //url netty请求
        // 封装request
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setTypes(method.getParameterTypes());
        request.setParams(args);
        // 发socket netty
        final RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();
        //启动一个netty监听
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast("handler",rpcProxyHandler);
                        }
                    });

            ChannelFuture future = b.connect("localhost", 8080).sync();
            future.channel().writeAndFlush(request).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
        // 服务器写过来的数据
        return rpcProxyHandler.getResponse();
    }
}
