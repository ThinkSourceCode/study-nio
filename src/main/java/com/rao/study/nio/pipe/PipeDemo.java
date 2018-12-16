package com.rao.study.nio.pipe;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class PipeDemo {

    @Test
    public void testPipe()throws Exception{
        Pipe pipe = Pipe.open();
        Pipe.SinkChannel sinkChannel = pipe.sink();//输出管道

        String str = "asdfsadfsadfsdfas";
        ByteBuffer byteBuffer = ByteBuffer.allocate(str.length());
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();
        sinkChannel.write(byteBuffer);
        sinkChannel.close();




        Pipe.SourceChannel sourceChannel = pipe.source();//读取管道
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(100);
        int len = -1;

        while ((len=sourceChannel.read(byteBuffer1))>0){
            byteBuffer1.flip();
            System.out.println(new String(byteBuffer1.array(),0,len));
            byteBuffer1.clear();
        }


        sourceChannel.close();
    }
}
