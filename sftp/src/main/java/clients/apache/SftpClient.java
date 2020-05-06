package ab.clients.apache;

import java.security.NoSuchAlgorithmException;
import org.apache.commons.net.ftp.FTPSClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SftpClient extends ab.clients.apache.Common implements ab.Client {

    private final static Logger logger = LoggerFactory.getLogger(SftpClient.class);

    public SftpClient(String server, int port, String login, String password) {
        super(server, port, login, password);
    }

    /**
     * Connects to the server
     */
    public void connect() {
        logger.debug("Initializing Apache SFTP client");
        try {
          FTPSClient ftpClient = new FTPSClient();
          connecting(ftpClient);
        } catch (NoSuchAlgorithmException e) {
          logger.error("NoSuchAlgorithm", e);
        }
    }

}
