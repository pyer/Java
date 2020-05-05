package ab;

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
        FtpClient fclient = new FtpClient(host, port, username, password);
        try {
            fclient.connect();
            fclient.changeDirectory(path);
            fclient.listFiles(".");
            fclient.uploadFile("pom.xml", targetName());
            fclient.listFiles(".");
            fclient.disconnect();
        } catch (Exception e) {
            logger.error("", e);
        }

        logger.info("Test Apache SFTP");
        SftpClient sclient = new SftpClient(host, port, username, password);
        try {
            sclient.connect();
            sclient.changeDirectory(path);
            sclient.listFiles(".");
            sclient.uploadFile("pom.xml", targetName());
            sclient.listFiles(".");
            sclient.disconnect();
        } catch (Exception e) {
            logger.error("", e);
        }

        logger.info("Test Jcraft JSCH");
        JschClient jclient = new JschClient(host, port, username, password);
        jclient.connect();
        try {
            jclient.changeDirectory(path);
            jclient.listFiles(".");
            jclient.uploadFile("pom.xml", targetName());
            jclient.listFiles(".");

        } catch (Exception e) {
            logger.error("", e);
        }
        jclient.disconnect();

        logger.debug("exit");
        System.exit(0);
    }
}


