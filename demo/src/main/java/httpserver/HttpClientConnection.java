package httpserver;

import java.io.*;
import java.net.Socket;


public class HttpClientConnection implements Runnable {
    int port;
    Socket socket;

    public HttpClientConnection (int port, Socket socket) {
        this.port = port;
        this.socket = socket;
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

            if (!line.startsWith("GET ")) {
                dos.writeUTF("HTTP/1.1 405 Method Not Allowed\\r\\n");
                dos.writeUTF("\\r\\n");
                dos.writeUTF("" + line.split(" ")[0] + "not supported \\r\\n");
                socket.close();
            }

            if () {
                dos.writeUTF("HTTP/1.1 404 Not Found\\r\\n");
                dos.writeUTF("\\r\\n");
                dos.writeUTF("" + line.split(" ")[1] + "not found \\r\\n");
                socket.close();
            }

        
        
    }
    
    
}
