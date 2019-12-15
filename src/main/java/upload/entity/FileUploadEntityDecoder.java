package upload.entity;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FileUploadEntityDecoder extends ByteToMessageDecoder {

    public final int BASE_LENGTH = 4 + 4;

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, List<Object> out) throws Exception {

        // 可读长度必须大于基本长度
        if (buffer.readableBytes() >= BASE_LENGTH) {

            // 记录包头开始的index
            int beginReader;

            while (true) {
                // 获取包头开始的index
                beginReader = buffer.readerIndex();
                // 标记包头开始的index
                buffer.markReaderIndex();
                // 读到了协议的开始标志，结束while循环
                if (buffer.readInt() == 0x76) {
                    break;
                }

                // 未读到包头，略过一个字节
                // 每次略过，一个字节，去读取，包头信息的开始标记
                buffer.resetReaderIndex();
                buffer.readByte();

                // 当略过，一个字节之后，
                // 数据包的长度，又变得不满足
                // 此时，应该结束。等待后面的数据到达
                if (buffer.readableBytes() < BASE_LENGTH) {
                    return;
                }
            }

            // 消息的长度
            int length = buffer.readInt();
            // 判断请求数据包数据是否到齐 解决拆包问题
            if (buffer.readableBytes() < length) {
                // 还原读指针
                buffer.readerIndex(beginReader);
                return;
            }

            // 读取data数据
            byte[] data = new byte[length];
            buffer.readBytes(data);

            FileUploadEntity protocol = new FileUploadEntity();
            protocol.setBytes(data);
            protocol.setDataLength(length);

            out.add(protocol);
        }
    }


}
