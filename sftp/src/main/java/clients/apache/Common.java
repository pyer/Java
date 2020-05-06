package ab.clients.apache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.InterruptedException;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Common {
    
    private final static Logger logger = LoggerFactory.getLogger(Common.class);
    
    private static int DATA_TIMEOUT = 9000;
    private static int SOCKET_TIMEOUT = 9000;

    private int retryConnectionTime = 100;
    private int retryConnectionNumber = 3;

    private FTPClient ftpClient = null;

    private String server;
    private int port;
    private String login;
    private String password;
    
    public Common(String server, int port, String login, String password) {
        this.server = server;
        this.port = port;
        this.login = login;
        this.password = password;
    }

    protected void connecting(FTPClient ftp) {
        try {
          ftpClient = ftp;
          ftp.setConnectTimeout(SOCKET_TIMEOUT);
          ftp.setDefaultTimeout(SOCKET_TIMEOUT);
//          ftp.setSoTimeout(SOCKET_TIMEOUT);
          ftp.setDataTimeout(DATA_TIMEOUT);

          ftp.connect(server);
          ftp.login(login, password);
          ftp.enterLocalPassiveMode();
          logger.info(ftp.getReplyString());

//        } catch (NoSuchAlgorithmException e) {
//          logger.error("NoSuchAlgorithm", e);
        } catch (SocketException e) {
          logger.error("Socket", e);
        } catch (IOException e) {
          logger.error("IO", e);
        } catch (Exception e) {
          logger.error("", e);
        }
    }


    /**
     * Change the current directory on the remote server
     * @param path String path on the remote server
     */
    public void changeDirectory(String path) {
        logger.debug("Change directory to " + path);
        try {
          boolean ret = ftpClient.changeWorkingDirectory(path);
          if (!ret) {
            logger.error("path " + path + " not found");
          }
        } catch (IOException e) {
          logger.error("IO", e);
        }
    }


    /**
     * List files from the sftp server
     * @param path String path on the remote server
     */
    public ArrayList<String> listFiles(String path) {
        logger.debug("List files:");
        ArrayList<String> fileNames = new ArrayList<String>();
        try {
            FTPFile[] filesList = ftpClient.listFiles(path);
            for (FTPFile ftpFile : filesList) {
                fileNames.add(ftpFile.getRawListing());
                logger.debug(ftpFile.getRawListing());
            }
        } catch (IOException e) {
          logger.error("IO", e);
        }
        return fileNames;
    }


    /**
     * Uploads a file to the sftp server
     * @param sourceFile String path to sourceFile
     * @param destinationFile String path on the remote server
     */
    public void uploadFile(String sourceFile, String destinationFile) {
        logger.debug("Uploading file to server");
        FileInputStream fis = null;
        try {
          fis = new FileInputStream(new File(sourceFile));
        } catch (FileNotFoundException e) {
          logger.error("", e);
        }
        boolean ret = false;
        int i = 0;
        try {
          ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
          while (ret==false && i<retryConnectionNumber) {
            ret = ftpClient.storeFile(destinationFile, fis);
            Thread.sleep(retryConnectionTime);
            i++;
          }
        } catch (InterruptedException e) {
          logger.error("Upload interrupted");
        } catch (IOException e) {
          logger.error("IO", e);
        }
        if (ret) {
          logger.info("Upload successful");
        } else {
          logger.error("Upload failed");
        }
    }


    /**
     * Retrieves a file from the sftp server
     * @param destinationFile String path to the remote file on the server
     * @param sourceFile String path on the local fileSystem
     */
    public void retrieveFile(String sourceFile, String destinationFile) {
        logger.debug("Retrieving file from server");
        FileOutputStream fos = null;
        try {
          fos = new FileOutputStream(new File(destinationFile));
        } catch (FileNotFoundException e) {
          logger.error("", e);
        }
        boolean ret = false;
        int i = 0;
        try {
          ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
          while (ret==false && i<retryConnectionNumber) {
            ret = ftpClient.retrieveFile(sourceFile, fos);
            Thread.sleep(retryConnectionTime);
            i++;
          }
        } catch (InterruptedException e) {
          logger.error("Retrieve interrupted");
        } catch (IOException e) {
          logger.error("IO", e);
        }
        if (ret) {
          logger.info("Retrieve successful");
        } else {
          logger.error("Retrieve failed");
        }
    }


    public void disconnect() {
        logger.debug("Disconnecting");
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
          }
        } catch (IOException e) {
          logger.error("IO", e);
        }
    }

}

