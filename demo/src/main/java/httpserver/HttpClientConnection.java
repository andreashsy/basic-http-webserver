package httpserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;


public class HttpClientConnection implements Runnable {
    int port;
    Socket socket;
    List<String> resources;

    public HttpClientConnection (int port, Socket socket, List<String> resorces) {
        this.port = port;
        this.socket = socket;
        this.resources = resources;
    }

    @Override
    public void run() {
        System.out.println("Listening on " + port);

        try (OutputStream os = socket.getOutputStream(); InputStream is = socket.getInputStream()) {

            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);

            String line = dis.readUTF();
            String methodName = line.split(" ")[0];
            String resourceName = line.split(" ")[1];
            HttpWriter hw = new HttpWriter(dos);


            if (!(methodName == "GET")) {
                dos.writeUTF("HTTP/1.1 405 Method Not Allowed\\r\\n");
                dos.writeUTF("\\r\\n");
                dos.writeUTF("" + methodName + "not supported \\r\\n");
                socket.close();
            }

            if (!(resources.contains(resourceName))) {
                dos.writeUTF("HTTP/1.1 404 Not Found\\r\\n");
                dos.writeUTF("\\r\\n");
                dos.writeUTF("" + resourceName + "not found \\r\\n");
                socket.close();
            }



            if (resourceName.endsWith("\\.png")) {
                byte[] filecontent = Files.readAllBytes(path);
                dos.writeUTF("HTTP/1.1 200 OK\\r\\n");
                dos.writeUTF("Content-Type: image/png \\r\\n");
                dos.writeUTF("\\r\\n");
                dos.writeUTF(hw.writeBytes(filecontent));
            } else {
                byte[] filecontent = Files.readAllBytes(path);
                dos.writeUTF("HTTP/1.1 200 OK\\r\\n");
                dos.writeUTF("\\r\\n");
                dos.writeUTF(hw.writeBytes(filecontent));
            }

        
        
    }
    
    
}
