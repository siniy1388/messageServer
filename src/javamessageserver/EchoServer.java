/*
 * License Sinelnikov Oleg
 */
package javamessageserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import java.net.InetSocketAddress;
/*
Обработка входящего
*/
public class EchoServer {

    private final int port;
    int rcvBuf;
    int sndBuf;

    public EchoServer(int port) {
        this.port = port;
        
    }

    public void start() throws Exception {
        rcvBuf = 64;
        sndBuf = 64;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                    @Override
                                    public void initChannel(SocketChannel ch) throws Exception {
                                        System.out.println("New client connected: " + 
                                                ch.localAddress());

                                        ch.pipeline().addLast(new EchoServerHandler(),
                                        new StringEncoder(CharsetUtil.UTF_8),
                                        new LineBasedFrameDecoder(8192),
                                        new StringDecoder(CharsetUtil.UTF_8));
                                    }
                                });
            b.childOption(ChannelOption.SO_RCVBUF, rcvBuf * 1024);
            b.childOption(ChannelOption.SO_SNDBUF, sndBuf * 1024);
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main (String [] args) throws Exception {
        new EchoServer(15444).start();
    }
}