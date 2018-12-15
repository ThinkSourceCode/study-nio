package com.rao.study.nio.channel.udp;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class DatagramChannelDemo {

    /**
     * 阻塞模式
     * @throws Exception
     */
    @Test
    public void testServer() throws Exception{
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.socket().bind(new InetSocketAddress(9999));

        //接收数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        datagramChannel.receive(byteBuffer);//receive是阻塞式的,没接收到数据就一直阻塞

        //切换模式
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        System.out.println(new String(bytes,0,bytes.length));

        datagramChannel.close();
    }

    /**
     * 阻塞模式
     * @throws Exception
     */
    @Test
    public void testClient()throws Exception{
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.connect(new InetSocketAddress("localhost",9999));

        String str = "11111111111111111111111111";
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());

        byteBuffer.flip();
        datagramChannel.write(byteBuffer);
        datagramChannel.close();
    }

    /**
     * 非阻塞模式
     * @throws Exception
     */
    @Test
    public void testServer1() throws Exception{
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);//设置为非阻塞模式
        datagramChannel.socket().bind(new InetSocketAddress(9999));

        //接收数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);

        boolean flag = true;
        while(flag){
            SocketAddress socketAddress = datagramChannel.receive(byteBuffer);//非阻塞模式下recevive会立即返回

            if(socketAddress!=null){
                //切换模式
                byteBuffer.flip();
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                System.out.println(new String(bytes,0,bytes.length));
            }

        }

        datagramChannel.close();
    }

    /**
     * 非阻塞模式
     * @throws Exception
     */
    @Test
    public void testClient1()throws Exception{
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);//设置为非阻塞模式
        datagramChannel.connect(new InetSocketAddress("localhost",9999));//因为是UDP模式,所以不管是阻塞还是非阻塞,这里都立即返回

        String str = "11111111111111111111111111";
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());

        byteBuffer.flip();
        datagramChannel.write(byteBuffer);
        datagramChannel.close();
    }
}
