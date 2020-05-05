package ab;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;


public class JschClient {
    
    private final static Logger logger = LoggerFactory.getLogger(JschClient.class);
    
    private String server;
    private int port;
    private String login;
    private String password;
    
    private JSch jsch = null;
    private Session session = null;
    private Channel channel = null;
    private ChannelSftp connexion = null;
   
    public JschClient(String server, int port, String login, String password) {
        this.server = server;
        this.port = port;
        this.login = login;
        this.password = password;
    }

    /**
     * Connects to the server and does some commands.
     */
    public void connect() {
        try {
            logger.debug("Initializing jsch");
            jsch = new JSch();
            session = jsch.getSession(login, server, port);

            // Java 6 version
            session.setPassword(password.getBytes(Charset.forName("ISO-8859-1")));
            
            // Java 5 version
            // session.setPassword(password.getBytes("ISO-8859-1"));

            logger.debug("Jsch set to StrictHostKeyChecking=no");
            Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            logger.info("Connecting to " + server + ":" + port);
            session.connect();
            logger.info("Connected !");

            // Initializing a channel
            logger.debug("Opening a channel ...");
            channel = session.openChannel("sftp");
            channel.connect();
            connexion = (ChannelSftp) channel;
            logger.debug("Channel sftp opened");

        } catch (JSchException e) {
            logger.error("", e);
        }
    }


    /**
     * Change the current directory on the remote server
     * @param path String path on the remote server
     * @throws SftpException if connection and channel are not available or if an error occurs during download.
     */
    public void changeDirectory(String path) throws SftpException {
        logger.debug("Change directory to " + path);
        connexion.cd(path);
    }

    /**
     * List files from the sftp server
     * @param path String path on the remote server
     * @throws SftpException if connection and channel are not available or if an error occurs during download.
     */
    public ArrayList<String> listFiles(String path) throws SftpException {
        ArrayList<String> fileNames = new ArrayList<String>();
        logger.debug("List files:");
        Vector<LsEntry> filesList = connexion.ls(path);
        if(filesList != null) {
             for(LsEntry entry : filesList) {
                 fileNames.add(entry.getLongname());
		             logger.debug(entry.getLongname());
	           }
        }
        return fileNames;
    }


    /**
     * Uploads a file to the sftp server
     * @param sourceFile String path to sourceFile
     * @param destinationFile String path on the remote server
     * @throws SftpException if connection and channel are not available or if an error occurs during upload.
     */
    public void uploadFile(String sourceFile, String destinationFile) throws SftpException {
        logger.debug("Uploading file to server");
        connexion.put(sourceFile, destinationFile);
        logger.info("Upload successfull.");
    }


    /**
     * Retrieves a file from the sftp server
     * @param destinationFile String path to the remote file on the server
     * @param sourceFile String path on the local fileSystem
     * @throws SftpException if connection and channel are not available or if an error occurs during download.
     */
    public void retrieveFile(String sourceFile, String destinationFile) throws SftpException {
        logger.debug("Downloading file to server");
        connexion.get(sourceFile, destinationFile);
        logger.info("Download successfull.");
    }


    public void disconnect() {
        if (connexion != null) {
            logger.debug("Disconnecting sftp channel");
            connexion.disconnect();
        }
        if (channel != null) {
            logger.debug("Disconnecting channel");
            channel.disconnect();
        }
        if (session != null) {
            logger.debug("Disconnecting session");
            session.disconnect();
        }
    }
    
/*
    public static void main(String[] args) {
        SftpClient client = new SftpClient();
        client.setServer("localhost");
        client.setPort(22);
        client.setLogin("test");
        client.setPassword("testPassword");

        client.connect();

        try {
            client.uploadFile("src/main/resources/upload.txt", "/uploaded.txt");

            client.retrieveFile("/uploaded.txt", "target/downloaded.txt");
        } catch (InfinItException e) {
            logger.error("", e);
        } finally {
            client.disconnect();
        }
    }
*/
}

