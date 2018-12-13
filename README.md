nio的三个关键部分
buffer      用来保存数据
channel     用来运输buffer,一个channel里有很多的buffer块
selector    用来选择不同的channel

buffer中有一个重点

1.非直接缓存和直接缓存
allocate 表示创建的是一个非直接缓存

2.几个位置变量
mark <= position <= limit <= capacity

capacity表示当前buffer的容量大小,在初始化时就设定了
limit 
    写模式下表示当前buffer最大可写入多少数据，初始化为capacity的值
    读模式下表示当前buffer最大可读多少数据，此时limit=position
    
position 
    写模式下表示当前buffer操作的当前位置，当插入一个数据时,position会移动到下一个可写的位置
    读模式下表示从当前buffer的某个位置开始读取的位置,当从写模式切换到读模式时position会被设置为0,当读取一个数据时，position会移动到下一个可读的位置
mark 表示标记之前的position位置,如果mark大于position时,则mark重新从-1开始

3.flip() 将写模式切换为读模式

4.reset()重置位置,表示由当前position回到上一个被mark标记的position位置
5.clear()清除位置,position变化0,limit变为capacity,但是buffer里的数据不会被情况
6.remaining() 返回当前buffer还可以操作的大小,limit-position
7.hasRemaining() 判断当前buffer是否还可以操作
8.discardMark() 重置mark
9.rewind() 表示可以重新读取buffer中的数据
10.put 写入数据
11.get 读取数据


public ByteBuffer get(byte[] dst, int offset, int length) {
        checkBounds(offset, length, dst.length);
        if (length > remaining())
            throw new BufferUnderflowException();
        System.arraycopy(hb, ix(position()), dst, offset, length);
        position(position() + length);
        return this;
    }
解析get方法,为什么在写入数据模式后，切换到读取模式的时候,需要调用flip方法重置position为0
比如：ByteBuffer 的初始化数据时capacity=16,而limit=16,position=0,写入14个字节数据后,capacity=16,limit=16,position=14,
此时还有两个可写limit-position=remaining()=2,但是如果没有调用flip()函数将position重置为0的话，name读取数据的时候,就是从position=14开始读取
此时分两种情况
1.定义的byte数组dst,长度大于bytebuffer可读的大小remaining(),此时就会抛出BufferUnderflowException,
    因为在 System.arraycopy(hb, ix(position()), dst, offset, length);这段代码，就会导致拷贝出错
2.定义的byte数组dst,长度小于等于bytebuffer可读的大小remaining(),此时从position=14开始读取,而limit=16,
    那么因为只写入了14个字节，第15，16个都为空，所以读取出来的数据相当于没有意义
    
综合上面的，就可以明白当在写完数据，准备进入读模式的时候，需要调用flip()函数来重置position=0,这样读的时候就是从0~dst.length,即position=0 到dst.length的长度的数据


   

    
 public ByteBuffer put(byte[] src, int offset, int length) {

        checkBounds(offset, length, src.length);
        if (length > remaining())
            throw new BufferOverflowException();
        System.arraycopy(src, offset, hb, ix(position()), length);
        position(position() + length);
        return this;



    }