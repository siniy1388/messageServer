/*
 * License Sinelnikov Oleg
 */
package database;

import io.netty.buffer.ByteBuf;
import java.util.List;

/**
Интерфейс  обработки данных из BD
 */
public interface IdataFromDb {
    public List getRegion(String key);
    public List getNetwork(String region);
    public List getProxy(String network);
    public ByteBuf getFileFromDb(String id,String key);
    
}
