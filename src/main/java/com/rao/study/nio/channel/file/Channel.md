Channel
用来传输Buffer

分为以下几种Channel
FileChannel         用于读取，写入，映射和操作文件的通道。
DatagramChannel     从UDP中读写数据
SocketChannel       从TCP中读写数据,TCP的客户端
ServerSocketChannel TCP的服务端



static int write(FileDescriptor var0, ByteBuffer var1, long var2, NativeDispatcher var4) throws IOException {
        if (var1 instanceof DirectBuffer) {
            return writeFromNativeBuffer(var0, var1, var2, var4);
        } else {
            int var5 = var1.position();
            int var6 = var1.limit();

            assert var5 <= var6;

            int var7 = var5 <= var6 ? var6 - var5 : 0;
            ByteBuffer var8 = Util.getTemporaryDirectBuffer(var7);

            int var10;
            try {
                var8.put(var1);
                var8.flip();
                var1.position(var5);
                int var9 = writeFromNativeBuffer(var0, var8, var2, var4);
                if (var9 > 0) {
                    var1.position(var5 + var9);
                }

                var10 = var9;
            } finally {
                Util.offerFirstTemporaryDirectBuffer(var8);
            }

            return var10;
        }
    }
    
可以看到channel.write的方法,在使用byteBuffer时,会使用到position的值（此时可以理解为读模式）,所以在bytebuffer.put数据后（写模式），需要调用flip()切换为读模式

transferTo和transferFrom 两个方法用来实现两个通道之间的数据传输,比如可以通过输入流的channel直接将数据传输到输出流的channel中