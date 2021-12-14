package httpserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {


    public static void main( String[] args ) throws IOException{

        List<String> docRoot = new ArrayList<>();
        int port = 3000;
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        List<String> resources = new ArrayList<String>();

        //parse command line args
        if (argList.contains("--docRoot")) {
            int docIndex = argList.indexOf("--docRoot");
            String roots = argList.get(docIndex + 1);

            // if there's only 1 directory
            if (roots.indexOf(":") == -1) {
                docRoot.add(roots);
            
            // if there's more than 1 directory
            } else {
                for (String root:roots.split(":")) {
                    docRoot.add(root);
                }
            }
        // if docRoot not specified
        } else {
            docRoot.add("static");
        }
        // if port is specified (already initialized with value of 3000)
        if (argList.contains("--port")) {
            int portIndex = argList.indexOf("--port");
            port = Integer.valueOf(argList.get(portIndex + 1));
        } 
        System.out.println("Document root is: " + docRoot);
        System.out.println("Port is " + port);


        //docRoot check
        HttpServer server = new HttpServer();
        server.docRootCheck(docRoot);

        /* for (String path: docRoot) {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)){
                System.out.println("Path does not exist: " + path);
                System.exit(1);
            } else if (!Files.isDirectory(filePath)) {
                System.out.println("Path is not a directory: " + path);
                System.exit(1);
            } else if (false) { // TODO: path readable check
                System.out.println("Path is not readable: " + path);
                System.exit(1);
            }
        } */

        //get resources
        for (String path: docRoot) {
            File[] files = new File(path).listFiles();
            for (File file: files) {
                if (file.isFile()) {
                    resources.add(path + "/" + file.getName());
                }
            }
    
        }
        System.out.println("Resources: " + resources);

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        try (ServerSocket serversocket = new ServerSocket(port)) {

            while (true) {

                Socket socket = serversocket.accept();
                System.out.println("Client connected!");
                HttpClientConnection worker = new HttpClientConnection(port, socket, resources);
                threadPool.submit(worker);
                System.out.println("Worker submitted!");

            }
            
            
        }
   

        /* HttpClientConnection worker = new HttpClientConnection(port, socket, resources);
        Thread mythread = new Thread(worker);
        mythread.start(); */

/*     ExecutorService threadPool = Executors.newFixedThreadPool(3);
    ServerSocket serversocket = new ServerSocket(port);
    System.out.println("Threadpool created, waiting...");

    try {
        while (true) {
            Socket socket = serversocket.accept();
            System.out.println("Client connected...");
            HttpClientConnection worker = new HttpClientConnection(port, socket, resources);
            threadPool.submit(worker);
            System.out.println("Worker submitted!");
        }

    } finally {
        System.out.println("Closing sockets..");
        serversocket.close();
    } */

    }
}
