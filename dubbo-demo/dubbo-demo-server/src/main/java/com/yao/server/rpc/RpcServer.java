package com.yao.server.rpc;

import com.yao.server.annotation.RpcAnnotation;
import com.yao.server.bean.RpcRequest;
import com.yao.server.registry.IRegisterCenter;

import java.util.HashMap;
import java.util.Map;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @Description:
 * @author: yaozou
 * @Date: 2019/4/17 14:37
 */
public class RpcServer {
    private Map<String,Object> handlerMap = new HashMap<>(16);
    private IRegisterCenter registerCenter;
    private String serviceAddress;

    public RpcServer(IRegisterCenter registerCenter,String serviceAddress){
        this.registerCenter = registerCenter;
        this.serviceAddress = serviceAddress;
    }

    /**
     * 綁定
     */
    public void bind(Object... services){
        for(Object service:services){
            // 得到服务名称
            RpcAnnotation annotation = service.getClass().getAnnotation(RpcAnnotation.class);
            String serviceName = annotation.value().getName();
            // 根据服务名称调用大队用的子类对象实现
            handlerMap.put(serviceName,service);
        }
    }


    /**
     * 注冊服務
     */
    public void publisher() {
        for(String serviceName:handlerMap.keySet()){
            registerCenter.register(serviceName,serviceAddress);
        }
        //启动一个netty监听
        try{
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workGroup = new NioEventLoopGroup();
            // 启动netty服务
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(
                            Integer.MAX_VALUE,0,0));
                    pipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
                    pipeline.addLast("encoder",new ObjectEncoder());
                    pipeline.addLast("Decoder",new ObjectDecoder(
                            Integer.MAX_VALUE, ClassResolvers.cacheDisabled(RpcRequest.class.getClassLoader())));
                    pipeline.addLast(new RpcServerHandler(handlerMap));

                }
            }).option(ChannelOption.SO_BACKLOG,128).childOption(ChannelOption.SO_KEEPALIVE,true);
            String[] addrs = serviceAddress.split(":");
            String ip = addrs[0];
            int port = Integer.parseInt(addrs[1]);
            ChannelFuture future = bootstrap.bind(ip,port).sync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
