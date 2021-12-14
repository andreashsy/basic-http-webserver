package httpserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class HttpClientConnection implements Runnable {
    int port;
    Socket socket;
    List<String> resources;

    public HttpClientConnection (int port, Socket socket, List<String> resourceList) {
        this.port = port;
        this.socket = socket;
        this.resources = resourceList;
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
                hw.writeString("HTTP/1.1 405 Method Not Allowed\\r\\n");
                hw.writeString("\\r\\n");
                hw.writeString("" + methodName + "not supported \\r\\n");
                socket.close();
            }

            if (!(resources.contains(resourceName))) {
                hw.writeString("HTTP/1.1 404 Not Found\\r\\n");
                hw.writeString("\\r\\n");
                hw.writeString("" + resourceName + "not found \\r\\n");
                socket.close();
            }

            // get target resource path
            System.err.println(resourceName + " Found");
            Path targetResourcePath;
            for (String res:resources) {
                if (res.contains(resourceName)) {
                    targetResourcePath = Paths.get(res);
                    System.out.println("Target Path Loaded: " + resourceName);
                    break;
                }
            }

            // if resource is PNG
            if (resourceName.endsWith("\\.png")) {
                byte[] filecontent = Files.readAllBytes(targetResourcePath);
                dos.writeUTF("HTTP/1.1 200 OK\\r\\n");
                dos.writeUTF("Content-Type: image/png \\r\\n");
                dos.writeUTF("\\r\\n");
                hw.writeBytes(filecontent);
               
            // if resource is not PNG
            } else {
                byte[] filecontent = Files.readAllBytes(targetResourcePath);
                dos.writeUTF("HTTP/1.1 200 OK\\r\\n");
                dos.writeUTF("\\r\\n");
                hw.writeBytes(filecontent);
            }

    } catch (IOException ioe) {
        System.out.println(ioe);
    } catch (Exception e) {
        System.out.println(e);
    }
    
    
}

}
