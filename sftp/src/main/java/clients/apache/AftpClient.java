package ab.clients.apache;

import org.apache.commons.net.ftp.FTPClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AftpClient extends ab.clients.apache.Common implements ab.Client {
    
    private final static Logger logger = LoggerFactory.getLogger(AftpClient.class);
    private FTPClient ftpClient = null;

    public AftpClient(String server, int port, String login, String password) {
        super(server, port, login, password);
    }

    /**
     * Connects to the server
     */
    public void connect() {
        logger.debug("Initializing Apache FTP client");
        ftpClient = new FTPClient();
        connecting(ftpClient);
    }

}
