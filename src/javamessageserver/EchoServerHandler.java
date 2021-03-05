/*
 * License Sinelnikov Oleg
 */
package javamessageserver;

import exchdata.getParamsfromFile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.simple.parser.ParseException;
/*
Обработка запросов на ЭХО сервер
servers - vpn ip Серверов и предоставляемые внешние ip
region  - Регион предоставления внешнего ip (США, Россия...)
network - Сеть. В одном регионе может буть несколько разных сетей:
           31.173.x.x; 87.174.x.x ...
proxy   - proxy сервер для конкретного региона и сети
file    - crt, key, conf файлы
*/
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    public void channelRead(ChannelHandlerContext ctx, Object msg) 
            throws IOException, FileNotFoundException, ParseException, Exception {
        getParamsfromFile ipp;
        ByteBuf in = (ByteBuf) msg;
        int ind = in.toString(CharsetUtil.UTF_8).indexOf(":");
        String requ = in.toString(CharsetUtil.UTF_8).substring(0, 
                in.toString(CharsetUtil.UTF_8).indexOf(":"));
        String id = in.toString(CharsetUtil.UTF_8).substring(
                in.toString(CharsetUtil.UTF_8).indexOf(":")+1,
                   in.toString(CharsetUtil.UTF_8).length());
        String ffil = requ.substring(0,3);
        String fid= requ.substring(4,requ.length());
        
        System.out.println(
            "Server received: " + in.toString(CharsetUtil.UTF_8));
        switch (requ){
            case("servers"):
                in.clear();
                ipp = new getParamsfromFile();
                ByteBufUtil.writeUtf8(in, (CharSequence) ipp.getParams());
                break;
            case("region"):
                in.clear();
                ipp = new getParamsfromFile();
                ByteBufUtil.writeUtf8(in, ipp.getRegion(id));
                break; 
            case("network"):
                in.clear();
                ipp = new getParamsfromFile();
                ByteBufUtil.writeUtf8(in, ipp.getNetwork(id));
                break;    
            case("proxy"):
                in.clear();
                ipp = new getParamsfromFile();
                String sproxy = ipp.getProxy(id);
                ByteBufUtil.writeUtf8(in, sproxy);
                break;    
            //Загрузка файлов    
            case("file"):
                in.clear();
                ipp = new getParamsfromFile();
               // ByteBufUtil.writeUtf8(in, ipp.getFile());
                //in.writeBytes(ipp.getFile(id));
                in.writeBytes(ipp.getFileDB(fid,id));
                break;      
            default:
                in.clear();
                ipp = new getParamsfromFile();
                //in.writeBytes(ipp.getFile(fid));
                in.writeBytes(ipp.getFileDB(fid,id));
                break;
                    
        }
        ctx.write(in);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
            .addListener(ChannelFutureListener.CLOSE);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
