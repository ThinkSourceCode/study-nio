package com.rao.study.nio.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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

    @Test
    public void testClear(){
        String str = "this is a buff";//length=14

        //初始化position=0,limit=16,capacity=16  //这里创建的是HeapByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);

        //写入数据后,position=14,limit=16,capacity=16
        byteBuffer.put(str.getBytes());//position(position() + length);这里会重置position位置

        byteBuffer.clear();//position=0,limit=16,capacity=16,clear只是将position设置为0,limit设置为capacity,并没有清除数据
//        byteBuffer.flip();//position=0,limit=14
        //读取数据
//        byte[] b = new byte[1024];//这里的byte的大小必须是<= byteBuffer.remaining()可读数据的大小,否则就会抛出BufferUnderflowException
        byte[] b = new byte[byteBuffer.remaining()];
        byteBuffer.get(b);//position=16,limit=16
        System.out.println(new String(b,0,b.length));

    }


    @Test
    public void testClearAndPut(){
        String str = "this is a buff";//length=14

        //初始化position=0,limit=16,capacity=16  //这里创建的是HeapByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);

        //写入数据后,position=14,limit=16,capacity=16
        byteBuffer.put(str.getBytes());//position(position() + length);这里会重置position位置

        byteBuffer.clear();//position=0,limit=16,capacity=16,clear只是将position设置为0,limit设置为capacity,并没有清除数据
//        byteBuffer.flip();//position=0,limit=14
        //读取数据
//        byte[] b = new byte[1024];//这里的byte的大小必须是<= byteBuffer.remaining()可读数据的大小,否则就会抛出BufferUnderflowException
        byte[] b = new byte[byteBuffer.remaining()];
        byteBuffer.get(b);//position=16,limit=16
        System.out.println(new String(b,0,b.length));

        byteBuffer.clear();//清除完后,position=0,limit=16,capacity=16

        //再添加数据
//        String str1 = "this is second buffer";//这个会抛出异常,越界
        String str1 = "this is second";//length=14
        byteBuffer.put(str1.getBytes());//clear不会清空byteBuffer中的数据,但是clear后再重新put数据,则会将原有的数据给覆盖掉

        System.out.println(new String(byteBuffer.array(),0,byteBuffer.array().length));

    }

    @Test
    public void testCompact(){
        String str = "this is a buff";//length=14
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());//这里将str的所有字节都写入到ByteBuffer中

        //切换读模式
        byteBuffer.flip();

        //设置只读10个字节
        byte[] b = new byte[10];
        byteBuffer.get(b);//还剩4个字节未读,remaining()

        System.out.println("已读数据:"+new String(b,0,b.length));

        //通过compact将未读的数据拷贝到bytebuffer的起始位置,并将position设置为未读数据的大小,表示可以准备再次写入数据了,但是不会覆盖未读的数据
        byteBuffer.compact();//position=remaining()=limit-position=14-10=4,limit=capacity=14

        //写入数据
        String str1 = "is compact";
        //因为前面还有4个字节未读取,而byteBuffer的大小为14,所以只能再次写入10个字节到byteBuffer中
        byteBuffer.put(str1.getBytes());

        //切换读模式
        byteBuffer.flip();

        byte[] bb = new byte[byteBuffer.remaining()];
        byteBuffer.get(bb);
        System.out.println(new String(bb,0,bb.length));
    }


    @Test
    public void testReset(){
        String str = "this is a buff";
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());

        byteBuffer.flip();
        byte[] b = new byte[5];//先读取5个,position=5
        byteBuffer.get(b);
        System.out.println(new String(b,0,b.length));
        //利用mark标记第一次读取的position=5的位置
        byteBuffer.mark();

        //再次读取
        byte[] bb = new byte[6];//再读取6个,则position=11
        byteBuffer.get(bb);
        System.out.println(new String(bb,0,bb.length));

        //调用reset将position重置到上一个position=5的位置
        byteBuffer.reset();//重置并不会清除数据

        System.out.println(new String(byteBuffer.array(),0,byteBuffer.array().length));

        //重新写入数据
        byteBuffer.put("123".getBytes());//从position=5的位置开始写入3个数据,那么position=8,byteBuffer中的数据第5个位置到第8个位置的数据将会被替换

        System.out.println(new String(byteBuffer.array(),0,byteBuffer.array().length));

    }

    @Test
    public void testRewind(){//Rewind()表示重读,将position设置为0
        String str = "this is a buff";
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());

        byteBuffer.flip();
        byte[] b = new byte[byteBuffer.remaining()];
        byteBuffer.get(b);//position=14,limit=14,capacity=14
        System.out.println(new String(b,0,b.length));

        //使用rewind()方法设置position=0,limit不变
        byteBuffer.rewind();//表示可以重新读取数据

        byte[] bb = new byte[byteBuffer.remaining()];
        byteBuffer.get(bb);
        System.out.println(new String(bb,0,bb.length));
    }

    //直接缓存

    @Test
    public void directAlloc(){
        String str = "this is a buff";
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(str.length());
        byteBuffer.put(str.getBytes());//查看DirectByteBuffer

        //切换模式
        byteBuffer.flip();

        //进行读取
        byte[] bb = new byte[str.length()];
        byteBuffer.get(bb);//直接从内存中获取
        System.out.println(new String(bb,0,bb.length));
    }



}
