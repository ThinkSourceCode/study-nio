package com.rao.study.nio.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;

public class ByteBufferDemo {

    @Test
    public void testPositionWithPutGet(){
        String str = "this is a buff";

        //初始化position=0,limit=14,capacity=14  //这里创建的是HeapByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());

        //写入数据后,position=14,limit=14,capacity=14
        byteBuffer.put(str.getBytes());//position(position() + length);这里会重置position位置

        byte[] b = new byte[str.length()];

        // flip切换读模式,必须要调用flip切换读模式,将position设置为0
        // 因为在调用get时,会调用remaining()判断limit-position,所以在写模式下position有可能等于limit,如果继续读的话,就会抛出异常,所以需要调用flip()重置position
        byteBuffer.flip();
        //读取数据,将读取的数据保存到b数组中
        byteBuffer.get(b);//remaining() limit-position
        System.out.println(new String(b,0,b.length));
    }

    @Test
    public void testPut(){
        String str = "this is a buff";//length=14

        //初始化position=0,limit=16,capacity=16  //这里创建的是HeapByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);

        //写入数据后,position=14,limit=16,capacity=16
        byteBuffer.put(str.getBytes());//position(position() + length);这里会重置position位置

        byte[] b = new byte[str.length()];//14

        // flip切换读模式,必须要调用flip切换读模式,将position设置为0
        // 因为在调用get时,会调用remaining()判断limit-position,所以在写模式下position有可能等于limit,如果继续读的话,就会抛出异常,所以需要调用flip()重置position
        byteBuffer.flip();//重置position变为0,同时将limit设置为之前的position=14
        //读取数据,将读取的数据保存到b数组中
        byteBuffer.get(b);//remaining() limit-position,读完数据后,position会变成position+length,所以此时position=14,而limit
        System.out.println(new String(b,0,b.length));

        //因为上面position=14了,而limit=14,所以此时可写入的大小为limit-position=0,此时再写入数据的话,就会报错,所以即使limit=14,capacity=16,还有两个没有写入也不能再写入数据了
//        byteBuffer.put("1".getBytes());


    }
}
