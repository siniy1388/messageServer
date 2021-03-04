/*
 * License Sinelnikov Oleg
 */
package database;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import static io.netty.buffer.Unpooled.buffer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author oleg
 */
public class GetDataFromDb implements IdataFromDb {
    private Connection cn;
    
    
    /*
    Получим из БД список регионов
    и отдадим по запросу
    */
    private List getDataFromDB(String query,String key,String key1) throws SQLException{
        if (cn==null){
            cn = new DataBase().getConnection();
        }
        List reg = new ArrayList() ;
        PreparedStatement s ;
        try {
           // cn.setAutoCommit(false);
            s = cn.prepareStatement(query);
            s.setString(1, key);
            if (!"".equals(key1)){
                s.setString(2, key1);
            }
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                String tstr = rs.getString(1)+":"+rs.getString(2);
                 reg.add(tstr);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return reg;
    }

    /*
    Получим из БД файлы
    и отдадим по запросу
    */
    private ByteBuf getDataFromDB1(String query,String key,int id) throws SQLException, IOException{
        if (cn==null){
            cn = new DataBase().getConnection();
        }
        String fname = "";
        ByteBuf req = buffer() ;
        PreparedStatement s ;
        try {
            s = cn.prepareStatement(query);
            s.setString(1, key);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                if (id==1){
                    // Получаеи имя файла сертификата клиента
                 // для записи в файл конфигурации
                    fname = rs.getString(2);
                }
                int i = rs.getBinaryStream(1).available();
                ByteBufOutputStream bos = new ByteBufOutputStream(req);
                byte[] b =  rs.getBytes(1);
                byte[] b1;
                if (id==1){
                    fname = rs.getString(2);
                    b1 = replaceFName(b,fname);
                   // b1 = b;
                }else{
                    b1 = b;
                }
                bos.write(b1);
                
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return req;
    }
    /*
    Запрос по регионам
    */
    public List getRegion(String key) {
      //  String query = "SELECT id,name_reg from region";
        String query = "SELECT distinct r.id,r.name_reg FROM client_fl c, region r , conf_reg_net crn "
                        +" WHERE crn.id_conf= c.id_conf and r.id = crn.id_reg and c.rndm_key = ?"; 
        List ret= null ;
        try {
            ret = getDataFromDB(query,key,"");
        } catch (SQLException ex) {
            Logger.getLogger(GetDataFromDb.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Region OK!");
        return ret;
    }
    /*
    Запрос по сетям
    */
    @Override
    public List getNetwork(String region) {
        String key = region.substring(0, region.indexOf("_"));
        //System.out.println("key " +key);
        String key1 = region.substring(region.indexOf("_")+1);
      //  System.out.println("key1 " +key1);
                
        String query = "SELECT distinct r.id ,r.network FROM `client_fl` c, `network` r , `conf_reg_net` crn"
                        +" WHERE crn.id_conf= c.id_conf and r.id = crn.id_netw "
                        +" and c.`rndm_key` = ? " //'d0d9ef60e8b'
                        +" and r.`id_reg`= ?";// 1
        List ret= null ;
        try {    
            ret = getDataFromDB(query,key1,key);
        } catch (SQLException ex) {
            Logger.getLogger(GetDataFromDb.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Network OK!");
        return ret;
    }
    /*
    Запрос попараметрам подключения
    Собираем строку
    */
    @Override
    public List getProxy(String network) {
        String query = "SELECT IFNULL(concat(`user_proxy`,':',`passwd_proxy`,'@'),' '),"
                      + "concat(`ip_proxy`,':',`port_proxy`)"
                          +"FROM `proxy` WHERE `id_network` =?" ;
        List ret= null ;
        
        try {  
            ret = getDataFromDB(query,network,"");
        } catch (SQLException ex) {
            Logger.getLogger(GetDataFromDb.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Proxy OK!");
        return ret;
    }
    /*
    Запрос по файлам
    */
    public ByteBuf getFileFromDb(String id,String key){
        ByteBuf ret = null;
        String ffile;
        switch (id){
            case("1"):
                ffile = "SELECT cf.conf_txt,cl.name FROM client_fl cl ,conf_fl cf " +
                     "WHERE rndm_key = ? and cf.id = cl.id_conf";
                break;
            case("2"):
                ffile = "SELECT cf.ca_txt FROM client_fl cl ,conf_fl cf " +
                     "WHERE rndm_key = ? and cf.id = cl.id_conf";
                break;
            case("3"):
                ffile = "SELECT cert_txt FROM client_fl " +
                    "WHERE rndm_key = ?";
                break;
            case("4"):
                ffile = "SELECT key_txt FROM client_fl " +
                    "WHERE rndm_key = ?";
                break;
            default:
                ffile = "SELECT cf.conf_txt FROM client_fl cl ,conf_fl cf " +
                     "WHERE rndm_key = ? and cf.id = cl.id_conf";
                break;    
        }
        try {
            ret = getDataFromDB1(ffile,key,Integer.parseInt(id));
        } catch (SQLException ex) {
            Logger.getLogger(GetDataFromDb.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetDataFromDb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    /*
    Подставляем номера файлов сертификатов, ключей для
    конкретного ключа доступа
    */    
    private byte[] replaceFName(byte[] fl,String fname){
        String tstr = new String(fl).replaceAll("clientXX", fname);
        char[] buffer = tstr.toCharArray();
        byte[] b = new byte[buffer.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) buffer[i];
        }
        return b;
    }
}
