package com.rao.study.nio.channel.file;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelDemo {

    //通过文件获取Channel
    @Test
    public void testChannel() throws Exception{
        RandomAccessFile file = new RandomAccessFile("1.txt","rw");
        FileChannel channel = file.getChannel();
        //再通过channel将文件内容存储到buffer中进行传输
        ByteBuffer byteBuffer = ByteBuffer.allocate(48);

        //通过channel将数据写到buffer中,就是buffer的写模式
        channel.read(byteBuffer);//所以此时buffer的position为最大值

        //此时需要通过flip()切换为读模式
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);

        System.out.println(new String(bytes,0,bytes.length));

        channel.close();
        file.close();
    }


    //通过文件流获取channel
    @Test
    public void testInputChannel1() throws Exception{
        FileInputStream fileInputStream = new FileInputStream("1.txt");
        FileChannel fileChannel = fileInputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(48);

        while(fileChannel.read(byteBuffer)>0){
            //切换为读模式
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            System.out.println(new String(bytes,0,bytes.length));
            //读取完后,进行clear,表示可以再重新写
            byteBuffer.clear();
        }

        //最后一定要关闭
        fileChannel.close();
        fileInputStream.close();
    }

    @Test
    public void testOutputChannel() throws Exception{
        FileOutputStream outputStream = new FileOutputStream("2.txt");
        //通过OutputStream流获取Channel
        FileChannel fileChannel = outputStream.getChannel();

        //将数据写到buffer中
        ByteBuffer byteBuffer = ByteBuffer.allocate(48);
        byteBuffer.put("afasdfasdfasdfsadf".getBytes());

        //需要切换模式,因为上面的byteBuffer.put数据后,position已经变成了最大值,而使用channel.write表示
        byteBuffer.flip();

        //通过channel将buffer中的数据传输到文件中
        fileChannel.write(byteBuffer);

        fileChannel.close();
        outputStream.close();
    }

    @Test
    public void testInputAndOutputChannel()throws Exception{
        FileInputStream inputStream = new FileInputStream("1.txt");
        FileOutputStream outputStream = new FileOutputStream("2.txt");

        //获取channel
        FileChannel inChannel = inputStream.getChannel();
        FileChannel outChannel = outputStream.getChannel();

        //读取数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(40);
        while (inChannel.read(byteBuffer)>0){//通过channel将文件中的数据写入到byteBuffer中,即写模式
            //写完数据后,通过flip()切换为读模式
            byteBuffer.flip();//会将position设置为0,limit设置为最大可读的位置
            //读取数据
            //读完数据后,将数据通过outChannel写入到输出流中
            outChannel.write(byteBuffer);//channel读取byteBuffer中的数据(即读模式,所以需要上面调用flip()函数),将读取的数据写入到输出流中

            //每次写完后,需要将byteBuffer.clear进行清除,让byteBuffer可以重新存数据
            byteBuffer.clear();
        }
        inChannel.close();
        outChannel.close();
        inputStream.close();
        outputStream.close();
    }

    @Test
    public void testTransfer() throws Exception{
        //通道之间的数据传输
        FileInputStream inputStream = new FileInputStream("1.txt");
        FileOutputStream outputStream = new FileOutputStream("3.txt");
        FileChannel inChannel = inputStream.getChannel();
        FileChannel outChannel = outputStream.getChannel();

        //表示将inChannel中的数据传输到outChannel中
        outChannel.transferFrom(inChannel,0,inChannel.size());
    }

    //聚合
    @Test
    public void testGather() throws Exception{
        FileOutputStream outputStream = new FileOutputStream("3.txt");
        FileChannel fileChannel = outputStream.getChannel();
        String str = "hello world";
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());

        String str1 = "this is kkk";
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(str1.length());
        byteBuffer1.put(str1.getBytes());

        ByteBuffer[] byteBuffers = new ByteBuffer[]{byteBuffer,byteBuffer1};
        //聚合,将多个ByteBuffer合并写入到文件中
        //切换bytebuffer的读模式
        byteBuffer.flip();
        byteBuffer1.flip();
        fileChannel.write(byteBuffers);//顺序是固定的
        fileChannel.close();
        outputStream.close();
    }

    //分散
    @Test
    public void testScatter() throws Exception{
        //将一个数据分散到多个Buffer中,第一个不够,则用第二个buffer继续存储
        FileInputStream inputStream = new FileInputStream("4.txt");

        ByteBuffer byteBuffer = ByteBuffer.allocate(7);
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(7);

        FileChannel fileChannel = inputStream.getChannel();
        ByteBuffer[] byteBuffers = new ByteBuffer[]{byteBuffer,byteBuffer1};
        fileChannel.read(byteBuffers);

        //切换模式
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);

        byteBuffer1.flip();
        byte[] bytes1 = new byte[byteBuffer1.remaining()];
        byteBuffer1.get(bytes1);

        System.out.println(new String(bytes,0,bytes.length));
        System.out.println(new String(bytes1,0,bytes1.length));
    }

}
