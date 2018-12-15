package com.rao.study.nio.channel.tcp;

import org.junit.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class SocketChannelDemo {

    /**
     * 阻塞模式下的Server
     * @throws Exception
     */
    @Test
    public void testServerSocketChannel1() throws Exception{
        //创建服务端socketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9999));

        SocketChannel socketChannel =serverSocketChannel.accept();//这个方法默认是阻塞式的,没有客户端连接时,会一直等到,当有客户端连接时,则返回客户端的socketChannel对象

        //读取客户端的数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        while (socketChannel.read(byteBuffer)>0){//这里是将socketChannel中的数据写入到ByteBuffer中,所以此时是写模式
            //切换到读模式
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            System.out.println(new String(bytes,0,bytes.length));
            byteBuffer.clear();
        }
        socketChannel.close();

        serverSocketChannel.close();

    }

    /**
     * 阻塞模式下的客户端
     * @throws Exception
     */
    @Test
    public void testSocketChannel1()throws Exception{
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost",9999));//客户端连接服务端

        //向服务端写数据
        String str = "asfasfasfsadfsadfsadfsadfasdfasfasfasfsadfsadfsadfsadfasdf";
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());//这里是写模式

        //切换模式
        byteBuffer.flip();
        socketChannel.write(byteBuffer);

        socketChannel.close();
    }


    /**
     * 非阻塞试服务端,非阻塞试的模式下,accept,read,write都是异步的
     * @throws Exception
     */
    @Test
    public void testServerSocketChannel2() throws Exception{
        //创建服务端socketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9999));
        serverSocketChannel.configureBlocking(false);//设置为非阻塞

        boolean flag = true;
        while (flag){
            SocketChannel socketChannel =serverSocketChannel.accept();//非阻塞模式下,accept会立即返回,有可能返回null,也有可能返回客户端SocketChannel

            if(socketChannel!=null){//表示有客户端连接
                //读取客户端的数据
                ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                while (socketChannel.read(byteBuffer)>0){//这里是将socketChannel中的数据写入到ByteBuffer中,所以此时是写模式
                    //切换到读模式
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    System.out.println(new String(bytes,0,bytes.length));
                    byteBuffer.clear();
                }
                socketChannel.close();
            }

        }

        serverSocketChannel.close();
    }

    /**
     * 非阻塞试客户端,非阻塞试下,connect,read,write方法都是异步的,立即返回结果,可能为null,也可能有值
     * @throws Exception
     */
    @Test
    public void testSocketChannel2() throws Exception{
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);//设置为非阻塞模式
        socketChannel.connect(new InetSocketAddress("localhost",9999));//客户端连接服务端

        while(!socketChannel.finishConnect()){//是否已建立连接
            System.out.println("客户端未连接成功");
        }
        //向服务端写数据
        String str = "asfasfasfsadfsadfsadfsadfasdfasfasfasfsadfsadfsadfsadfasdf";
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());//这里是写模式

        //切换模式
        byteBuffer.flip();
        socketChannel.write(byteBuffer);

        socketChannel.close();
    }

}
