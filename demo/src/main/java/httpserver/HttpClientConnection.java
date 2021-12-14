package httpserver;

import java.io.*;
import java.net.Socket;
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
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());

            String line = in.readLine();
            System.out.println("Client said: " + line);
            if (line.indexOf(" ") == -1) {
                System.out.println("Invalid Argument");
                printWriter.write("Invalid Argument, closing connection...");
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
                printWriter.write("HTTP/1.1 405 Method Not Allowed\r\n");
                printWriter.write("\r\n");
                printWriter.write(methodName + " not supported \r\n");
                socket.close();
                throw new NoSuchMethodException(methodName + " not supported");
            }

            //get names of resources without path
            List<String> res = new ArrayList<String>();
            for (String resource:resources) {
                String[] rl = resource.split("/");
                String ele = "/" + rl[rl.length -1];
                res.add(ele);
            }
            System.out.println("Resource names: " + res);

            // if client requests a resource thats not found
            if (!(res.contains(resourceName))) {
                printWriter.write("HTTP/1.1 404 Not Found\r\n");
                printWriter.write("\r\n");
                printWriter.write(resourceName + " not found\r\n");
                socket.close();
                throw new FileNotFoundException(resourceName + " not found");
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
            File file = new File(targetResourcePath.toString());

            // if resource is PNG
            if (resourceName.endsWith("png")) {
                System.out.println("Sending PNG...");

                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
                DataOutputStream binaryOut = new DataOutputStream(os);              
                binaryOut.writeBytes("HTTP/1.1 200 OK \r\n");
                binaryOut.writeBytes("Content-Type: image/png \r\n");
                binaryOut.writeBytes("Content-Length: " + data.length);
                binaryOut.writeBytes("\r\n\r\n");
                binaryOut.write(data);
                printWriter.close();
                                   
            // if resource is not PNG
            } else {
                //File file = new File(targetResourcePath.toString());
                BufferedReader reader = new BufferedReader(new FileReader(file));
                System.out.println("Sending file...");
                printWriter.write("HTTP/1.1 200 OK \r\n");
                printWriter.write("\r\n");
                String l = reader.readLine();
                while (l != null) {
                    printWriter.println(l);// print current line
                    l = reader.readLine();// read next line
                }
                reader.close();
                printWriter.close();
            }
            

    } catch (IOException ioe) {
        System.out.println(ioe);
    } catch (Exception e) {
        System.out.println(e);
    }
    
    
}

}
