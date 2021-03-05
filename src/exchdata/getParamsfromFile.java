/*
 * License Sinelnikov Oleg
 */
package exchdata;

import database.IdataFromDb;
import database.GetDataFromDb;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



/**
 *
 * @author oleg
 */
public class getParamsfromFile {
    private static final String filePath = "resouces/servers.json";
    // Корневой каталог app. Linux!
    private final static  String fconfPath = 
            System.getProperty("user.dir");
     

    
    /*
    Список регионов
    */
    public String getRegion(String key) throws FileNotFoundException, IOException, ParseException{
       IdataFromDb db = new GetDataFromDb();
       String reg = "";
       List lregion= db.getRegion(key);
       for (int i=0;i<lregion.size();i++){
           reg = reg+lregion.get(i)+";";
       }
       
       return reg; 
    }
    
    /*
    Список сетей
    */
    public String getNetwork(String idregion) throws FileNotFoundException, IOException, ParseException{
       IdataFromDb db = new GetDataFromDb();
       String reg = "";
       List lnetwork = db.getNetwork(idregion);
       for (int i=0;i<lnetwork.size();i++){
           reg = reg+lnetwork.get(i)+";";
       }
       return reg; 
    }
       
    /*
    Список Proxy
    */
       public String getProxy(String idnetwork) throws FileNotFoundException, IOException, ParseException{
       IdataFromDb db = new GetDataFromDb();
       String reg = "";
       List lproxy = db.getProxy(idnetwork);
       for (int i=0;i<lproxy.size();i++){
           reg = reg+lproxy.get(i).toString().
                   substring(2,lproxy.get(i).toString().length())+";";
       }
       
       return reg; 
    }
    /*
    Внутренние и внешние ip из файла   servers.json
    */
    public String getParams() throws FileNotFoundException, IOException, ParseException{
        FileReader reader = new FileReader(filePath);
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(reader);
        String res = (String) obj.get("type")+":";
        List<String>  ipAddrIn = (List ) (JSONArray ) 
                obj.get("ipAddrIn");
        List<String> ipAddrOut = (List) (JSONArray) 
                obj.get("ipAddrOut");
        for (String inIP :ipAddrIn) {
            res = res+inIP+",";
        }
       return res+";"; 
    }
    
 
    /*
    Читаем файл для передачи
    */
    public ByteBuf getFileDB(String id,String key) throws FileNotFoundException, IOException, ParseException{
       IdataFromDb db = new GetDataFromDb();
       return db.getFileFromDb(id,key);
    }
}
