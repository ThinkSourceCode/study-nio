package com.rao.study.aio.tcp;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;

public class AsychronizeSocketChannelDemo {

    @Test
    public void testServer()throws Exception{
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9999));

        Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture = serverSocketChannel.accept();

        while (!asynchronousSocketChannelFuture.isDone()){}

        AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        Future<Integer> future = asynchronousSocketChannel.read(byteBuffer);

        while (!future.isDone()){

        }

        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        System.out.println(new String(bytes,0,bytes.length));

//        asynchronousSocketChannel.close();
//        serverSocketChannel.close();
    }

    @Test
    public void testClient()throws Exception{
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        Future<Void> future = socketChannel.connect(new InetSocketAddress("localhost", 9999));

        while (!future.isDone()){}
        String str = "dddddddddddddddddddddddd";
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer,byteBuffer,new WriteHandler());
    }
}

class WriteHandler implements CompletionHandler<Integer,ByteBuffer>{

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        System.out.println("输出完成");
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        System.out.println("failed");
    }
}
