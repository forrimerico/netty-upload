package client;

import entity.FileUploadEntity;
import entity.FileUploadEntityDecoder;
import entity.FileUploadEntityEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 文件上传demo 客户端
 * @Author: walking
 * @Date: 2019年10月29日16:50:25
 */
public class FileUploadClient {

    private static String file_name = "E:\\游玩\\青山湖接力跑\\video\\DJI_0325.JPG";

    public void connect(int port, String host, final FileUploadEntity fileUploadEntity) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new FileUploadEntityEncoder());
                            ch.pipeline().addLast(new FileUploadEntityDecoder());
//                            ch.pipeline().addLast(new ObjectEncoder());
//                            ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)));
                            ch.pipeline().addLast(new FileUploadClientHandler(fileUploadEntity));
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();
            System.out.println("客户端启动...");
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
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
            // 这里是单文件上传。如何实现多文件批量上传呢？
            /**
             * 1、 多个文件单进程一个一个传。在客户端这边做处理。
             * 传进来一个文件，然后循环遍历这个文件下的文件，实现一个批量上传的功能。
             * 实际上就是在这里写个循环，循环一个文件夹下面的文件。
             */
            //构建上传文件对象
//            List<File> files = getFiles("E:\\游玩\\青山湖接力跑\\video");
            File f = new File(file_name);
//            for(File f : files){
            System.out.println(f.getName());
            FileUploadEntity uploadFile = new FileUploadEntity();

            String fileName = f.getName();// 文件名
            uploadFile.setFile(f);
            uploadFile.setFileName(fileName);

            //连接到服务器 并上传
            new FileUploadClient().connect(port, "127.0.0.1", uploadFile);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<File> getFiles(String path) {
        File root = new File(path);
        List<File> files = new ArrayList<File>();
        if (!root.isDirectory()) {
            files.add(root);
        } else {
            File[] subFiles = root.listFiles();
            for (File f : subFiles) {
                files.addAll(getFiles(f.getAbsolutePath()));
            }
        }
        return files;
    }
}