Selector 可以实现单个线程中多个连接的使用,即实现多路复用,selector通过轮询的方式查看注册到selector选择器中的所有Channel,并返回
准备就绪的Channel

channel必须注册到Selector中,Selector才知道这个channel,因为Selector时非阻塞式的,所以FileChannel无法注册到Selector中

open() 创建Selector
register(channel,selectionKey) 注册channel到Selector,并指明对哪些事件关心

SelectionKey
    public static final int OP_READ = 1 << 0;//读操作准备就绪
    public static final int OP_WRITE = 1 << 2;//写操作准备就绪
    public static final int OP_CONNECT = 1 << 3;//连接操作准备就绪
    public static final int OP_ACCEPT = 1 << 4;//接受操作准备就绪
    
    多个事件通过|或操作，如OP_READ|OP_WRITE 表示关心读和写操作
    
    attach 附带对象
    attachment 获取附带的对象
    channel 获取选择的channel


select()
通过select方法进行轮询,当一个channel准备就绪时,select返回的就是1,表示有一个channel准备就绪,当有返回2表示有两个channel准备就绪
需要注意的是,如果在第一次调用select返回了一个就绪的channel,但是没对这个channel做任何操作，再次调用select时又有一个channel准备就绪
此时有两个channel准备就绪,但是select返回的值为1

selectedKeys 获取已经准备就绪的channel



