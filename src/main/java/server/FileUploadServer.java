package server;

import entity.FileUploadEntityDecoder;
import entity.FileUploadEntityEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


public class FileUploadServer {
    public void bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            // 这里实现了自定义的编解码
                            // 但是由于只定义了开始位置的字节码，数据长度，以及数据，没有去定义这个文件名称，以及后缀
                            // 所以导致存的文件名是一个null。修改了后缀的扩展名，发现会是一个图片。这里在encode和decode里还要在定义一下文件名称。
                            ch.pipeline().addLast(new FileUploadEntityEncoder());
                            ch.pipeline().addLast(new FileUploadEntityDecoder());
//                            ch.pipeline().addLast(new ObjectEncoder());
//                            ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)));
                            ch.pipeline().addLast(new FileUploadServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            System.out.println("服务端启动....");
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        try {
            new FileUploadServer().bind(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}