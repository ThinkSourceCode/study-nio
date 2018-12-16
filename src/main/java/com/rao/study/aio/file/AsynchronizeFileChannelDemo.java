package com.rao.study.aio.file;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

public class AsynchronizeFileChannelDemo {

    /**
     * 使用Future回调
     * @throws Exception
     */
    @Test
    public void testRead() throws Exception{
        Path path = Paths.get("1.txt");
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

        //读取文件
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //采用feture的形式获取,这里是异步的
        Future<Integer> future = asynchronousFileChannel.read(byteBuffer,0);

        while(!future.isDone()){//异步处理是否完毕
        }

        //处理完毕
        byteBuffer.flip();
        //获取数据
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        System.out.println(new String(bytes,0,bytes.length));
    }

    @Test
    public void testRead1() throws Exception{
        Path path = Paths.get("1.txt");
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

        //读取文件
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //采用回调函数回调
        asynchronousFileChannel.read(byteBuffer,0,byteBuffer,new ReadCompleteHandler());
    }

    @Test
    public void testWrite() throws Exception{
        Path path = Paths.get("5.txt");
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE_NEW,StandardOpenOption.WRITE);

        String str = "aaaaaaaaa";
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();
        Future<Integer> integerFuture = asynchronousFileChannel.write(byteBuffer,0);

        while (!integerFuture.isDone()){}

        System.out.println("do done");
    }

    @Test
    public void testWrite1() throws Exception{
        Path path = Paths.get("5.txt");
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE,StandardOpenOption.WRITE);

        String str = "ffffffffffff";
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();
        asynchronousFileChannel.write(byteBuffer,0,byteBuffer,new WriteComplete());


    }
}

//回调
class ReadCompleteHandler implements CompletionHandler<Integer,ByteBuffer>{

    //完成了,就回调这个方法
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        System.out.println("result = " + result);
        attachment.flip();
        byte[] data = new byte[attachment.limit()];
        attachment.get(data);
        System.out.println(new String(data));
        attachment.clear();

    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }
}

class WriteComplete implements CompletionHandler<Integer,ByteBuffer>{

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        System.out.println("do done");
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }
}
