/*
 * License Sinelnikov Oleg
 */
package exchdata;

import java.util.List;

/**
 *
 * Класс параметров данных из файла
 */
public class IpProxy {
    public String type;    // Server/Programm
    public List ipAddrIn;  // Ip address in interface
    public List ipAddrOut; // Ip address out interface
    public String region;  // region address server
    public List ipPort;    // Port
    public List user;      // user
    public List passwd;    // Password
    
}
