package ab.clients.apache;

import org.apache.commons.net.ftp.FTPSClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpsClient extends ab.clients.apache.Common implements ab.Client {

    private final static Logger logger = LoggerFactory.getLogger(FtpsClient.class);

    public FtpsClient(String server, int port, String login, String password) {
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
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
        }
    }

}
