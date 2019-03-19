/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ecg;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author bon
 */
public class Variable {
    static String FILEPATH;
    static String ECGINFOPATH;
    static String BACKUPPATH;
    static int BACKUPDAY;
    static String HOST;
    static String HNDIRECTORY;
    static String SAVEDIRECTORY;
    static String ECGFILE;
    static String SMIPORT;
    static String FONT;
    static int H1;
    static int H2;
    public void init()
    {
        // Read properties file.
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("ECG.properties"));
        } catch (IOException e) {
            System.out.println(e);
        }
      
        FILEPATH = properties.getProperty("filepath");
        ECGINFOPATH = properties.getProperty("ecginfo");
        BACKUPPATH = properties.getProperty("backup");
        HOST = properties.getProperty("host");
        HNDIRECTORY = properties.getProperty("hndirectory");
        SAVEDIRECTORY = properties.getProperty("savedirectory");
        ECGFILE = properties.getProperty("ecgfile");
        SMIPORT = properties.getProperty("smiport");
        FONT = properties.getProperty("font");
        H1 = Integer.parseInt(properties.getProperty("h1"));
        H2 = Integer.parseInt(properties.getProperty("h2"));
        BACKUPDAY = Integer.parseInt(properties.getProperty("backupday"));
    }
    public void initbyURL(String urlstring)
    {
        // Read properties file.
        Properties properties = new Properties();
        try { 
            //URL url = new URL("http://localhost/public_html/ECG/ECG.properties");
            String username = "host1";
            String password = "host/01";
            String userPassword = username + ":" + password;
            String encoding = new String(Base64.decodeBase64(userPassword));
            URL url = new URL(urlstring);
            URLConnection uc = url.openConnection();
            uc.setRequestProperty ("Authorization", "Basic " + encoding);
            properties.load(url.openStream());
        } catch (IOException e) {
            System.out.println(e);
        }
      
        FILEPATH = properties.getProperty("filepath");
        ECGINFOPATH = properties.getProperty("ecginfo");
        BACKUPPATH = properties.getProperty("backup");
        HOST = properties.getProperty("host");
        HNDIRECTORY = properties.getProperty("hndirectory");
        SAVEDIRECTORY = properties.getProperty("savedirectory");
        ECGFILE = properties.getProperty("ecgfile");
        SMIPORT = properties.getProperty("smiport");
        FONT = properties.getProperty("font");
        H1 = Integer.parseInt(properties.getProperty("h1"));
        H2 = Integer.parseInt(properties.getProperty("h2"));
        BACKUPDAY = Integer.parseInt(properties.getProperty("backupday"));
    }
    
    public static String getFilepath()
    {
        return FILEPATH;
    }
    public static String getECGinfopath()
    {
        return ECGINFOPATH;
    }
    public static String getBackuppath()
    {
        return BACKUPPATH;
    }
    public static String getHost()
    {
        return HOST;
    }
    public static String getHNdirectory()
    {
        return HNDIRECTORY;
    }
    public static String getSavedirectory()
    {
        return SAVEDIRECTORY;
    }
    public static String getECGfile()
    {
        return ECGFILE;
    }
    public static String getSMIport()
    {
        return SMIPORT;
    }
    public static String getFont()
    {
        return FONT;
    }
    public static int getHeader1size()
    {
        return H1;
    }
    public static int getHeader2size()
    {
        return H2;
    }
    public static int getBackupday()
    {
        return BACKUPDAY;
    }
}
