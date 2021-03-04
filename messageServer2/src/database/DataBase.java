/*
 * License Sinelnikov Oleg
 */
package database;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

/*
Подключение к BD
*/

public class DataBase {

    protected String url ;
    protected String host = "localhost";
    protected String usr = "W0r91ro_ot%%";
    protected String pwd = "$%SextetoS%$_1!";
    protected Connection connection;
    protected String DriverClass;
    protected Exception error;
    
  public Connection getConnection() {
    try {
       if (connection ==null) {
       Activate();
       }
    } catch (Exception ex) {
            System.out.println( "Caught exception: " +
            ex.getClass().getName() + ": " + ex.getMessage() );
      }  
    return connection;
  }

  public Exception getException() {
    return error;
  }
  
  
  private void Activate() throws Exception {
  if (!host.isEmpty()){
    url= "jdbc:mysql://"+host+":3306/vpnproxy?";
    Properties properties=new Properties();
    properties.setProperty("user",usr);
    properties.setProperty("password",pwd);
    properties.setProperty("useUnicode","true");
    properties.setProperty("characterEncoding","UTF-8");
    properties.setProperty("serverTimezone","UTC");
      try {
        connection = 
                DriverManager.getConnection(url, properties);
            System.out.println("Connected!");
      } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            System.out.println( "Caught exception: " +
            ex.getClass().getName() + ": " + ex.getMessage() );
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }  
   } else {
          connection = null;
   }
  }
  
}