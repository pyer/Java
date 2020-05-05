package ab;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.io.OutputStream;
import java.lang.InterruptedException;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SftpClient {
    
    private final static Logger logger = LoggerFactory.getLogger(SftpClient.class);
    
    private String server;
    private int port;
    private String login;
    private String password;
    
    private FTPSClient ftpClient = null;

    private static int DATA_TIMEOUT = 9000;
    private static int SOCKET_TIMEOUT = 9000;

    private int retryConnectionTime = 100;
    private int retryConnectionNumber = 3;


    public SftpClient(String server, int port, String login, String password) {
        this.server = server;
        this.port = port;
        this.login = login;
        this.password = password;
    }

    /**
     * Connects to the server
     */
    public void connect() throws NoSuchAlgorithmException, SocketException, IOException {
        logger.debug("Initializing Apache SFTP client");
        ftpClient = new FTPSClient();

        ftpClient.setConnectTimeout(SOCKET_TIMEOUT);
        ftpClient.setDefaultTimeout(SOCKET_TIMEOUT);

        //ftpClient.connect(server, port);
        ftpClient.connect(server);
        ftpClient.login(login, password);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setSoTimeout(SOCKET_TIMEOUT);
        ftpClient.setDataTimeout(DATA_TIMEOUT);

        // int reply = getReplyCode();
        logger.info(ftpClient.getReplyString());
    }


    /**
     * Change the current directory on the remote server
     * @param path String path on the remote server
     * @throws IOException if connection and channel are not available or if an error occurs during download.
     */
//    public void changeDirectory(String path) throws IOException, FTPConnectionClosedException {
    public void changeDirectory(String path) throws IOException {
        logger.debug("Change directory to " + path);
        boolean ret = ftpClient.changeWorkingDirectory(path);
        if (!ret) {
          throw new IOException("path " + path + " not found");
        }
    }


    /**
     * List files from the sftp server
     * @param path String path on the remote server
     * @throws IOException if connection and channel are not available or if an error occurs during download.
     */
    public ArrayList<String> listFiles(String path) throws IOException {
        ArrayList<String> fileNames = new ArrayList<String>();
        logger.debug("List files:");
        FTPFile[] filesList = ftpClient.listFiles(path);
        for (FTPFile ftpFile : filesList) {
            fileNames.add(ftpFile.getRawListing());
            logger.debug(ftpFile.getRawListing());
        }
        return fileNames;
    }


    /**
     * Uploads a file to the sftp server
     * @param sourceFile String path to sourceFile
     * @param destinationFile String path on the remote server
     * @throws IOException if connection and channel are not available or if an error occurs during upload.
     */
    public void uploadFile(String sourceFile, String destinationFile) throws IOException, InterruptedException {
        logger.debug("Uploading file to server");
        FileInputStream fis = new FileInputStream(new File(sourceFile));
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        boolean ret = false;
        int i = 0;
        while (ret==false && i<retryConnectionNumber) {
          ret = ftpClient.storeFile(destinationFile, fis);
          //logger.info(ftpClient.getReplyString());
          Thread.sleep(retryConnectionTime);
          i++;
        }
        if (ret) {
          logger.info("Upload successful");
        } else {
          throw new IOException("Upload failed");
        }
    }


    /**
     * Retrieves a file from the sftp server
     * @param destinationFile String path to the remote file on the server
     * @param sourceFile String path on the local fileSystem
     * @throws IOException if connection and channel are not available or if an error occurs during download.
     */
    public void retrieveFile(String sourceFile, String destinationFile) throws IOException {
        logger.debug("Downloading file to server");
        //ftpClient.get(sourceFile, destinationFile);
        //logger.info("Download successfull.");
    }


    public void disconnect() throws IOException {
        logger.debug("Disconnecting");
        if (ftpClient.isConnected()) {
            ftpClient.logout();
        }
    }

}

