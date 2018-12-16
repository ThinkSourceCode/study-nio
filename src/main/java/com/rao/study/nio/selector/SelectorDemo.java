package com.rao.study.nio.selector;

import com.sun.org.apache.bcel.internal.generic.Select;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SelectorDemo {

    @Test
    public void testSelector()throws Exception{
        //创建服务端
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //使用Selector,必须设置为非阻塞式的
        serverSocketChannel.configureBlocking(false);
        //绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(9998));

        //创建Selector
        Selector selector = Selector.open();

        //将channel注册到Selector中,对多个事件感兴趣,则使用|操作
        //注意最开始时这里必须是OP_ACCEPT事件,只有在accept后才能获取到目标客户端的channel,并对客户端的channel进行操作
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //通过select进行轮询操作
        while (selector.select()>0){//当大于0时表示至少有一个channel已经准备就绪
            //此时通过selectedKeys来获取已选择的准备就绪的channel
            Set<SelectionKey> selectionKeys = selector.selectedKeys();//返回的是哪个事件的key

            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()){
                SelectionKey selectionKey = keyIterator.next();
                if(selectionKey.isAcceptable()){//接受就绪操作
                    System.out.println("isAcceptable");

                    //客户端连接上了,则服务端进行接受客户端,并设置只对客户端的哪些事件感兴趣
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //设置客户端channel为非阻塞式
                    socketChannel.configureBlocking(false);
                    //将客户端channel注册到Selector中
                    socketChannel.register(selector,SelectionKey.OP_READ| SelectionKey.OP_WRITE);

                }else if(selectionKey.isConnectable()){//连接就绪操作
                    System.out.println("isConnectable");
                }else if(selectionKey.isReadable()){//读就绪操作
//                    System.out.println("isReadable");
                    //读操作表示读取客户端输出的数据的准备就绪
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();//通过channel()方法获取选择的Channel

                    ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                    int length = -1;
                    while ((length = socketChannel.read(byteBuffer))>0){
                        //切换模式
                        byteBuffer.flip();
                        System.out.println(new String(byteBuffer.array(),0,length));
                        byteBuffer.clear();//清除
                    }

                }else if(selectionKey.isValid() && selectionKey.isWritable()){//写就绪操作
                    System.out.println("isWritable");

                    //获取选择的Channel
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    //向客户端写数据
                    String str = "aaaaaaaaaaaaaaaaaa";
                    ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
                    byteBuffer.put(str.getBytes());
                    byteBuffer.flip();
                    socketChannel.write(byteBuffer);

                    byteBuffer.clear();

                    socketChannel.close();

                }

                // 当获取一个 SelectionKey 后, 就要将它删除, 表示我们已经对这个 IO 事件进行了处理.
                keyIterator.remove();
            }
        }
    }


    /**
     * 创建一个输出数据的客户端
     * @throws Exception
     */
    @Test
    public void testClient1()throws Exception{
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);//设置非阻塞式,可以不用设置非阻塞式,如果设置为非阻塞式,则需要使用到finishConnect
        socketChannel.connect(new InetSocketAddress("localhost",9998));//连接服务端,那么服务端的Selector就会察觉到连接就绪

        while(!socketChannel.finishConnect()){

        }
        String str = "asfasfasdf";
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());

        //切换byteBuffer模式,为读模式
        byteBuffer.flip();
        socketChannel.write(byteBuffer);//客户端向服务端写数据

        socketChannel.close();
    }

    /**
     * 创建一个读取服务端数据的客户端
     * @throws Exception
     */
    @Test
    public void testClient2() throws Exception{
        SocketChannel socketChannel = SocketChannel.open();
//        socketChannel.configureBlocking(false);//设置非阻塞式,可以不用设置非阻塞式,如果设置为非阻塞式,则需要使用到finishConnect
        socketChannel.connect(new InetSocketAddress("localhost",9998));//连接服务端,那么服务端的Selector就会察觉到连接就绪

//        while(!socketChannel.finishConnect()){
//
//        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        while((socketChannel.read(byteBuffer))>0){
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            System.out.println(new String(bytes,0,bytes.length));
            byteBuffer.clear();
        }

        socketChannel.close();
    }

}
