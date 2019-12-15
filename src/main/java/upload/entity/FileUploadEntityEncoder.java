package upload.entity;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class FileUploadEntityEncoder extends MessageToByteEncoder<FileUploadEntity> {

    protected void encode(ChannelHandlerContext channelHandlerContext, FileUploadEntity msg, ByteBuf out) throws Exception {
        // 1.写入消息的开头的信息标志(int类型)
        out.writeInt(msg.getHeadData());
        // 2.写入消息的长度(int 类型)
        out.writeInt(msg.getDataLength());
        // 3.写入消息的内容(byte[]类型)
        out.writeBytes(msg.getBytes());

    }
}
