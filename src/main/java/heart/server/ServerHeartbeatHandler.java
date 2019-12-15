package heart.server;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by Yohann on 2016/11/9.
 */
public class ServerHeartbeatHandler extends ChannelInboundHandlerAdapter {

    // 心跳丢失计数器
    private int counter;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("--- Client is active ---");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("--- Client is inactive ---");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 判断接收到的包类型
        if (msg instanceof String) {

            // 如果是心跳包，
            if (msg.equals("heartdata")) {
                handleHeartbreat(ctx, (String) msg);
            } else {
                handleData(ctx, (String) msg);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 空闲6s之后触发 (心跳包丢失)
            if (counter >= 3) {
                // 连续丢失3个心跳包 (断开连接)
                ctx.channel().close().sync();
                System.out.println("已与Client断开连接");
            } else {
                counter++;
                System.out.println("丢失了第 " + counter + " 个心跳包");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("连接出现异常");
    }

    /**
     * 处理心跳包
     *
     * @param ctx
     * @param packet
     */
    private void handleHeartbreat(ChannelHandlerContext ctx, String packet) {
        // 将心跳丢失计数器置为0
        counter = 0;
        System.out.println("收到心跳包");
        ReferenceCountUtil.release(packet);
    }

    /**
     * 处理数据包
     *
     * @param ctx
     * @param packet
     */
    private void handleData(ChannelHandlerContext ctx, String packet) {
        // 将心跳丢失计数器置为0
        counter = 0;
        System.out.println(packet);
        ReferenceCountUtil.release(packet);
    }
}