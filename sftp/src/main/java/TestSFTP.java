package ab;

import ab.Client;
import ab.clients.FtpClient;
import ab.clients.JschClient;
import ab.clients.SftpClient;

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
          props.load(new FileInputStream("test.properties"));
        } catch(Exception e) {
          logger.error("", e);
          System.exit(1);
        }
        String host     = props.getProperty("host");
        String username = props.getProperty("username");
        String password = props.getProperty("password");
        String path     = props.getProperty("path");
        int port=22;

        logger.info("Test Apache FTP");
        // for compatibility
        testSFTP(new FtpClient(host, port, username, password), path);

        logger.info("Test Apache SFTP");
        testSFTP(new SftpClient(host, port, username, password), path);

        logger.info("Test Jcraft JSCH");
        testSFTP(new JschClient(host, port, username, password), path);

        System.exit(0);
    }
}

