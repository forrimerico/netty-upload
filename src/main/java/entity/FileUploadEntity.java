package entity;

import com.sun.org.apache.bcel.internal.classfile.ConstantValue;

import java.io.File;
import java.io.Serializable;

public class FileUploadEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private File file;// 文件
    private String fileName;// 文件名
    private byte[] bytes;// 文件字节数组
    private int dataLength;// 数据长度
    private int headData = 0x76; // 自定义消息流开始的标志

    public int getHeadData() {
        return headData;
    }

    public void setHeadData(int headData) {
        this.headData = headData;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }
//getter and setter
    //overwrite toString()
}
