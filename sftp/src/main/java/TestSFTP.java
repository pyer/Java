package ab;

import ab.Client;
import ab.clients.apache.AftpClient;
import ab.clients.apache.FtpsClient;
import ab.clients.jcraft.JschClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSFTP
{

    private final static Logger logger = LoggerFactory.getLogger(TestSFTP.class);

    private static String targetName() {
      DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmmss");
      return "09999999_" + dtf.print(LocalDateTime.now());
    }

    private static void testSFTP(Client client, String path) {
        client.connect();
        client.changeDirectory(path);
        client.listFiles(".");
        client.uploadFile("pom.xml", targetName());
        client.listFiles(".");
        client.disconnect();
    }

    public static void main(String[] args) {

        logger.info("Read properties");
        Properties props = new Properties();
        try {
          props.load(new FileInputStream("sftp.properties"));
        } catch(Exception e) {
          logger.error("", e);
          System.exit(1);
        }
        String host     = props.getProperty("host");
        String username = props.getProperty("username");
        String password = props.getProperty("password");
        String path     = props.getProperty("path");
        String protocol = props.getProperty("protocol").toUpperCase();
        int port        = Integer.parseInt(props.getProperty("port"));

        if ("SFTP".equals(protocol)) {
          logger.info("Test Jcraft JSCH");
          testSFTP(new JschClient(host, port, username, password), path);
        } else if ("FTPS".equals(protocol)) {
          logger.info("Test Apache FTPS");
          testSFTP(new FtpsClient(host, port, username, password), path);
        } else if ("FTP".equals(protocol)) {
          logger.info("Test Apache FTP");
          testSFTP(new AftpClient(host, port, username, password), path);
        } else {
          logger.error("Unknown protocol");
        }

        System.exit(0);
    }
}

