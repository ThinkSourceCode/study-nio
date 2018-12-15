Buffer
用来缓存数据的载体

1.非直接缓存和直接缓存
allocate 表示创建的是一个非直接缓存,在堆内存中使用一个数组来保存数据,数据的操作通过copy这个数组中的数据实现   HeapByteBuffer
allocateDirect 表示创建一个直接缓存,数据直接存储在jvm分配的内存中,数据操作不需要进行copy   DirectByteBuffer

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

3.flip() 表示切换模式,比如从读模式切换到写模式,从写模式切换到读模式,其实就是在改变position和limit的位置

4.reset()重置位置,表示由当前position回到上一个被mark标记的position位置,所以reset方法需要结合mark()方法一起使用
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
        System.arraycopy(src, offset, hb, ix(position()), length);//将数据copy到堆内存中的数组中
        position(position() + length);
        return this;
    }
put方法解析,会检测可写区域,如果写入的src数组的大小大于目前byteBuffer则无法写入抛出异常,所以如果position=limit后想再次写入数据
,则需要调用flip切换可写模式,即将position和limit重置,这么写入,会将原来的数据覆盖掉
 
 
 public final Buffer flip() {
         limit = position;
         position = 0;
         mark = -1;
         return this;
     }
     
从flip方法可以看到,当写入完数据,调用flip() 切换到读模式时,limit被设置为了position的大小,所以当读取完0~limit个数据后,再次写入是无法写入数据的



//这个方法表示将未读的数据拷贝到buffer的起始位置,准备下一个数据的写入,不会覆盖未读的数据,因为position已经变成remaining()的大小了
public ByteBuffer compact() {

        //先将未读的数据limit-position,拷贝到buffer的起始位置
        System.arraycopy(hb, ix(position()), hb, ix(0), remaining());
        position(remaining());//再将position设置为position为limit-position,表示准备写入的位置
        limit(capacity());//再将limit设置为capacity
        discardMark();//重置mark=-1
        return this;

    }
    

重置reset()方法一般都结合mark()方法使用,mark记录上一个position位置,所以reset是重置回到上一个position位置
public final Buffer reset() {
        int m = mark;
        if (m < 0)
            throw new InvalidMarkException();
        position = m;
        return this;
    }
    
   
   
   
   
   
   
   
   
   
   
   -------------------------------------------------------------------------------------------------------------------------------- 
    
//直接缓存,使用直接缓存的话,减少了数据之间的拷贝
  DirectByteBuffer(int cap) {                   // package-private

        super(-1, 0, cap, cap);
        boolean pa = VM.isDirectMemoryPageAligned();
        int ps = Bits.pageSize();
        long size = Math.max(1L, (long)cap + (pa ? ps : 0));
        Bits.reserveMemory(size, cap);

        long base = 0;
        try {
            base = unsafe.allocateMemory(size);//直接从内存中划分
        } catch (OutOfMemoryError x) {
            Bits.unreserveMemory(size, cap);
            throw x;
        }
        unsafe.setMemory(base, size, (byte) 0);
        if (pa && (base % ps != 0)) {
            // Round up to page boundary
            address = base + ps - (base & (ps - 1));
        } else {
            address = base;
        }
        cleaner = Cleaner.create(this, new Deallocator(base, size, cap));
        att = null;



    }
    
    
    
    public ByteBuffer put(byte x) {
    
            unsafe.putByte(ix(nextPutIndex()), ((x)));//直接将数据保存到内存中去
            return this;
    
    
    
        }