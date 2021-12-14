package httpserver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {


    public static void main( String[] args ) {

        System.out.println( "Hello World!" );
        List<String> docRoot = new ArrayList<>();
        int port = 3000;
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        List<String> resources = new ArrayList<String>();

        //parse command line args
        if (argList.contains("--docRoot")) {

        } else {
            docRoot.add("static");
        }
        if (argList.contains("--port")) {

        } 
        System.out.println("Doc root is: " + docRoot);
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
                    resources.add(path + "\\" + file.getName());
                }
            }
    
        }
        System.out.println(resources);
    }
}
