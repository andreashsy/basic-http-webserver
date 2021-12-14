package httpserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;



public class HttpClientConnection implements Runnable {
    int port;
    Socket socket;
    List<String> resources;

    public HttpClientConnection (int port, Socket socket, List<String> resourceList) {
        this.port = port;
        this.socket = socket;
        this.resources = resourceList;
        System.out.println("HttpClientConnection initialized");
    }

    @Override
    public void run() {
        System.out.println("ClientConnection starting...");
        

        try (OutputStream os = socket.getOutputStream(); InputStream is = socket.getInputStream()) {

            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //String line = dis.readUTF();
            String line = in.readLine();
            System.out.println("Client said: " + line);
            if (line.indexOf(" ") == -1) {
                System.out.println("Invalid Argument");
                dos.writeUTF("Invalid Argument, closing connection...");
                dos.flush();
                socket.close();
            }
            String methodName = line.split(" ")[0];
            String resourceName = line.split(" ")[1];
            if (resourceName.equals("/")) {
                resourceName = "/index.html";
            }
            System.out.println("Method: " + methodName);
            System.out.println("Resource: " + resourceName);
            
            // if not a GET method
            if (!(methodName.equals("GET"))) {
                out.write("HTTP/1.1 405 Method Not Allowed\r\n");
                out.write("\r\n");
                out.write(methodName + " not supported \\r\\n");
                System.out.println("Method not supported, closing connection...");
                socket.close();
            }

            //get name of resources without path
            List<String> res = new ArrayList<String>();
            for (String resource:resources) {
                String[] rl = resource.split("/");
                String ele = "/" + rl[rl.length -1];
                res.add(ele);
            }
            System.out.println("Resource names: " + res);

            // if client requests a resource thats not found
            if (!(res.contains(resourceName))) {
                out.write("HTTP/1.1 404 Not Found\r\n");
                out.write("\r\n");
                out.write(resourceName + " not found \\r\\n");
                System.out.println("Resource not found, closing connection...");
                socket.close();
            }

            // get target resource path (default is "/")
            System.err.println(resourceName + " Found");
            Path targetResourcePath = Paths.get("/");
            for (String reso:resources) {
                if (reso.contains(resourceName)) {
                    targetResourcePath = Paths.get(reso);
                    System.out.println("Target Path Loaded: " + resourceName);
                    break;
                }
            }

           
            // if resource is PNG
            if (resourceName.endsWith("png")) {
                byte[] filecontent = Files.readAllBytes(targetResourcePath);
                System.out.println("Sending PNG...");
                out.write("HTTP/1.1 200 OK \r\n");
                out.write("Content-Type: image/png \\r\\n");
                out.write("\r\n");                     
                os.write(filecontent, 0, filecontent.length);
                os.flush();
               
            // if resource is not PNG
            } else {
                File file = new File(targetResourcePath.toString());
                BufferedReader reader = new BufferedReader(new FileReader(file));
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                // byte[] filecontent = Files.readAllBytes(targetResourcePath);
                System.out.println("Sending file...");
                out.write("HTTP/1.1 200 OK \r\n");
                out.write("\r\n");
                String l = reader.readLine();
                while (l != null) {
                    printWriter.println(line);// print current line
                    line = reader.readLine();// read next line
                }
                reader.close();
                printWriter.close();
                //os.write(filecontent, 0, filecontent.length);
                //os.flush();
            } 

            

            

    } catch (IOException ioe) {
        System.out.println(ioe);
    } catch (Exception e) {
        System.out.println(e);
    }
    
    
}

}
