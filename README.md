nio的三个关键部分
buffer      用来保存数据
channel     用来运输buffer,一个channel里有很多的buffer块
selector    用来选择不同的channel,在同一个线程中可以选择不同的channel,selector调用select方法会一直阻塞,当channel中有数据时selector会让线程去执行
            通过一个Selector可以管理多个Channel,需要将channel注册到selector中,通过selector可以监控到哪个Channel准备好了
            
            
FileChannel无法使用Selector,因为FileChannel无法切换为非阻塞试