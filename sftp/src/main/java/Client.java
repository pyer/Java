package ab;

import java.util.ArrayList;

public interface Client {
    
    public void connect();
    public void changeDirectory(String path);
    public ArrayList<String> listFiles(String path);
    public void uploadFile(String sourceFile, String destinationFile);
    public void retrieveFile(String sourceFile, String destinationFile);
    public void disconnect();

}

