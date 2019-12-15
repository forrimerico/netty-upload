# netty-upload
## 批量上传文件
#### 实现方式 

- 给定一个文件路径，里面包含需要上传的N个文件 
- 单进程一个一个上传

#### 实现代码 

```
for (File f : files) {
   System.out.println(f.getName());
   FileUploadEntity uploadFile = new FileUploadEntity();

   String fileName = f.getName();// 文件名
   uploadFile.setFile(f);
   uploadFile.setFileName(fileName);

   //连接到服务器 并上传
   new FileUploadClient().connect(port, "127.0.0.1", uploadFile);
            }
```

#### 优点
- 实现简单
- 资源消耗不会很多（控制好每片上传的大小）

#### 缺点
- 速度慢
- 缺少异常处理（服务中断了，就得重新传）

### 实现自定义的编解码，解决粘包和拆包问题
> 主要实现了两个类。 FileUploadEntityDecoder 和 FileUploadEntityDecoder

##### 遇到的问题
- 传输文件的名字丢了。
> 主要还是靠协议。<br/> 
起始头 ：0f86 
文件名长度：int  文件名byte  
数据长度：int 数据流byte 

- 服务端向客户端发送的消息，客户端收不到
> 待解决。这个是个致命问题。

### 心跳机制

#### 解决的问题
> 客户端长时间无响应，应该断开，而不是占着连接资源

#### 模拟实现思路
> 1、客户端向服务端发一段消息，并sleep 随机秒<br/>
2、服务端收到消息，回一条消息。<br>
3、服务端超过 XX 秒没收到客户端的消息，主动断开<br>
4、客户端发了消息没收到回复，主动重连


